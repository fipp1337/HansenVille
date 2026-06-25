package com.hansenvillage.hansenapp.exception;

public class FamilyException extends RuntimeException {

    private final FamilyErrorCode errorCode;

    private FamilyException(FamilyErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static FamilyException of(FamilyErrorCode errorCode, Object... args) {
        String message = args.length == 0 ? errorCode.getMessage() : errorCode.getMessage().formatted(args);
        return new FamilyException(errorCode, message);
    }

    public FamilyErrorCode getErrorCode() {
        return errorCode;
    }
}
