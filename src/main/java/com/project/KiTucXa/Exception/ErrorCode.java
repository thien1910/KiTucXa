package com.project.KiTucXa.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXITED(1001,"User exited", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    INVALID_KEY(1000, "Invalid message Key",HttpStatus.NOT_FOUND),
    STUDENT_EXITED(1005,"Student exited",HttpStatus.NOT_FOUND),
    USER_NOT_EXITED(1006, "User not existed",HttpStatus.NOT_FOUND),
    UNAUTHENTIACTED(1008, "Unauthenticated",HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1007,"User not found",HttpStatus.NOT_FOUND),
    MANAGER_EXITED(1009,"manager exited",HttpStatus.NOT_FOUND),
    ROOM_ALREADY_EXISTS(1010, "Room already exists",HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND(1011, "Room not found",HttpStatus.NOT_FOUND),
    UTILITY_SERVICE_NOT_FOUND(1012, "Utility service not found",HttpStatus.NOT_FOUND),
    ROOM_SERVICE_NOT_FOUND(1013, "Room service not found",HttpStatus.NOT_FOUND),
    CONTRACT_NOT_FOUND(1014, "Contract not found",HttpStatus.NOT_FOUND),
    BILL_NOT_FOUND(1015, "Bill not found",HttpStatus.NOT_FOUND),
    BILL_DETAIL_NOT_FOUND(1016, "Bill detail not found",HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(1017, "Payment not found",HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1018, "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1019, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND(1020,"Cannot find user with UserName",HttpStatus.NOT_FOUND),
    INVALID_USERNAME_PASSWORD(1021, "Invalid UserName/Password",HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1022, "Role not found", HttpStatus.NOT_FOUND),
    CAN_NOT_CREATE_JWT_TOKEN(1023,"Cannot create jwt token", HttpStatus.BAD_REQUEST),
    MANAGER_NOT_FOUND(1024, "manager not found",HttpStatus.NOT_FOUND),
    STUDENT_NOT_FOUND(1025,"Student not found", HttpStatus.NOT_FOUND),
    PAYMENT_LIMIT_EXCEEDED (1026, "payment limit exceeded", HttpStatus.BAD_REQUEST),
    USER_FORBIDDENED(1027,"user account is unaccessible",HttpStatus.BAD_REQUEST),
    USER_HAS_ACTIVE_CONTRACT(1028,"User still has an active contract",HttpStatus.BAD_REQUEST),
    CONTRACT_INACTIVE(1029,"Contract inactive",HttpStatus.BAD_REQUEST),
    ROOM_FULL(1030,"Room is full can't add student",HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;

}
