package com.bubble.status.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommonException extends RuntimeException{
    private String message;
}
