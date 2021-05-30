package org.rzlabs.halo.util.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.IllegalFormatException;
import java.util.Locale;

public class StringUtils {

    public static final byte[] EMPTY_BYTES = new byte[0];
    @Deprecated // Charset parameters to String are currently slower than the charset's stirng name
    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static final String UTF8_STRING = StandardCharsets.UTF_8.toString();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    /**
     * Formats the string as {@link String#format(String, Object...)}, but instead of failing on illegal format, returns
     * the concatenated format string and format arguments. Should be used for unimportant formatting like
     * logging, exception messages, typically not directly.
     */
    public static String nonStrictFormat(String message, Object... formatArgs) {

        if (formatArgs == null || formatArgs.length == 0) {
            return message;
        }
        try {
            return String.format(Locale.ENGLISH, message, formatArgs);
        } catch (IllegalFormatException e) {
            StringBuilder sb = new StringBuilder(message);
            for (Object formatArg : formatArgs) {
                sb.append("; ").append(formatArg);
            }
            return sb.toString();
        }
    }

    public static String format(String message, Object... formatArgs) {
        return String.format(Locale.ENGLISH, message, formatArgs);
    }
}
