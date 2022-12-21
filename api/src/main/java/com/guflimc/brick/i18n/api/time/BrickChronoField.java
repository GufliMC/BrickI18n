package com.guflimc.brick.i18n.api.time;

import java.time.temporal.*;

import static java.time.temporal.ChronoUnit.*;

public enum BrickChronoField implements TemporalField {
    /**
     * Represents the total amount of years in a {@link TemporalAmount}.
     */
    YEAR(BrickChronoUnit.YEARS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the total amount of months in a {@link TemporalAmount}.
     */
    MONTH(BrickChronoUnit.MONTHS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of months that do not make up for a whole year in a {@link TemporalAmount}.
     */
    MONTH_REMAINDER(BrickChronoUnit.MONTHS, BrickChronoUnit.YEARS, ValueRange.of(0, 11)),
    /**
     * Represents the total amount of days in a {@link TemporalAmount}.
     * **/
    DAY(DAYS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of days that do not make up for a whole month in a {@link TemporalAmount}.
     */
    DAY_REMAINDER(DAYS, BrickChronoUnit.MONTHS, ValueRange.of(0, 29)),
    /**
     * Represents the total amount of hours in a {@link TemporalAmount}.
     * **/
    HOUR(HOURS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of hours that do not make up for a whole day in a {@link TemporalAmount}.
     */
    HOUR_REMAINDER(HOURS, DAYS, ValueRange.of(0, 23)),
    /**
     * Represents the total amount of minutes in a {@link TemporalAmount}.
     */
    MINUTE(MINUTES, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of minutes that do not make up for a whole hour in a {@link TemporalAmount}.
     */
    MINUTE_REMAINDER(MINUTES, HOURS, ValueRange.of(0, 59)),
    /**
     * Represents the total amount of seconds in a {@link TemporalAmount}.
     */
    SECOND(SECONDS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of seconds that do not make up for a whole minute in a {@link TemporalAmount}.
     */
    SECOND_REMAINDER(SECONDS, MINUTES, ValueRange.of(0, 59)),
    /**
     * Represents the total amount of milliseconds in a {@link TemporalAmount}.
     */
    MILLISECOND(MILLIS, FOREVER, ValueRange.of(0, Long.MAX_VALUE)),
    /**
     * Represents the remaining amount of milliseconds that do not make up for a whole second in a {@link TemporalAmount}.
     */
    MILLISECOND_REMAINDER(MILLIS, SECONDS, ValueRange.of(0, 999));

    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;

    BrickChronoField(TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range = range;
    }

    @Override
    public TemporalUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public TemporalUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public ValueRange range() {
        return range;
    }

    @Override
    public boolean isDateBased() {
        return baseUnit.isDateBased();
    }

    @Override
    public boolean isTimeBased() {
        return baseUnit.isTimeBased();
    }

    @Override
    public boolean isSupportedBy(TemporalAccessor temporal) {
        return temporal.isSupported(this);
    }

    @Override
    public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
        return temporal.range(this);
    }

    @Override
    public long getFrom(TemporalAccessor temporal) {
        return temporal.getLong(this);
    }

    @Override
    public <R extends Temporal> R adjustInto(R temporal, long newValue) {
        return (R) temporal.with(this, newValue);
    }
}
