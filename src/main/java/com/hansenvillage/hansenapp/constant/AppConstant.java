package com.hansenvillage.hansenapp.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {

    @UtilityClass
    public class Redis {
        public final String REG_CODE_PREFIX = "reg:code:";
        public final String ADMIN_LOGIN_PREFIX = "admin:login:";
        public final String RESET_PASSWORD_PREFIX = "reset:code:";
        public final long OTP_EXPIRATION_MINUTES = 10;
    }

    @UtilityClass
    public class Upload {
        public final String FACILITIES_DIR = "uploads/facilities";
        public final String ACTIVITIES_DIR = "uploads/activities";
        public final String POSTERS_DIR = "uploads/posters";
    }
}
