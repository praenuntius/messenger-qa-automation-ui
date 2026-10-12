package org.foxtrot.utils;

import java.util.concurrent.ThreadLocalRandom;

public class MessageUtil {
    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    public static String getRandomMessage(int minLength, int maxLength) {
        if (minLength < 0 || maxLength < 0) {
            throw new IllegalArgumentException("minLength and maxLength must be >= 0");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength must be >= minLength");
        }
        if (LOREM_IPSUM.isEmpty()) {
            return "";
        }

        int min = Math.min(minLength, LOREM_IPSUM.length());
        int max = Math.min(maxLength, LOREM_IPSUM.length());
        int messageLength = ThreadLocalRandom.current().nextInt(min, max + 1);

        // рандомный кусок текста
        int maxStart = Math.max(0, LOREM_IPSUM.length() - messageLength);
        int start = ThreadLocalRandom.current().nextInt(0, maxStart + 1);
        String slice = LOREM_IPSUM.substring(start, start + messageLength);

        // откат, на случай пробела в начале куска
        slice = slice.stripLeading();
        if (slice.isEmpty() && messageLength > 0) {
            return "a";
        }
        return slice;
    }
}
