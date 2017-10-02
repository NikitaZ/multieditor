package org.multieditor.probe;

import java.util.Collection;

public interface Profile {

    Long getSampleCount(String event);

    Long getLength(String key);

    Collection<String> getEvents();

    void clear();

    void setStartTime(Long startTime);

    Long getStartTime();

}
