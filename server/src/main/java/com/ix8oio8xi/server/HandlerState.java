package com.ix8oio8xi.server;

import com.ix8oio8xi.config.Config;

/**
 * A state class storing data for the current user.
 */
public class HandlerState {
    private boolean loggedIn = false;
    private int attemptsLeft = Config.ATTEMPTS_TO_LOG_IN;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public int attemptsLeft() {
        return attemptsLeft--;
    }

    public void login() {
        this.loggedIn = true;
    }
}
