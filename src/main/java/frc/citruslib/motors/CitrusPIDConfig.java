package frc.citruslib.motors;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.Slot2Configs;

public class CitrusPIDConfig {

    public final double kP;
    public final double kI;
    public final double kD;
    public final double kS;
    public final double kV;
    public final double kA;
    public final double kG;

    /**
     * Config PID values
     * @param kP output per unit of error | position: (output/(rot)) - velocity (output/rps)
     * @param kI output per unit of integrated error | position: (output/(rot*s) - velocity (output/rot)
     * @param kD output per unit of error derivative | position: (output/(rps)) - velocity (output/(rps/s))
     * @param kS output to overcome static friction (output)
     * @param kV velocity only: output per unit of requested velocity (output/rps)
     * @param kA motion magic only: output per unit of target accel - (output/(rps/s))
     * @param kG output to overcome gravity (output)
     */
    public CitrusPIDConfig(final double kP, final double kI, final double kD, final double kS, final double kV, final double kA, final double kG) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;
        this.kG = kG;
    }

    /**
     * Config PID values
     * @param kP output per unit of error | position: (output/(rot)) - velocity (output/rps)
     * @param kI output per unit of integrated error | position: (output/(rot*s) - velocity (output/rot)
     * @param kD output per unit of error derivative | position: (output/(rps)) - velocity (output/(rps/s))
     */
    public CitrusPIDConfig(final double kP, final double kI, final double kD) {
        this(kP, kI, kD, 0, 0, 0, 0);
    }

    /**
     * Empty Config
     */
    public CitrusPIDConfig() {
        this(0, 0, 0);
    }

    public Slot0Configs fillCTRE(Slot0Configs conf) {
        conf.kP = this.kP;
        conf.kI = this.kI;
        conf.kD = this.kD;
        conf.kS = this.kS;
        conf.kV = this.kV;
        conf.kA = this.kA;
        conf.kG = this.kG;
        return conf;
    }

    public Slot1Configs fillCTRE(Slot1Configs conf) {
        conf.kP = this.kP;
        conf.kI = this.kI;
        conf.kD = this.kD;
        conf.kS = this.kS;
        conf.kV = this.kV;
        conf.kA = this.kA;
        conf.kG = this.kG;
        return conf;
    }

    public Slot2Configs fillCTRE(Slot2Configs conf) {
        conf.kP = this.kP;
        conf.kI = this.kI;
        conf.kD = this.kD;
        conf.kS = this.kS;
        conf.kV = this.kV;
        conf.kA = this.kA;
        conf.kG = this.kG;
        return conf;
    }

}
