package com.hansenvillage.hansenapp.constant;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class AppConstant {

    @UtilityClass
    public class Redis {

        public final String REG_CODE_PREFIX = "reg:code:";
        public final String ADMIN_LOGIN_PREFIX = "admin:login:";
        public final String RESET_PASSWORD_PREFIX = "reset:code:";
        public final String COOLDOWN_PREFIX = "cooldown:";

        public final long OTP_EXPIRATION_MINUTES = 10;
        public final long RESEND_COOLDOWN_SECONDS = 60;
    }


    @UtilityClass
    public class Otp {

        public final int CODE_LENGTH = 6;
        public final int CODE_BOUND = 1_000_000;
    }


    @UtilityClass
    public class Upload {

        public final String FACILITIES_DIR = "uploads/facilities";
        public final String ACTIVITIES_DIR = "uploads/activities";
        public final String POSTERS_DIR = "uploads/posters";
        public final String FAMILIES_DIR = "uploads/families";

        public final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

        public final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        );

        public final Set<String> ALLOWED_EXTENSIONS = Set.of(
                "jpg",
                "jpeg",
                "png",
                "webp"
        );
    }
}