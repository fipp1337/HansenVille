package com.hansenvillage.hansenapp.exception;

import org.springframework.http.HttpStatus;

public enum FamilyErrorCode {
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "Wrong password: %s"),
    WRONG_EMAIL(HttpStatus.UNAUTHORIZED, "Wrong email: %s"),
    REGISTER_IS_FAILED(HttpStatus.UNAUTHORIZED, "User register is failed: %s"),
    FAMILY_ADD_MEMBER_FAILED(HttpStatus.CONFLICT, "Member add is failed: %s"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found: %s"),
    FAMILY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found: %s"),
    FAMILY_NOT_FOUND(HttpStatus.NOT_FOUND, "Family not found: %s"),
    GYM_GROUP_CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "Gym group class not found: %s"),
    GYM_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Gym templates not found: %s"),
    SESSION_ALREADY_STARTED(HttpStatus.CONFLICT, "Session was started"),
    TOO_MANY_SEATS(HttpStatus.CONFLICT, "You can't book more seats, than there are members in your family"),
    POSTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Poster not found for session: %s"),
    FILE_UPLOAD_FAILED(HttpStatus.CONFLICT, "File upload failed"),
    FILE_DELETE_FAILED(HttpStatus.CONFLICT, "File delete failed"),
    OLD_PASSWORD_REQUIRED(HttpStatus.CONFLICT, "Old password required"),
    INVALID_OLD_PASSWORD(HttpStatus.CONFLICT, "Invalid old password"),
    NEW_PASSWORD_REQUIRED(HttpStatus.CONFLICT, "New password required"),
    NEW_PASSWORD_MATCH_WITH_OLD(HttpStatus.CONFLICT, "New password match with old"),
    BOOKING_FAILED(HttpStatus.CONFLICT, "Booking failed"),
    FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Facility not found: %s"),
    FACILITY_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Facility image not found"),
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Activity not found: %s"),
    ACTIVITY_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Activity image not found"),
//    PRODUCT_QUANTITY_EMPTY(HttpStatus.NOT_FOUND, "StockQuantity is empty or not enough: %s"),
//    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session not found: %s"),
    OUT_OF_TICKETS(HttpStatus.NOT_ACCEPTABLE, "Not enough tickets"),
    INVALID_PHONE_FORMAT(HttpStatus.CONFLICT, "Invalid phone number format"),
    NOT_YOUR_BOOKING(HttpStatus.NOT_ACCEPTABLE, "Not your booking"),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Template not found: %s"),
    GYM_ALREADY_BOOKED(HttpStatus.CONFLICT, "Gym already booked"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND,"Booking not found"),
    INVALID_FAMILY(HttpStatus.BAD_REQUEST, "Invalid family"),
    TIME_OUT(HttpStatus.CONFLICT, "Time out"),
    BOOKING_ALREADY_CANCELLED(HttpStatus.CONFLICT, "Book already has been canceled"),
    ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "Address already exists"),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "Passwords do not match"),
//    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found: %s"),
//    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found for user: %s"),
//    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "Wallet not found for user: %s"),
    NOT_ENOUGH_MEMBERS(HttpStatus.NOT_FOUND, "Product not in cart: %s"),
    NO_POOL_TEMPLATES_FOUND(HttpStatus.NOT_FOUND, "No pool templates found"),
    POOL_SESSION_IS_FULL(HttpStatus.CONFLICT, "Pool session is full: %s"),
    CINEMA_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Cinema session not found %s"),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "Cinema seat not found %s"),
    CINEMA_SESSION_IS_FULL(HttpStatus.CONFLICT, "Cinema session is full: %s"),
//    WALLET_BALANCE_NOT_ENOUGH(HttpStatus.CONFLICT, "Balance not enough: %s"),
    USER_DOES_NOT_EXISTS(HttpStatus.CONFLICT, "User not found or does not exists: %s"),
    USER_EMAIL_EXISTS(HttpStatus.CONFLICT, "User with this email already exists: %s"),
//    COMMENT_ALREADY_LIKED(HttpStatus.CONFLICT, "You have already liked this comment"),
    INVALID_DATES(HttpStatus.CONFLICT, "Not valid dates"),
    INVALID_TIME(HttpStatus.CONFLICT, "Not valid time"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "Invalid token"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero"),
    POOL_HAS_BEEN_BOOKED(HttpStatus.BAD_REQUEST, "Pool has been booked: %s"),
    SEAT_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "Seat has been booked: %s"),
    NO_CINEMA_TEMPLATES_FOUND(HttpStatus.NOT_FOUND, "Cinema templates not found"),
    POOL_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Pool session not found: %s"),
//    INVALID_TOP_UP(HttpStatus.BAD_REQUEST, "Top-up amount must be positive"),
    FAMILY_EMPTY(HttpStatus.BAD_REQUEST, "Family is empty"),
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "Hall not found: %s"),
    ROLES_EMPTY(HttpStatus.BAD_REQUEST, "Roles must not be empty"),
    INVALID_INVITE_CODE(HttpStatus.UNAUTHORIZED, "Invalid invite code"),
    INVALID_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "Invalid verification code"),
    REGISTRATION_NOT_INITIATED (HttpStatus.UNAUTHORIZED, "Registration not initiated");


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