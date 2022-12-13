package com.guflimc.brick.i18n.api.time;

import java.time.Year;
import java.time.temporal.*;

import static java.time.temporal.ChronoUnit.*;

public enum ExtendedChronoField implements TemporalField {
    MONTH(MONTHS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    WEEK(WEEKS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    DAY(DAYS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    HOUR(HOURS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    MINUTE(MINUTES, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    SECOND(SECONDS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE)),
    MILLISECOND(MILLIS, FOREVER, ValueRange.of(Year.MIN_VALUE, Year.MAX_VALUE));

    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;

    ExtendedChronoField(TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
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
