package com.quakbo.client;

import java.util.Objects;

public class DeleteResult {
    private final int id;
    private final String message;

    public DeleteResult(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteResult that = (DeleteResult) o;
        return id == that.id &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message);
    }

    @Override
    public String toString() {
        return "DeleteResult{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
