package frc.citruslib.motors;

import com.ctre.phoenix6.signals.InvertedValue;

public interface CitrusMotorControllerWithEncoder {

    /** @return CAN ID of the device */
    public int getDeviceId();

    /** Configures the motor and should be called on construction */
    public void setConfiguration();

    // ====== Motor Setters ======
    /** @param percentOutput Motor output [-1.0 to 1.0] */
    public void setPercentOutput(double percentOutput);

    /** @param voltageOutput Motor output [-12.0 VDC to 12.0 VDC] */
    public void setVoltageOutput(double voltageOutput);

    /** @param slot Index of slot on motor controller [0, 1, 2] are valid
     *  @param setpoint Position in encoder units to move the mechanism */
    public void setPositionSetpoint(int slot, double setpoint);

    /** @param slot Index of slot on motor controller [0, 1, 2] are valid
     *  @param setpoint Position in native units to move the mechanism
     *  @param feedForwardVolts Base voltage to get close to most positions */
    public void setPositionSetpoint(int slot, double setpoint, double feedForwardVolts);

    /** @param slot Index of slot on motor controller [0, 1, 2] are valid
     *  @param setpoint Velocity in native units */
    public void setVelocitySetpoint(int slot, double setpoint);

    /** @param slot Index of slot on motor controller [0, 1, 2] are valid
     *  @param setpoint Velocity in native units
     *  @param feedForwardVolts Base voltage to get close to the setpoint */
    public void setVelocitySetpoint(int slot, double setpoint, double feedForwardVolts);

    // ====== Motor Getters ======
    /** @return Percent output [-1.0 to 1.0] as reported by the device */
    public double getPercentOutput();

    /** @return Percent output [-1.0 to 1.0] as reported by the device. Sign
     * corresponds with directionality of the motor */
    public double getPhysicalPercentOutput();

    /** @return Stator current draw in amps */
    public double getStatorCurrent();

    /** @return Supply current draw in amps as reported by the device */
    public double getSupplyCurrent();

    /** @return Inversion value for motor direction */
    public InvertedValue getInverted();

    // ====== Sensor Setters ======
    /** Sets the sensor position to zero at the current position */
    public void zeroSensorPosition();

    /** Sets the sensor position to a specific native unit value at the current position
     * @param pos Native unit position  */
    public void setSensorPosition(double pos);

    // ====== Sensor Getters ======
    /** @return Position in native units */
    public double getSensorPosition();

    /** @return Velocity in native units */
    public double getSensorVelocity();

    /** @return Position in mechanism units */
    public double getMechPosition(double ratio);

    /** @return Velocity in mechanism units */
    public double getMechVelocity(double ratio);

    // ====== Unit Conversions ======
    /** Converts mechanism position to native sensor units
     * @param pos Mechanism units
     * @param ratio Mechanism ratio
     * @return Native sensor units
     */
    public double toNativeSensorPosition(double pos, double ratio);

    /**
     * Converts native units to mechanism position
     * @param pos Native units
     * @param ratio Mechanism ratio
     * @return Mechanism units
     */
    public double fromNativeSensorPosition(double pos, double ratio);

    /**
     * Converts mechanism velocity to native sensor velocity
     * @param vel Mechanism units
     * @param ratio Mechanism ratio
     * @return Native sensor units
     */
    public double toNativeSensorVelocity(double vel, double ratio);

    /**
     * Converts native sensor velocity to mechanism velocity
     * @param vel Native units
     * @param ratio Mechanism ratio
     * @return Mechanism units
     */
    public double fromNativeSensorVelocity(double vel, double ratio);

    // ====== Simulation ======

    /**
     * Sets the simulated sensor position in mechanism units
     * @param pos Mechanism units
     * @param dt Delta time in seconds
     * @param ratio Mechanism ratio
     */
    public void setSimSensorPosition(double pos, double dt, double ratio);

    /**
     * Sets the simulated sensor velocity in mechanism units
     * @param vel Mechanism units
     * @param dt Delta time in seconds
     * @param ratio Mechanism ratio
     */
    public void setSimSensorVelocity(double vel, double dt, double ratio);

}
