package com.hansenvillage.hansenapp.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {

    @UtilityClass
    public class Redis {
        public final String REG_CODE_PREFIX = "reg:code:";
        public final String RESET_PASSWORD_PREFIX = "reset:code:";
        public final long OTP_EXPIRATION_MINUTES = 5;
    }

    @UtilityClass
    public class Security {
        public final String AUTHORIZATION_HEADER = "Authorization";
        public final String BEARER_PREFIX = "Bearer ";
        public final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
        public final String ROLE_POOL_MANAGER = "POOL_MANAGER";
        public final String ROLE_GYM_MANAGER = "GYM_MANAGER";
        public final String ROLE_CINEMA_MANAGER = "CINEMA_MANAGER";
    }
}