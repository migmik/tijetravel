package com.tijetravel.frontend.util;

import com.tijetravel.frontend.dto.AuthUserSession;
import org.springframework.security.core.Authentication;

public class SecurityUtils {

    public static AuthUserSession getSessionUser(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof AuthUserSession) {
            return (AuthUserSession) auth.getPrincipal();
        }
        return null;
    }

    public static boolean isAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return !"anonymousUser".equals(auth.getPrincipal());
    }
}

