package com.example.todoapi.common;

public record DeleteResponse(String status) {

    public static DeleteResponse ok() {
        return new DeleteResponse("ok");
    }
}
