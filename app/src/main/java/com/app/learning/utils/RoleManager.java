package com.app.learning.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import com.app.learning.data.model.User;
import com.app.learning.data.model.UserModel;

import java.util.EnumSet;
import java.util.Set;

/**
 * RoleManager checks user sessions and determines roles and granular permissions.
 */
public class RoleManager {

    public enum Permission {
        VIEW_COURSES,
        ENROLL_COURSE,
        CREATE_COURSE,
        EDIT_COURSE,
        DELETE_COURSE,
        VIEW_DASHBOARD,
        MANAGE_USERS
    }

    public enum Role {
        STUDENT(EnumSet.of(Permission.VIEW_COURSES, Permission.ENROLL_COURSE)),
        TEACHER(EnumSet.of(Permission.VIEW_COURSES, Permission.CREATE_COURSE, Permission.EDIT_COURSE, Permission.VIEW_DASHBOARD)),
        ADMIN(EnumSet.allOf(Permission.class));

        private final Set<Permission> permissions;

        Role(Set<Permission> permissions) {
            this.permissions = permissions;
        }

        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission);
        }
    }

    private static volatile RoleManager instance;
    private final Context context;

    private RoleManager(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public static RoleManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (RoleManager.class) {
                if (instance == null) {
                    instance = new RoleManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Retrieve current user role from active sessions (UserPreference or SessionManager).
     * Defaults to Role.STUDENT if session is empty.
     */
    @NonNull
    public Role getCurrentRole() {
        // 1. Try UserPreference
        UserPreference userPreference = UserPreference.getInstance(context);
        if (userPreference.isLoggedIn()) {
            User user = userPreference.getUserProfile();
            if (user != null && user.getRole() != null) {
                return parseRole(user.getRole());
            }
        }

        // 2. Try SessionManager
        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isLoggedIn()) {
            UserModel userModel = sessionManager.getUser();
            if (userModel != null && userModel.getRole() != null) {
                return parseRole(userModel.getRole());
            }
        }

        return Role.STUDENT; // Safe default
    }

    /**
     * Helper to verify if user has a specific granular permission.
     */
    public boolean hasPermission(Permission permission) {
        return getCurrentRole().hasPermission(permission);
    }

    private Role parseRole(String roleStr) {
        if (roleStr == null) return Role.STUDENT;
        switch (roleStr.toLowerCase().trim()) {
            case "teacher":
            case "instructor":
                return Role.TEACHER;
            case "admin":
                return Role.ADMIN;
            case "student":
            default:
                return Role.STUDENT;
        }
    }
}
