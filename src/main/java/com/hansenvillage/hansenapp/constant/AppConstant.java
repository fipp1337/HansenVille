package com.hansenvillage.hansenapp.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {

    @UtilityClass
    public class Redis {
        public final String REG_CODE_PREFIX = "reg:code:";
        public final String ADMIN_LOGIN_PREFIX = "admin:login:";
        public final String RESET_PASSWORD_PREFIX = "reset:code:";
        public static final String COOLDOWN_PREFIX = "cooldown:";
        public final long OTP_EXPIRATION_MINUTES = 10;
        public static final long RESEND_COOLDOWN_SECONDS = 60;
    }

    @UtilityClass
    public class Otp {
        public static final int CODE_LENGTH = 6;
        public static final int CODE_BOUND = 1_000_000;
    }

    @UtilityClass
    public class Upload {
        public final String FACILITIES_DIR = "uploads/facilities";
        public final String ACTIVITIES_DIR = "uploads/activities";
        public final String POSTERS_DIR = "uploads/posters";
    }
}
