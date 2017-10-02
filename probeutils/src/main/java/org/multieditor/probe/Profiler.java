package org.multieditor.probe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal profiler.
 * - stopwatch mode (measuring events which span some interval)
 * - probe mode (measuring events which have no time interval)
 * <p>
 * This implementation is NOT thread-safe.
 */
public class Profiler {

    private static final Long NOT_STARTED = new Long(Long.MIN_VALUE);

    private ProfileImpl profile;

    private final Map<String, Long> started;

    public Profiler() {
        this(new ProfileImpl());
    }

    public Profiler(ProfileImpl profile) {
        this.profile = profile;
        this.started = new HashMap<>();
    }

    /**
     * Notifies profiler that named event had started.
     *
     * @param event event to account
     */
    public void start(String event) {
        started.put(event, System.currentTimeMillis());
    }

    /**
     * Notifies profiler that named event had stopped
     *
     * @param event event to account
     */
    public void stop(String event) {
        Long start = started.get(event);
        if (start != null && start != NOT_STARTED) {
            profile.add(event, System.currentTimeMillis() - start);
            started.put(event, NOT_STARTED);
        }
    }

    /**
     * Notifies profiler the probe event had happened
     *
     * @param event event to account
     */
    public void probe(String event) {
        profile.add(event);
    }

    /**
     * Notify all pending named events have stopped.
     */
    public void stopAll() {
        List<String> startedEvents = new ArrayList<>(started.keySet());
        for (String event : startedEvents) {
            stop(event);
        }
        started.clear();
    }

    /**
     * Get the profile
     *
     * @return accumulated profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Clear the profile
     */
    public void clear() {
        started.clear();
        profile.clear();
    }

    public boolean isEmpty() {
        return profile.isEmpty();
    }
}
