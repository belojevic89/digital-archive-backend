package com.projekat.digital_archive.exception;

public class DuplicateDocumentException extends RuntimeException{

    public DuplicateDocumentException(String message){

        super(message);
    }
}
