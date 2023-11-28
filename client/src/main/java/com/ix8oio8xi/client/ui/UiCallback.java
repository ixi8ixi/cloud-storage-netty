package com.ix8oio8xi.client.ui;

/**
 * A callback to facilitate communication between command processors and the user interface.
 */
public interface UiCallback {
    /**
     * Post a message indicating the success of the operation and an informational message.
     *
     * @param success result of operation
     * @param message info message
     */
    void postMessage(boolean success, String message);
}
