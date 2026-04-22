package org.xenon.echo.utils;

public class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{10,}$"
    );
}
