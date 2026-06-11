package com.community.api.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T> {
    private String message;
    private T data;

    public static <T> ApiResponse<T> of(String message, T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.message = message;
        response.data = data;
        return response;
    }

    // data가 null
    public static ApiResponse<Object> of(String message){
        return of(message, null);
    }
}
