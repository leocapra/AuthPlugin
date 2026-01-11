package br.com.otk.login.domain.valueobject;

public enum RegisterResult {
    SUCCESS,
    ALREADY_EXISTS,
    PASSWORD_MISMATCH,
    EMPTY_PASSWORD,
    INVALID_PASSWORD
}
