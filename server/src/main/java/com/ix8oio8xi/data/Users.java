package com.ix8oio8xi.data;

import java.util.Map;

/**
 * Information about users stored on the server, and a few methods to check passwords and verify
 * the existence of a user with a given username.
 */
public class Users {
    private static final Map<String, String> users = Map.of(
            "root", "root",
            "ixi", "password",
            "guest", "ggg"
    );

    public static boolean containsUser(String user) {
        return users.containsKey(user);
    }

    public static boolean checkPassword(String user, String messagePassword) {
        String password = users.get(user);
        return password != null && password.equals(messagePassword);
    }
}
