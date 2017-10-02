package org.multieditor.probe;

import java.util.Arrays;
import java.util.List;

public final class ProfilerUtils {

    protected ProfilerUtils() {
        // prevent instantiation
    }

    /**
     * Joins multiple profiles into one.
     *
     * @param profiles profiles to join
     * @return aggregated profile
     * @see #aggregate(java.util.List)
     */
    public static Profile aggregate(Profile... profiles) {
        return aggregate(Arrays.asList(profiles));
    }

    /**
     * Joins multiple profiles into one.
     *
     * @param profiles profiles to join
     * @return aggregated profile
     * @see #aggregate(Profile...)
     */
    public static Profile aggregate(List<Profile> profiles) {
        ProfileImpl result = new ProfileImpl();

        for (Profile profile : profiles) {
            for (String key : profile.getEvents()) {
                result.addSampleCount(key, profile.getSampleCount(key));
                result.addLength(key, profile.getLength(key));
            }
        }
        return result;
    }

    /**
     * Compares two profiles and returns the comparison.
     * It's useful to see the rate of change in event count over the time.
     *
     * @param baseline  baseline profile
     * @param specimen  specimen profile
     * @param timeMsecs msecs spent between profile snapshots
     * @return
     */
    public static String compare(Profile baseline, Profile specimen, long timeMsecs) {
        if (baseline == null || specimen == null) {
            return "N/A\n";
        }

        String result = "";
        for (String event : baseline.getEvents()) {
            long difference = specimen.getSampleCount(event) - baseline.getSampleCount(event);

            if (timeMsecs != 0) {
                long eventsPerMinute = difference * 60 * 1000 / timeMsecs;
                result += event + " = " + eventsPerMinute + " events per minute\n";
            } else {
                result += event + " = " + difference + " events\n";
            }
        }
        return result;
    }

}
