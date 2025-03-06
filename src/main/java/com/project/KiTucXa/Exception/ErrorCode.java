package com.project.KiTucXa.Exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_EXITED(1001,"User exited"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),
    INVALID_KEY(1000, "Invalid message Key"),
    STUDENT_EXITED(1005,"Student exited"),
    USER_NOT_EXITED(1006, "User not existed"),
    UNAUTHENTIACTED(1008, "Unauthenticated"),
    USER_NOT_FOUND(1007,"User not found"),
    MANAGER_EXITED(1009,"manager exited"),
    ROOM_ALREADY_EXISTS(1010, "Room already exists"),
    ROOM_NOT_FOUND(1011, "Room not found"),
    UTILITY_SERVICE_NOT_FOUND(1012, "Utility service not found"),
    ROOM_SERVICE_NOT_FOUND(1013, "Room service not found"),
    CONTRACT_NOT_FOUND(1014, "Contract not found"),
    BILL_NOT_FOUND(1015, "Bill not found"),
    BILL_DETAIL_NOT_FOUND(1016, "Bill detail not found")

    ;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
