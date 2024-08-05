package frc.citruslib.devices;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.Timestamp;

import java.util.function.Function;

public class CitrusStatusSignal {
    private final StatusSignal<Double> statusSignal;
    private final Function<Double, Double> fromNativeUnits;

    public CitrusStatusSignal(final StatusSignal<Double> statusSignal, final Function<Double, Double> fromNativeUnits) {
        this.statusSignal = statusSignal.clone();
        this.fromNativeUnits = fromNativeUnits;
    }

    public CitrusStatusSignal(final StatusSignal<Double> statusSignal) {
        this.statusSignal = statusSignal.clone();
        this.fromNativeUnits = (Double value) -> value;
    }

    public void setUpdateFrequency(double freqHz, double timeoutSeconds) {
        CitrusPhoenixUtil.checkErrorAndRetry(() -> statusSignal.setUpdateFrequency(freqHz, timeoutSeconds));
    }

    public double getAppliedUpdateFrequency() {
        return statusSignal.getAppliedUpdateFrequency();
    }

    public void refresh() {
        CitrusPhoenixUtil.checkErrorAndRetry(() -> statusSignal.refresh().getStatus());
    }

    public void waitForUpdate(final double timeoutSeconds) {
        CitrusPhoenixUtil.checkErrorAndRetry(() -> statusSignal.waitForUpdate(timeoutSeconds).getStatus());
    }

    public double getValue() {
        return fromNativeUnits.apply(statusSignal.getValue());
    }

    public Timestamp getTimestamp() {
        return statusSignal.getTimestamp();
    }

    public static boolean waitForAll(final double timeoutSeconds, final CitrusStatusSignal... citrusStatusSignals) {
        final BaseStatusSignal[] statusSignals = new BaseStatusSignal[citrusStatusSignals.length];
        for (int i = 0; i < citrusStatusSignals.length; i++) {
            statusSignals[i] = citrusStatusSignals[i].statusSignal;
        }
        return CitrusPhoenixUtil.checkErrorAndRetry(() -> BaseStatusSignal.waitForAll(timeoutSeconds, statusSignals));
    }

    public static double getLatencyCompensatedValue(final CitrusStatusSignal statusSignal, final CitrusStatusSignal signalSlope) {
        return statusSignal.getValue() + (signalSlope.getValue() * statusSignal.getTimestamp().getLatency());
    }

}
