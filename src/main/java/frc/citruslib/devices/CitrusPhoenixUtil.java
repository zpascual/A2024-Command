package frc.citruslib.devices;

import com.ctre.phoenix6.StatusCode;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.function.Supplier;

public class CitrusPhoenixUtil {
    /**
     * Checks the status code and outputs to the driver station if the status code is not ok
     * @param statusCode Status code from a phoeinx function return
     * @param message User inputted message
     */
    public static void checkError(StatusCode statusCode, String message) {
        if (!statusCode.isOK()) {
            DriverStation.reportError(String.format("%s %d", message, statusCode.value), false);
        }
    }

    /**
     * Allows retries for setting a config on the device.
     * @param function Motor controller config function
     * @param attempts Quantity of retries allowed
     * @return If the config was set successfully in the quantity of retries allowed.
     */
    public static boolean checkErrorAndRetry(Supplier<StatusCode> function, int attempts) {
        StatusCode statusCode = function.get();
        int attempt = 0;
        while (!statusCode.isOK() && attempt < attempts) {
            DriverStation.reportWarning(String.format("Retrying CTRE Device Config %s", statusCode.getName()), false);
            statusCode = function.get();
            attempt++;
        }
        if (!statusCode.isOK()) {
            DriverStation.reportError(String.format("Failed to execute phoenix api call after %d attempts", attempt), false);
            return false;
        }
        return true;
    }

    /**
     * Checks the status code to verify that the config was set properly otherwise it will throw an exception
     * @param statusCode Status code from motor config function
     * @param message User generated message
     */
    public static void checkErrorWithThrow(StatusCode statusCode, String message) {
        if (!statusCode.isOK()) {
            throw new RuntimeException(String.format("%s %d", message, statusCode.value));
        }
    }

    /**
     * Attempts 5 times successfully apply the motor config
     * @param function Motor controller config function
     * @return If the config was set successfully in 5 attempts
     */
    public static boolean checkErrorAndRetry(Supplier<StatusCode> function) {
        return checkErrorAndRetry(function, 5);
    }


}
