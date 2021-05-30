package org.rzlabs.halo.util.common;

public class ISE extends IllegalStateException {

    public ISE(String formatText, Object... arguments) {
        super(StringUtils.nonStrictFormat(formatText, arguments));
    }

    public ISE(Throwable cause, String formatText, Object... arguments) {
        super(StringUtils.nonStrictFormat(formatText, arguments), cause);
    }
}
