package org.foxtrot.utils;

public final class Waits {

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e.getMessage());
        }
    }

    private Waits() {
    }
}
