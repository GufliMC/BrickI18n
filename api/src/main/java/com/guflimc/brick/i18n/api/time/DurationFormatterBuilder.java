package com.guflimc.brick.i18n.api.time;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DurationFormatterBuilder {

    private final List<DurationFormatter.TemporalFieldFormatter> formatters = new ArrayList<>();
    private Function<String, String> postProcessor = Function.identity();

    /**
     * Appends a format for the given {@link BrickChronoField} to the resulting format. Extra flags may be applied
     * to this field.
     *
     * @param field     the chrono field to which this format applies
     * @param formatter the formatter to use for this field
     * @param flags     extra flags to apply to this field
     * @return this builder
     */
    public DurationFormatterBuilder append(BrickChronoField field, Function<Long, String> formatter, int flags) {
        formatters.add(new DurationFormatter.TemporalFieldFormatter(field, formatter, flags));
        return this;
    }

    /**
     * Appends a format for the given {@link BrickChronoField} to the resulting format. This also applies
     * the {@link DurationFormatter#SKIP_OUT_OF_UPPER_BOUND} flag.
     *
     * @param field     the chrono field to which this format applies
     * @param formatter the formatter to use for this field
     * @return this builder
     */
    public DurationFormatterBuilder append(BrickChronoField field, Function<Long, String> formatter) {
        return append(field, formatter, DurationFormatter.SKIP_OUT_OF_UPPER_BOUND);
    }

    /**
     * Appends a format for the given {@link BrickChronoField} to the resulting format. Extra flags may be applied
     * to this field.
     *
     * @param field  the chrono field to which this format applies
     * @param format the format to use for this field
     * @param flags  extra flags to apply to this field
     * @return this builder
     */
    public DurationFormatterBuilder append(BrickChronoField field, String format, int flags) {
        return append(field, (v) -> String.format(format, v), flags);
    }

    /**
     * Appends a format for the given {@link BrickChronoField} to the resulting format. This also applies
     * the {@link DurationFormatter#SKIP_OUT_OF_UPPER_BOUND} flag.
     *
     * @param field  the chrono field to which this format applies
     * @param format the format to use for this field
     * @return this builder
     */
    public DurationFormatterBuilder append(BrickChronoField field, String format) {
        return append(field, format, DurationFormatter.SKIP_OUT_OF_UPPER_BOUND);
    }

    /**
     * Applies a post processor to the resulting format.
     *
     * @param postProcessor the post processor to apply
     * @return this builder
     */
    public DurationFormatterBuilder postProcessor(@NotNull Function<String, String> postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    /**
     * Create a new {@link DurationFormatter} with the previously added formats.
     *
     * @return the new formatter
     */
    public DurationFormatter build() {
        return new DurationFormatter(formatters.toArray(DurationFormatter.TemporalFieldFormatter[]::new), postProcessor);
    }

}
