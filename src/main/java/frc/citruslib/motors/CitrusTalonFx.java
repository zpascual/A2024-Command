package frc.citruslib.motors;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.wpilibj.DriverStation;
import frc.citruslib.Util;
import frc.citruslib.devices.CANDevice;
import frc.citruslib.devices.CitrusPhoenixUtil;
import frc.citruslib.devices.CitrusStatusSignal;

public class CitrusTalonFx implements CitrusMotorControllerWithEncoder, AutoCloseable {

    private static final double CANShortTimeout = 50; //ms
    private static final double CANLongTimeout = 100; //ms
    private static final double defaultUpdateFreq = 100; //hz

    private final CANDevice CANId;
    private final TalonFX controller;
    private final TalonFXSimState simState;
    private final double ratio;
    private final CitrusTalonFxConfiguration config;

    private final DutyCycleOut dutyCycleControl = new DutyCycleOut(0);
    private final VoltageOut voltageControl = new VoltageOut(0);
    private final VelocityVoltage velocityVoltageControl = new VelocityVoltage(0);
    private final PositionVoltage positionVoltageControl = new PositionVoltage(0);
    private final MotionMagicExpoVoltage motionMagicExpoVoltageControl = new MotionMagicExpoVoltage(0);
    private final MotionMagicVelocityVoltage motionMagicVelocityVoltageControl = new MotionMagicVelocityVoltage(0);

    private final CitrusStatusSignal percentOutputSignal;
    private final CitrusStatusSignal sensorPositionSignal;
    private final CitrusStatusSignal sensorVelocitySignal;

    public CitrusTalonFx(final CANDevice canID, final double ratio, final CitrusTalonFxConfiguration config) {
        this.CANId = canID;
        this.controller = new TalonFX(canID.deviceNumber, canID.CANBusName);
        this.simState = this.controller.getSimState();
        this.ratio = ratio;
        this.config = config;

        this.percentOutputSignal = new CitrusStatusSignal(this.controller.getDutyCycle());
        this.sensorPositionSignal = new CitrusStatusSignal(this.controller.getRotorPosition(), this::fromNativeSensorPosition);
        this.sensorVelocitySignal = new CitrusStatusSignal(this.controller.getRotorVelocity(), this::fromNativeSensorVelocity);

        this.controller.hasResetOccurred();

        setConfiguration();

        proLicenseCheck();
    }

    public void proLicenseCheck() {
        if (controller.getFault_UnlicensedFeatureInUse().getValue()) {
            throw new RuntimeException(String.format("TalonFX %s: No pro license found and utilizing pro features", this.CANId));
        }
    }

    public boolean checkFaultsAndReconfigure() {
        if (this.controller.hasResetOccurred()) {
            DriverStation.reportError(String.format("TalonFX %s: reset occurred", this.CANId), false);
            setConfiguration();
            return true;
        }
        return false;
    }

    public CitrusStatusSignal getPercentOutputSignal() {
        return percentOutputSignal;
    }

    public CitrusStatusSignal getSensorPositionSignal() {
        return sensorPositionSignal;
    }

    public CitrusStatusSignal getSensorVelocitySignal() {
        return sensorVelocitySignal;
    }

    @Override
    public void close() {
        this.controller.close();
    }

    /**
     * @return CAN ID of the device
     */
    @Override
    public int getDeviceId() {
        return this.controller.getDeviceID();
    }

    /**
     * Configures the motor and should be called on construction
     */
    @Override
    public void setConfiguration() {
        // Motor Config
        final TalonFXConfiguration config = this.config.toTalonFXConfiguration(this::toNativeSensorPosition);
        CitrusPhoenixUtil.checkErrorAndRetry(
                () -> this.controller.getConfigurator().apply(config, CANLongTimeout)
        );
        System.out.printf("TalonFX: %s: Apply Configuration%n", this.CANId);

        // Set Update Frequencies
        percentOutputSignal.setUpdateFrequency(this.config.getSensorUpdateFreq(), CANShortTimeout);
        System.out.printf("TalonFX: %s: Percent output update frequency done", this.CANId);
        sensorPositionSignal.setUpdateFrequency(this.config.getSensorUpdateFreq(), CANShortTimeout);
        System.out.printf("TalonFX: %s: Sensor position update frequency done", this.CANId);
        sensorVelocitySignal.setUpdateFrequency(this.config.getSensorUpdateFreq(), CANShortTimeout);
        System.out.printf("TalonFX: %s: Sensor velocity update frequency done", this.CANId);

        // Disable all signals not explicitly defined
        CitrusPhoenixUtil.checkErrorAndRetry(this.controller::optimizeBusUtilization);
        System.out.printf("TalonFX: %s: Optimizing bus utilization", this.CANId);

        // Block all other actions until we get valid signals
        CitrusStatusSignal.waitForAll(
                CANShortTimeout,
                percentOutputSignal,
                sensorPositionSignal,
                sensorVelocitySignal
        );
        System.out.printf("TalonFx %s: Wait for all signals to come back online after configuration", this.CANId);
    }

    /**
     * @param percentOutput Motor output [-1.0 to 1.0]
     */
    @Override
    public void setPercentOutput(double percentOutput) {
        this.dutyCycleControl.Output = Util.limit(percentOutput, -1.0, 1.0);;
        this.controller.setControl(this.dutyCycleControl);
    }

    /**
     * @param voltageOutput Motor output [-12.0 VDC to 12.0 VDC]
     * @param synchronous Will send request immediately. Useful for syncing outputs for logging
     */
    public void setVoltageOutput(double voltageOutput, boolean synchronous) {
        this.voltageControl.UpdateFreqHz = synchronous ? 0.0 : this.config.getSensorUpdateFreq();
        this.voltageControl.Output = Util.limit(voltageOutput, -12.0, 12.0);
        this.controller.setControl(this.voltageControl);
    }

    /**
     * @param voltageOutput Motor output [-12.0 VDC to 12.0 VDC]
     */
    @Override
    public void setVoltageOutput(double voltageOutput) {

    }

    /**
     * @param slot     Index of slot on motor controller [0, 1, 2] are valid
     * @param setpoint Position in encoder units to move the mechanism
     */
    @Override
    public void setPositionSetpoint(int slot, double setpoint) {

    }

    /**
     * @param slot             Index of slot on motor controller [0, 1, 2] are valid
     * @param setpoint         Position in native units to move the mechanism
     * @param feedForwardVolts Base voltage to get close to most positions
     */
    @Override
    public void setPositionSetpoint(int slot, double setpoint, double feedForwardVolts) {

    }

    /**
     * @param slot     Index of slot on motor controller [0, 1, 2] are valid
     * @param setpoint Velocity in native units
     */
    @Override
    public void setVelocitySetpoint(int slot, double setpoint) {

    }

    /**
     * @param slot             Index of slot on motor controller [0, 1, 2] are valid
     * @param setpoint         Velocity in native units
     * @param feedForwardVolts Base voltage to get close to the setpoint
     */
    @Override
    public void setVelocitySetpoint(int slot, double setpoint, double feedForwardVolts) {

    }

    /**
     * @return Percent output [-1.0 to 1.0] as reported by the device
     */
    @Override
    public double getPercentOutput() {
        return 0;
    }

    /**
     * @return Percent output [-1.0 to 1.0] as reported by the device. Sign
     * corresponds with directionality of the motor
     */
    @Override
    public double getPhysicalPercentOutput() {
        return 0;
    }

    /**
     * @return Stator current draw in amps
     */
    @Override
    public double getStatorCurrent() {
        return 0;
    }

    /**
     * @return Supply current draw in amps as reported by the device
     */
    @Override
    public double getSupplyCurrent() {
        return 0;
    }

    /**
     * @return Inversion value for motor direction
     */
    @Override
    public InvertedValue getInverted() {
        return null;
    }

    /**
     * Sets the sensor position to zero at the current position
     */
    @Override
    public void zeroSensorPosition() {

    }

    /**
     * Sets the sensor position to a specific native unit value at the current position
     *
     * @param pos Native unit position
     */
    @Override
    public void setSensorPosition(double pos) {

    }

    /**
     * @return Position in native units
     */
    @Override
    public double getSensorPosition() {
        return 0;
    }

    /**
     * @return Velocity in native units
     */
    @Override
    public double getSensorVelocity() {
        return 0;
    }

    /**
     * @param ratio
     * @return Position in mechanism units
     */
    @Override
    public double getMechPosition(double ratio) {
        return 0;
    }

    /**
     * @param ratio
     * @return Velocity in mechanism units
     */
    @Override
    public double getMechVelocity(double ratio) {
        return 0;
    }

    /**
     * Converts mechanism position to native sensor units
     *
     * @param pos   Mechanism units
     * @param ratio Mechanism ratio
     * @return Native sensor units
     */
    @Override
    public double toNativeSensorPosition(double pos, double ratio) {
        return 0;
    }

    /**
     * Converts native units to mechanism position
     *
     * @param pos   Native units
     * @param ratio Mechanism ratio
     * @return Mechanism units
     */
    @Override
    public double fromNativeSensorPosition(double pos, double ratio) {
        return 0;
    }

    /**
     * Converts mechanism velocity to native sensor velocity
     *
     * @param vel   Mechanism units
     * @param ratio Mechanism ratio
     * @return Native sensor units
     */
    @Override
    public double toNativeSensorVelocity(double vel, double ratio) {
        return 0;
    }

    /**
     * Converts native sensor velocity to mechanism velocity
     *
     * @param vel   Native units
     * @param ratio Mechanism ratio
     * @return Mechanism units
     */
    @Override
    public double fromNativeSensorVelocity(double vel, double ratio) {
        return 0;
    }

    /**
     * Sets the simulated sensor position in mechanism units
     *
     * @param pos   Mechanism units
     * @param dt    Delta time in seconds
     * @param ratio Mechanism ratio
     */
    @Override
    public void setSimSensorPosition(double pos, double dt, double ratio) {

    }

    /**
     * Sets the simulated sensor velocity in mechanism units
     *
     * @param vel   Mechanism units
     * @param dt    Delta time in seconds
     * @param ratio Mechanism ratio
     */
    @Override
    public void setSimSensorVelocity(double vel, double dt, double ratio) {

    }
}
