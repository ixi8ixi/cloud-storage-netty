package com.ix8oio8xi.config;

public class Config {
    private Config() {
        // No instance
        throw new AssertionError();
    }

    // Network
    public static final int PORT = 27359;
    public static final String HOST = "localhost";
    public static final int CONNECTION_TIMEOUT_SECONDS = 10;
    public static final int MAX_FRAME_LENGTH = 1000000;

    // Message format
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 512;

    // Registry
    public static String SERVER_PROCESSORS_PACKAGE = "com.ix8oio8xi.server.commands.processors";
    public static String CLIENT_PROCESSORS_PACKAGE = "com.ix8oio8xi.client.commands.processors";

    // Client logic
    public static final int ATTEMPTS_TO_LOG_IN = 3;

    // Client UI
    public static final double WINDOW_WIDTH = 360;
    public static final double WINDOW_HEIGHT = 250;
}
