package com.guflimc.brick.i18n.api.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public enum BrickChronoUnit implements TemporalUnit {
    /**
     * This unit replaces {@link ChronoUnit#YEARS} with a fixed length of 360 days.
     * This is used when parsing a duration and during the formatting of a duration.
     */
    YEARS(Duration.ofDays(360)),
    /**
     * This unit replaces {@link ChronoUnit#MONTHS} with a fixed length of 30 days.
     * This is used when parsing a duration and during the formatting of a duration.
     */
    MONTHS(Duration.ofDays(30));

    private final Duration duration;

    BrickChronoUnit(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Although the duration of these units is not accurate, e.g. due leap years, we do return false here
     * because these units are meant to be used as if their durations were accurate.
     * @return false
     */
    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    @Override
    public boolean isDateBased() {
        return true;
    }

    @Override
    public boolean isTimeBased() {
        return false;
    }

    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) duration.multipliedBy(amount).addTo(temporal);
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }
}
