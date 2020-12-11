package com.example.JPACheckpoint;

import java.util.HashMap;

public class AuthenticatedUser {
    private boolean authenticated;
    private HashMap<String, Object> user = new HashMap<>();

    public AuthenticatedUser() {
        this.authenticated = false;

    }
    public AuthenticatedUser(boolean authenticated, User user) {
        this.authenticated = authenticated;
        this.user.put("id",user.getId());
        this.user.put("email",user.getEmail());

        //this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public HashMap<String, Object> getUser() {
        return user;
    }

    public AuthenticatedUser setUser(HashMap<String, Object> user) {
        this.user = user;
        return this;
    }
}
