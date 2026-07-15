package com.hansenvillage.hansenapp.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String REG_CODE_PREFIX = "reg:code:";
    private static final String LOGIN_CODE_PREFIX = "login:code:";
    private static final String LINK_STATE_PREFIX = "link:state:";
    private static final String RESET_CODE_PREFIX = "reset:code:";
    private static final Duration CODE_TTL = Duration.ofMinutes(10);

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRegistrationData(String email, String inviteCode, String verificationCode) {
        String key = REG_CODE_PREFIX + email;
        Map<String, String> data = Map.of(
                "inviteCode", inviteCode != null ? inviteCode : "",
                "verificationCode", verificationCode != null ? verificationCode : ""
        );

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, CODE_TTL);
    }

    public Map<Object, Object> getRegistrationData(String email) {
        String key = REG_CODE_PREFIX + email;
        return redisTemplate.opsForHash().entries(key);
    }

    public void deleteRegistrationData(String email) {
        String key = REG_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }

    public void storeResetCode(String email, String code) {
        String key = RESET_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_TTL.toMinutes(), java.util.concurrent.TimeUnit.MINUTES);
    }

    public String getResetCode(String email) {
        String key = RESET_CODE_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteResetCode(String email) {
        String key = RESET_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }

    public void preSeedInviteCode(String email, String inviteCode) {
        storeRegistrationData(email, inviteCode, "");
    }

    public void storeLoginCode(String email, String code) {
        String key = LOGIN_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_TTL.toMinutes(), TimeUnit.MINUTES);
    }

    public String getLoginCode(String email) {
        return redisTemplate.opsForValue().get(LOGIN_CODE_PREFIX + email);
    }

    public void deleteLoginCode(String email) {
        redisTemplate.delete(LOGIN_CODE_PREFIX + email);
    }

    public void storeLinkState(String token, long userId) {
        String key = LINK_STATE_PREFIX + token;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), CODE_TTL.toMinutes(), TimeUnit.MINUTES);
    }

    public String getLinkState(String token) {
        return redisTemplate.opsForValue().get(LINK_STATE_PREFIX + token);
    }

    public void deleteLinkState(String token) {
        redisTemplate.delete(LINK_STATE_PREFIX + token);
    }
}