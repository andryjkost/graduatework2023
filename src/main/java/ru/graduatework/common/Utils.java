package ru.graduatework.common;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class Utils {
    public static String getFullName(String lastName, String firstName) {
        return Stream.of(lastName, firstName)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
