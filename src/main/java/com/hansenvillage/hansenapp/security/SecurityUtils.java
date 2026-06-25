package com.hansenvillage.hansenapp.security;

import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<SecurityFamily> optionalCurrentFamily() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityFamily securityFamily) {
            return Optional.of(securityFamily);
        }
        return Optional.empty();
    }

    public static SecurityFamily currentFamily() {
        return optionalCurrentFamily()
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.INVALID_CREDENTIALS));
    }

    public static Long currentFamilyId() {
        return currentFamily().getId();
    }

    public static boolean hasRole(Role role) {
        return optionalCurrentFamily()
                .map(family -> family.getRoles().contains(role))
                .orElse(false);
    }

    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public static void assertOwnerOrAdmin(Long familyId) {
        if (!isAdmin() && !currentFamilyId().equals(familyId)) {
            throw FamilyException.of(FamilyErrorCode.ACCESS_DENIED);
        }
    }
}
