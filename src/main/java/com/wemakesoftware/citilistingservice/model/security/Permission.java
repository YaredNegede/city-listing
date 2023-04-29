package com.wemakesoftware.citilistingservice.model.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ALLOW_EDIT("admin:update"),
    ALLOW_CREATE("admin:create"),
    ALLOW_DELETE("admin:delete");

    @Getter
    private final String permission;
}
