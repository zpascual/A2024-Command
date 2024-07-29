package frc.robot;

import frc.citruslib.devices.CANDevice;

public class Ports {
    private static final String CanivoreName = "Canivore";

    public static final CANDevice IntakeRollerId = new CANDevice(8, CanivoreName);
    public static final CANDevice IntakePivotId = new CANDevice(9, CanivoreName);

    public static final CANDevice ShooterPrimaryId = new CANDevice(10, CanivoreName);
    public static final CANDevice ShooterFollowerId = new CANDevice(11, CanivoreName);

    public static final CANDevice AcceleratorId = new CANDevice(12, CanivoreName);

    public static final CANDevice PigeonId = new CANDevice(20, CanivoreName);
}
