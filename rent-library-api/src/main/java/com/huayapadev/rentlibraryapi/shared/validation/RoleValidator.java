package com.huayapadev.rentlibraryapi.shared.validation;

import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RoleValidator {

    public boolean hasRole(UserRoleEnum role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream().anyMatch(myRole -> Objects.equals(myRole.getAuthority(), "ROLE_" + role));
    }

    public boolean hasAnyRole(List<UserRoleEnum> roles) {
        return roles.stream().anyMatch(this::hasRole);
    }
}
