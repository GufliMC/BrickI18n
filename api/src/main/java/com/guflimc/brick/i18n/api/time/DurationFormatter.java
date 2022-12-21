package com.guflimc.brick.i18n.api.time;

import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class DurationFormatter {

    /**
     * A value of a temporal field will not be formatted in the final result if the temporal field's value for
     * the given temporal amount is zero.
     * e.g. if the temporal amount is 2 years and 17 days, the result will be "2y 17d" and not "2y 0m 17d".
     */
    public static final int SKIP_ZERO = 1;

    /**
     * A value of a temporal field will not be formatted in the final result if the temporal amount is less than
     * the temporal field's base unit duration.
     * e.g. if the temporal amount is 3 months and 24 days, the result will be "3m 24d" and not "0y 3m 24d".
     */
    public static final int SKIP_OUT_OF_UPPER_BOUND = 2;

    /**
     * A value of a temporal field will not be formatted in the final result if the temporal field's value and any
     * subsequent temporal field's value in the format for the given temporal amount is zero. This only applies when
     * at least one prior temporal field's value is non-zero.
     * e.g. if the temporal amount is 1 year and 7 months, the result will be "1y 7m" and not "1y 7m 0d".
     */
    public static final int SKIP_OUT_OF_LOWER_BOUND = 4;

    //

    /**
     * Digital clock format. e.g. for 7 hours, 38 minutes and 12 seconds, the result will be "07:38:12".
     */
    public static final DurationFormatter DIGITAL = new DurationFormatterBuilder()
            .append(BrickChronoField.HOUR_REMAINDER, "%02d:", SKIP_OUT_OF_UPPER_BOUND)
            .append(BrickChronoField.MINUTE_REMAINDER, "%02d:")
            .append(BrickChronoField.SECOND_REMAINDER, "%02d:")
            .postProcessor(s -> s.substring(0, s.length() - 1)) // remove trailing colon
            .build();

    /**
     * Cozy format with period and duration. e.g. for 3 days and 38 minutes the result will be "3d 0H 38M".
     */
    public static final DurationFormatter COZY = new DurationFormatterBuilder()
            .append(BrickChronoField.YEAR, "%dy ", SKIP_OUT_OF_UPPER_BOUND | SKIP_OUT_OF_LOWER_BOUND)
            .append(BrickChronoField.MONTH_REMAINDER, "%dm ", SKIP_OUT_OF_UPPER_BOUND | SKIP_OUT_OF_LOWER_BOUND)
            .append(BrickChronoField.DAY_REMAINDER, "%dd ", SKIP_OUT_OF_UPPER_BOUND | SKIP_OUT_OF_LOWER_BOUND)
            .append(BrickChronoField.HOUR_REMAINDER, "%dH ", SKIP_OUT_OF_UPPER_BOUND | SKIP_OUT_OF_LOWER_BOUND)
            .append(BrickChronoField.MINUTE_REMAINDER, "%dM ", SKIP_OUT_OF_UPPER_BOUND | SKIP_OUT_OF_LOWER_BOUND)
            .append(BrickChronoField.SECOND_REMAINDER, "%dS ", SKIP_OUT_OF_LOWER_BOUND)
            .postProcessor(String::strip) // remove trailing space
            .build();

    /**
     * Compact format with period and duration. e.g. for 3 days and 38 minutes the result will be "3d 38M".
     */
    public static final DurationFormatter COMPACT = new DurationFormatterBuilder()
            .append(BrickChronoField.YEAR, "%dy ", SKIP_ZERO)
            .append(BrickChronoField.MONTH_REMAINDER, "%dm ", SKIP_ZERO)
            .append(BrickChronoField.DAY_REMAINDER, "%dd ", SKIP_ZERO)
            .append(BrickChronoField.HOUR_REMAINDER, "%dH ", SKIP_ZERO)
            .append(BrickChronoField.MINUTE_REMAINDER, "%dM ", SKIP_ZERO)
            .append(BrickChronoField.SECOND_REMAINDER, "%dS ", SKIP_OUT_OF_LOWER_BOUND)
            .postProcessor(String::strip) // remove trailing space
            .build();

    /**
     * Format only the highest unit of value. e.g. for 7 months, 26 days and 22 hours the result will be "7m".
     */
    public static final DurationFormatter HIGHEST = new DurationFormatterBuilder()
            .append(BrickChronoField.YEAR, "%dy ", SKIP_ZERO)
            .append(BrickChronoField.MONTH, "%dm ", SKIP_ZERO)
            .append(BrickChronoField.DAY, "%dd ", SKIP_ZERO)
            .append(BrickChronoField.HOUR, "%dH ", SKIP_ZERO)
            .append(BrickChronoField.MINUTE, "%dM ", SKIP_ZERO)
            .append(BrickChronoField.SECOND, "%dS ")
            .postProcessor(s -> s.substring(0, s.indexOf(" "))) // only use the first and therefore highest value
            .build();

    //

    private final TemporalFieldFormatter[] formatters;
    private final Function<String, String> postProcessor;

    DurationFormatter(TemporalFieldFormatter[] formatters, Function<String, String> postProcessor) {
        this.formatters = formatters;
        this.postProcessor = postProcessor;
    }

    public String format(Duration duration) {
        StringBuilder sb = new StringBuilder();
        for (TemporalFieldFormatter formatter : formatters) {
            sb.append(formatter.format(duration));
        }
        return postProcessor.apply(sb.toString());
    }

    record TemporalFieldFormatter(BrickChronoField field, Function<Long, String> format, int flags) {

        String format(Duration duration) {
            long millis = duration.toMillis();
            if ( flag(SKIP_OUT_OF_UPPER_BOUND) && millis < field.getBaseUnit().getDuration().toMillis()) {
                return "";
            }
            long reduce = millis;
            if (field.getRangeUnit() != ChronoUnit.FOREVER) {
                reduce %= field.getRangeUnit().getDuration().toMillis();
            }
            if ( flag(SKIP_OUT_OF_LOWER_BOUND) && millis > 0 && reduce == 0) {
                return "";
            }
            long value = reduce / field.getBaseUnit().getDuration().toMillis();
            if ( flag(SKIP_ZERO) && value == 0) {
                return "";
            }
            return format.apply(value);
        }

        boolean flag(int flag) {
            return (flags & flag) > 0;
        }
    }
}
