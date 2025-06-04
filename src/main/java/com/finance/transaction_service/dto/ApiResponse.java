package com.finance.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;

    public ApiResponse(HttpStatus status,String message,T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
