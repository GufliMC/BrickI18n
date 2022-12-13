package com.guflimc.brick.i18n.api.time;

import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DurationFormatterBuilder {

    private final List<DurationFormatter.TemporalFieldFormatter> formatters = new ArrayList<>();

    public DurationFormatterBuilder append(TemporalField field, boolean skipWhenOutOfBounds, Function<Long, String> formatter) {
        formatters.add(new DurationFormatter.TemporalFieldFormatter(field, skipWhenOutOfBounds, formatter));
        return this;
    }

    public DurationFormatterBuilder append(TemporalField field, Function<Long, String> formatter) {
        formatters.add(new DurationFormatter.TemporalFieldFormatter(field, true, formatter));
        return this;
    }

    public DurationFormatterBuilder append(TemporalField field, String format) {
        return append(field, v -> String.format(format, v));
    }

    public DurationFormatter build() {
        return new DurationFormatter(formatters.toArray(DurationFormatter.TemporalFieldFormatter[]::new));
    }

}
