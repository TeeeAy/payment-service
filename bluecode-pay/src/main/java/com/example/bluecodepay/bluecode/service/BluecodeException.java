package com.example.bluecodepay.bluecode.service;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class BluecodeException extends RuntimeException{

    private final int status;

    private final String message;

}
