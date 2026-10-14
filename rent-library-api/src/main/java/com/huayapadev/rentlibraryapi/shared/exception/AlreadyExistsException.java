package com.huayapadev.rentlibraryapi.shared.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
    public AlreadyExistsException(String resource, String field) {
        this(resource, field, "", "");
    }
    public AlreadyExistsException(String resource, String field, String value) {
        this(resource, field, value, "");
    }
    public AlreadyExistsException(String resource, String field, String value, String note) {
        super(buildMessage(resource, field, value, note));
    }

    private static String buildMessage(String resource, String field, String value, String note) {
        String message = (value == null || value.isBlank())
            ? String.format("Ya existe %s registrado con %s.", resource, field)
            : String.format("Ya existe %s registrado con %s '%s'.", resource, field, value);
        if (note != null && !note.isBlank()) {
            return message + " " + note;
        }
        return message;
    }
}
