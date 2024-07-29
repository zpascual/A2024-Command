package frc.citruslib.devices;

public class CANDevice {

    public static final String kRIOCANBusName = "rio";

    public final int deviceNumber;
    public final String CANBusName;

    public CANDevice(final int deviceNumber, final String canBusName) {
        this.deviceNumber = deviceNumber;
        this.CANBusName = canBusName;
    }

    public CANDevice(final int deviceNumber) {
        this(deviceNumber, kRIOCANBusName);
    }

    public String toString() {
        return String.format("[%s:%d]", CANBusName, deviceNumber);
    }
}
