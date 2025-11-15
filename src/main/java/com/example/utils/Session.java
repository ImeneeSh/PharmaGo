package com.example.utils;

public class Session {
    private static String currentUserRole;

    public static String getRole() {
        return currentUserRole;
    }

    public static void setRole(String role) {
        currentUserRole = role;
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(currentUserRole);
    }
}