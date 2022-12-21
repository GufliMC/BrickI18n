package com.guflimc.brick.i18n.api.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private static final Pattern PATTERN =
            Pattern.compile("(?:([0-9]+)y)?(?:([0-9]+)m)?(?:([0-9]+)d)?(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?");

    private static final List<BrickChronoField> GROUPS = List.of(
            BrickChronoField.YEAR,
            BrickChronoField.MONTH_REMAINDER,
            BrickChronoField.DAY_REMAINDER,
            BrickChronoField.HOUR_REMAINDER,
            BrickChronoField.MINUTE_REMAINDER,
            BrickChronoField.SECOND_REMAINDER
    );

    /**
     * Obtains a {@link DurationParser} from a text string such as "{@code 2y 4d 3H 17M 6S}".
     * This will parse a textual representation of a {@link DurationParser}, including the string produced
     * by {@link #toString()}.
     */
    public static Duration parse(@NotNull String text) {
        text = text.replace(" ", "").replace("_", "");
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Text cannot be parsed to a BrickTemporalAmount", text, 0);
        }

        Duration duration = Duration.ZERO;
        for (int i = 0; i < GROUPS.size(); i++ ) {
            int value = parseNumber(matcher.group(i + 1));
            BrickChronoField field = GROUPS.get(i);
            if ( !field.range().isValidValue(value) && !duration.isZero() ) {
                throw new DateTimeParseException("Text cannot be parsed, invalid value for " + field.getBaseUnit().toString().toLowerCase() + ".", text, 0);
            }

            duration = duration.plus(value, GROUPS.get(i).getBaseUnit());
        }
        return duration;
    }

//    /**
//     * Obtains a {@link DurationParser} from a text string such as "{@code 2y 4d 3H 17M 6S}".
//     * This will parse a textual representation of a {@link DurationParser}, including the string produced
//     * by {@link #toString()}.
//     */
//    public static Duration parse(@NotNull String text) {
//        text = text.replace(" ", "").replace("_", "");
//        Matcher matcher = PATTERN.matcher(text);
//        if ( !matcher.matches() ) {
//            throw new DateTimeParseException("Text cannot be parsed to a BrickTemporalAmount", text, 0);
//        }
//
//        // years
//        int years = parseNumber(matcher.group(1));
//
//        // months
//        int months = parseNumber(matcher.group(2));
//        if ( months > 11 && years != 0 ) {
//            throw new DateTimeParseException("Text cannot be parsed, invalid value for months.", text, 0);
//        }
//
//        // days
//        int days = parseNumber(matcher.group(3));
//        if ( days > 29 && (years != 0 || months != 0) ) {
//            throw new DateTimeParseException("Text cannot be parsed, invalid value for days.", text, 0);
//        }
//
//        // hours
//        int hours = parseNumber(matcher.group(4));
//        if ( hours > 23 && (years != 0 || months != 0 || days != 0) ) {
//            throw new DateTimeParseException("Text cannot be parsed, invalid value for hours.", text, 0);
//        }
//
//        // minutes
//        int minutes = parseNumber(matcher.group(5));
//        if ( minutes > 59 && (years != 0 || months != 0 || days != 0 || hours != 0 ) ) {
//            throw new DateTimeParseException("Text cannot be parsed, invalid value for minutes.", text, 0);
//        }
//
//        // seconds
//        int seconds = parseNumber(matcher.group(6));
//        if ( seconds > 59 && (years != 0 || months != 0 || days != 0 || hours != 0 || minutes != 0 ) ) {
//            throw new DateTimeParseException("Text cannot be parsed, invalid value for seconds.", text, 0);
//        }
//
//        return Duration.ZERO
//                .plus(years, BrickChronoUnit.YEARS)
//                .plus(months, BrickChronoUnit.MONTHS)
//                .plus(days, DAYS)
//                .plus(hours, HOURS)
//                .plus(minutes, MINUTES)
//                .plus(seconds, SECONDS);
//    }
//
    private static int parseNumber(String text) {
        if (text == null) {
            return 0;
        }
        return Integer.parseInt(text);
    }

}
