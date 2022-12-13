package com.guflimc.brick.i18n.api.time;

import java.time.Duration;
import java.time.temporal.*;
import java.util.function.Function;

public class DurationFormatter {

    public static final DurationFormatter COMPACT = new DurationFormatterBuilder()
            .append(ChronoField.YEAR, "%02d:")
            .append(ChronoField.DAY_OF_YEAR, "%02d:")
            .append(ChronoField.HOUR_OF_DAY, "%02d:")
            .append(ChronoField.MINUTE_OF_HOUR, "%02d:")
            .append(ChronoField.SECOND_OF_MINUTE, "%02d")
            .build();

    public static final DurationFormatter COZY = new DurationFormatterBuilder()
            .append(ChronoField.YEAR, "%02dy ")
            .append(ChronoField.DAY_OF_YEAR, "%02dd ")
            .append(ChronoField.HOUR_OF_DAY, "%02dh ")
            .append(ChronoField.MINUTE_OF_HOUR, "%02dm ")
            .append(ChronoField.SECOND_OF_MINUTE, "%02ds ")
            .build();

    //

    private final TemporalFieldFormatter[] formatters;

    DurationFormatter(TemporalFieldFormatter[] formatters) {
        this.formatters = formatters;
    }

    public String format(Duration duration) {
        StringBuilder sb = new StringBuilder();
        for (TemporalFieldFormatter formatter : formatters) {
            sb.append(formatter.format(duration));
        }
        return sb.toString();
    }

    record TemporalFieldFormatter(TemporalField field, boolean skipWhenOutOfBounds, Function<Long, String> format) {

        String format(Duration duration) {
            long millis = duration.toMillis();
            if ( skipWhenOutOfBounds && millis < field.getBaseUnit().getDuration().toMillis() ) {
                return "";
            }
            long reduce = millis;
            if ( field.getRangeUnit() != ChronoUnit.FOREVER ) {
                reduce %= field.getRangeUnit().getDuration().toMillis();
            }
            long value = reduce / field.getBaseUnit().getDuration().toMillis();
            return format.apply(value);
        }

    }
}
