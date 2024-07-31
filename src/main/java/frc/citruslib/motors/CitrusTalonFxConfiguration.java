package frc.citruslib.motors;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.Slot2Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import java.util.function.Function;

public class CitrusTalonFxConfiguration {
    private NeutralModeValue neutralMode;
    private InvertedValue invertedValue;

    private double supplyCurrentLimit;
    private double statorCurrentLimit;

    private boolean fwdSoftLimitEnabled;
    private double fwdSoftLimitThreshold;

    private boolean revSoftLimitEnabled;
    private double revSoftLimitThreshold;

    private CitrusPIDConfig slot0Conf = new CitrusPIDConfig();
    private CitrusPIDConfig slot1Conf = new CitrusPIDConfig();
    private CitrusPIDConfig slot2Conf = new CitrusPIDConfig();

    private double sensorUpdateFreq = 100; //hz

    public CitrusTalonFxConfiguration setBrakeMode() {
        neutralMode = NeutralModeValue.Brake;
        return this;
    }

    public CitrusTalonFxConfiguration setInvertedType(InvertedValue invertedValue) {
        this.invertedValue = invertedValue;
        return this;
    }

    public CitrusTalonFxConfiguration setSupplyCurrentLimit(final double supplyCurrentLimit) {
        this.supplyCurrentLimit = supplyCurrentLimit;
        return this;
    }

    public CitrusTalonFxConfiguration setStatorCurrentLimit(final double statorCurrentLimit) {
        this.statorCurrentLimit = statorCurrentLimit;
        return this;
    }

    public CitrusTalonFxConfiguration setFwdSoftLimit(final double fwdPos) {
        this.fwdSoftLimitEnabled = true;
        this.fwdSoftLimitThreshold = fwdPos;
        return this;
    }

    public CitrusTalonFxConfiguration setRevSoftLimit(final double revPos) {
        this.revSoftLimitEnabled = true;
        this.revSoftLimitThreshold = revPos;
        return this;
    }

    public CitrusTalonFxConfiguration setPIDConfig(final int slot, final CitrusPIDConfig conf) {
        switch (slot) {
            case 0:
                this.slot0Conf = conf;
                break;
            case 1:
                this.slot1Conf = conf;
                break;
            case 2:
                this.slot2Conf = conf;
            default:
                throw new RuntimeException(String.format("Invalid PID Slot Number: %s", slot));
        }
        return this;
    }

    public CitrusTalonFxConfiguration setSensorUpdateFreq(final double freq) {
        this.sensorUpdateFreq = freq;
        return this;
    }

    public TalonFXConfiguration toTalonFXConfiguration(final Function<Double, Double> toNativeSensorPos) {
        final TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput
                .withNeutralMode(this.neutralMode)
                .withInverted(this.invertedValue)
                .withDutyCycleNeutralDeadband(0.0);

        config.SoftwareLimitSwitch
                .withForwardSoftLimitEnable(this.fwdSoftLimitEnabled)
                .withForwardSoftLimitThreshold(toNativeSensorPos.apply(this.fwdSoftLimitThreshold))
                .withReverseSoftLimitEnable(this.revSoftLimitEnabled)
                .withReverseSoftLimitThreshold(toNativeSensorPos.apply(this.revSoftLimitThreshold));

        config.CurrentLimits
                .withStatorCurrentLimitEnable(true)
                .withStatorCurrentLimit(this.statorCurrentLimit)
                .withSupplyCurrentLimitEnable(true)
                .withSupplyCurrentLimit(this.supplyCurrentLimit)
                .withSupplyCurrentThreshold(this.supplyCurrentLimit)
                .withSupplyTimeThreshold(0.1);

        config.Voltage
                .withSupplyVoltageTimeConstant(0.0)
                .withPeakForwardVoltage(16.0)
                .withPeakReverseVoltage(-16.0);

        config
                .withSlot0(slot0Conf.fillCTRE(new Slot0Configs()))
                .withSlot1(slot1Conf.fillCTRE(new Slot1Configs()))
                .withSlot2(slot2Conf.fillCTRE(new Slot2Configs()));

        return config;
    }

    public static CitrusTalonFxConfiguration makeDefaultConfiguration() {
        return new CitrusTalonFxConfiguration();
    }

}
