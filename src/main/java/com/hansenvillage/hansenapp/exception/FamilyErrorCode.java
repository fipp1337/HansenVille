package com.hansenvillage.hansenapp.exception;

import org.springframework.http.HttpStatus;

public enum FamilyErrorCode {
    REGISTER_IS_FAILED(HttpStatus.UNAUTHORIZED, "User register is failed: %s"),
    FAMILY_ADD_MEMBER_FAILED(HttpStatus.CONFLICT, "Member add is failed: %s"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found: %s"),
    FAMILY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found: %s"),
    FAMILY_NOT_FOUND(HttpStatus.NOT_FOUND, "Family not found: %s"),
//    PRODUCT_QUANTITY_EMPTY(HttpStatus.NOT_FOUND, "StockQuantity is empty or not enough: %s"),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session not found: %s"),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Template not found: %s"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND,"Booking not found"),
    INVALID_FAMILY(HttpStatus.BAD_REQUEST, "Invalid family"),
    TIME_OUT(HttpStatus.CONFLICT, "Time out"),
//    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found: %s"),
//    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found for user: %s"),
//    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "Wallet not found for user: %s"),
    NOT_ENOUGH_MEMBERS(HttpStatus.NOT_FOUND, "Product not in cart: %s"),
    POOL_SESSION_IS_FULL(HttpStatus.CONFLICT, "Pool session is full %s"),
//    WALLET_BALANCE_NOT_ENOUGH(HttpStatus.CONFLICT, "Balance not enough: %s"),
    USER_DOES_NOT_EXISTS(HttpStatus.CONFLICT, "User not found or does not exists: %s"),
    USER_EMAIL_EXISTS(HttpStatus.CONFLICT, "User with this email already exists: %s"),
//    COMMENT_ALREADY_LIKED(HttpStatus.CONFLICT, "You have already liked this comment"),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "Invalid token"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero"),
    POOL_HAS_BEEN_BOOKED(HttpStatus.BAD_REQUEST, "Pool has been booked: %s"),
    POOL_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Pool session not found: %s"),
//    INVALID_TOP_UP(HttpStatus.BAD_REQUEST, "Top-up amount must be positive"),
    FAMILY_EMPTY(HttpStatus.BAD_REQUEST, "Cart is empty");

//    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for: %s"),
//    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Insufficient wallet balance. Required: %s, available: %s");

    private final HttpStatus status;
    private final String message;

    FamilyErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}