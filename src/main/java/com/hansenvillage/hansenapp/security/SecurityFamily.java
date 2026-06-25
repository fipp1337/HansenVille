package com.hansenvillage.hansenapp.security;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class SecurityFamily implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final List<Role> roles;

    public SecurityFamily(Family family, List<Role> roles) {
        this.id = family.getId();
        this.email = family.getEmail();
        this.password = family.getPassword();
        this.roles = List.copyOf(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
