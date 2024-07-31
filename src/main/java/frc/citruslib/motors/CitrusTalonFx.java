package frc.citruslib.motors;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;
import frc.citruslib.devices.CANDevice;

public class CitrusTalonFx implements AutoCloseable {

    private static final double CANShortTimeout = 10; //ms
    private static final double CANLongTimeout = 100; //ms
    private static final double defaultUpdateFreq = 100; //hz

    private final CANDevice CANId;
    private final TalonFX controller;
    private final TalonFXSimState simState;
    private final CitrusTalonFxConfiguration config;


    @Override
    public void close() throws Exception {

    }
}
