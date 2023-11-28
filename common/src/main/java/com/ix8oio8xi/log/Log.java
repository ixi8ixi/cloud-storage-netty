package com.ix8oio8xi.log;

import java.time.LocalDateTime;

public class Log {
    private Log() {
        // no instance
        throw new AssertionError();
    }

    private static void logImpl(String marker, String message) {
        System.out.println(marker + " " + LocalDateTime.now() + " " + message);
    }

    public static void system(String message) {
        logImpl("[S]", message);
    }

    public static void error(String message) {
        logImpl("[E]", message);
    }

    public static void warning(String message) {
        logImpl("[S]", message);
    }

    public static void event(String message) {
        logImpl("[*]", message);
    }
}
