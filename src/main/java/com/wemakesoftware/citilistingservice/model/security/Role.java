package com.wemakesoftware.citilistingservice.model.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wemakesoftware.citilistingservice.model.security.Permission.*;


@RequiredArgsConstructor
public enum Role {

    ADMIN(
            Set.of(
                    ALLOW_EDIT,
                    ALLOW_CREATE,
                    ALLOW_DELETE
            )
    );

    public static final String ADMIN_ROLE = "ADMIN";

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}