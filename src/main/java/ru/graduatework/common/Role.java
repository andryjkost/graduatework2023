package ru.graduatework.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.graduatework.common.Permission.*;

@RequiredArgsConstructor
public enum Role {

    USER(Set.of(USER_READ)),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    AUTHOR_READ,
                    AUTHOR_UPDATE,
                    AUTHOR_CREATE,
                    AUTHOR_DELETE)
    ),
    AUTHOR(
            Set.of(
                    AUTHOR_READ,
                    AUTHOR_UPDATE,
                    AUTHOR_CREATE,
                    AUTHOR_DELETE)
    );

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
