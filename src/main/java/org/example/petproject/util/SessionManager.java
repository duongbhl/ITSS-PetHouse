package org.example.petproject.util;

import org.example.petproject.model.User;
import org.example.petproject.model.User.Role;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        currentUser = user;
        System.out.println("Session started for user: " + user.getFullName() + " with role: " + user.getRole());
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            System.out.println("Warning: Attempted to get current user but no user is logged in");
        }
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasRole(Role role) {
        if (!isLoggedIn()) {
            System.out.println("Warning: Checking role but no user is logged in");
            return false;
        }
        return currentUser.getRole() == role;
    }

    public static void clearSession() {
        if (currentUser != null) {
            System.out.println("Clearing session for user: " + currentUser.getFullName());
        }
        currentUser = null;
    }

    public static Role getCurrentUserRole() {
        if (!isLoggedIn()) {
            System.out.println("Warning: Attempted to get role but no user is logged in");
            return null;
        }
        return currentUser.getRole();
    }
}
