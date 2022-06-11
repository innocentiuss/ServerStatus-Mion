package com.bubble.status.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigErrorException extends RuntimeException{
    private String message;
}
