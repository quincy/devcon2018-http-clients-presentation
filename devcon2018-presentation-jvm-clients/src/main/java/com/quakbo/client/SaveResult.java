package com.quakbo.client;

import java.util.Objects;

public class SaveResult {
    private final int id;
    private final String message;

    public SaveResult(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveResult that = (SaveResult) o;
        return id == that.id &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, id);
    }

    @Override
    public String toString() {
        return "SaveResult{" +
                "message='" + message + '\'' +
                ", id=" + id +
                '}';
    }
}
