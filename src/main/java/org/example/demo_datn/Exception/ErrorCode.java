package org.example.demo_datn.Exception;

public enum ErrorCode {
    FILE_EMPTY(1001, "File is empty"),
    FILE_NOT_IMAGE(1002, "File is not image"),
    FILE_INVALID(1003, "File is invalid"),



    ALBUM_PRIVATE(2001, "Album is private"),
    LOCATION_NOT_FOUND(2002, "Location not found"),

    JWT_NOT_CREATED(4000,"Cannot create JWT"),
    INVALID_SIGNATURE(4001,"Invalid signature"),
    INVALID_TOKEN(4002,"Invalid token"),
    MISSING_TOKEN(4003,"Missing token"),
    INVALID_PASSWORD(4004,"Invalid password"),
    FORBIDDEN(4005,"You not have permission to perform this operation"),

    UNAUTHENTICATED(5000,"Unauthenticated "),//ko dang nhap
    ACCESS_DENIED(5001,"You do not have permission to access this resource"),
    INVALID_PERMISSIONS(5003,"Cannot parse permissions from token"),
    PERMISSION_NOT_FOUND(5004,"Permission not found"),
    USER_ROLE_NOT_FOUND ( 5005,"User role not found"),
    USER_NOT_FOUND(5006,"User not found"),
    ALBUM_NOT_FOUND(5007,"Album not found"),



    ;


    ErrorCode(int code ,String message){
        this.code = code;
        this.message = message;
    }
    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
