package org.ranking.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 断言工具类
 *
 * @author Gu
 * @since 2023-05-24
 */
public class AssertionUtil {
    private AssertionUtil() {}

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertPositiveNumber(int num, String message) {
        if (num <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String string, String message) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }
    }
}
