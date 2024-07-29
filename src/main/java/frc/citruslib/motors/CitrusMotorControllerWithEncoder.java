package frc.citruslib.motors;

public interface CitrusMotorControllerWithEncoder {

    // Returns the CAN ID of the device
    public int getDeviceId();

    /**
     * Configures the motor with settings. Call on construction
     */
    public void setConfiguration();

    // ====== Motor Setters ======

}
