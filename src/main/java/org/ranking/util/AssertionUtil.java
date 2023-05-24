package org.ranking.util;

import org.apache.commons.lang3.StringUtils;

public class AssertionUtil {
    private AssertionUtil() {}

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notEmpty(String string, String message) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }
    }
}
