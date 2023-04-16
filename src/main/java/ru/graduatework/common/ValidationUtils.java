package ru.graduatework.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class ValidationUtils {

    public static final String FIO_VALIDATION_PATTERN = "^[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$";

    public static final String EMAIL_VALIDATION_PATTERN = "^[а-яёА-ЯЁ\\w\\-_\\.]+@[а-яёА-ЯЁ\\w\\-_\\.]+\\.[а-яёА-ЯЁ\\w]+$";

    public static final String PHONE_VALIDATION_PATTERN = "^$|([+]7|8)\\d{10}";

    public static boolean isValidEmail(String email) {
        return !StringUtils.isEmpty(email) && email.matches(ValidationUtils.EMAIL_VALIDATION_PATTERN);
    }
}

