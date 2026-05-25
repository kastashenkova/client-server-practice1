package org.example.practice2.warehouse;

public record CommandResult(boolean success, String message) {

    public static CommandResult success(String message) {
        return new CommandResult(true, message);
    }

    public static CommandResult error(String message) {
        return new CommandResult(false, message);
    }
}
