package ru.graduatework.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum NetworkingEventStatus {
    TO_BE("TO_BE"),
    IN_PROCESS("IN_PROCESS"),
    PASSED("PASSED");

    @Getter
    public final String value;
}
