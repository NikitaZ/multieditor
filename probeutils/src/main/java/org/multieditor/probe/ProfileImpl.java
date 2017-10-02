package org.multieditor.probe;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Profile data.
 */
public class ProfileImpl implements Profile {

    private final Map<String, Long> samples;

    private final Map<String, Long> total;

    private Long startTime;

    public ProfileImpl() {
        total = new TreeMap<String, Long>();
        samples = new TreeMap<String, Long>();
        startTime = System.currentTimeMillis();
    }

    /**
     * Answers sample count for event
     *
     * @param event named event
     * @return sample count
     */
    @Override
    public Long getSampleCount(String event) {
        Long value = samples.get(event);
        return value == null ? 0L : value;
    }

    public void add(String event, long l) {
        addSampleCount(event, 1L);
        addLength(event, l);
    }

    public void add(String event) {
        addSampleCount(event, 1L);
    }

    @Override
    public void clear() {
        samples.clear();
        total.clear();
        setStartTime(System.currentTimeMillis());
    }

    @Override
    public Collection<String> getEvents() {
        return Collections.unmodifiableSet(samples.keySet());
    }

    @Override
    public Long getLength(String key) {
        Long value = total.get(key);
        return value == null ? 0L : value;
    }

    public void addSampleCount(String key, Long sampleCount) {
        Long sample = samples.get(key);
        if (sample == null) {
            sample = 0L;
        }
        sample += sampleCount;
        samples.put(key, sample);
    }

    public void addLength(String event, long len) {
        Long subTotal = total.get(event);
        if (subTotal == null) {
            subTotal = 0L;
        }
        subTotal += len;
        total.put(event, subTotal);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Profile Start at: ").append(new Date(getStartTime())).append(" [")
                .append((System.currentTimeMillis() - getStartTime()) / 1000).append("] seconds ago\n\n");
        for (String event : samples.keySet()) {
            Long totalSamples = samples.get(event);
            Long totalTime = total.get(event);

            result.append(event).append(" = ");
            if (totalTime != null && totalTime != 0) {
                Long timePerSample = (totalSamples != 0) ? totalTime / totalSamples : -1;
                result.append(totalTime).append(" ms (").append(totalSamples).append(" samples, ").append(timePerSample).append(" ms/sample)\n");
            } else {
                result.append(totalSamples).append(" samples\n");
            }
        }
        return result.toString();
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public boolean isEmpty() {
        return samples.isEmpty();
    }
}
