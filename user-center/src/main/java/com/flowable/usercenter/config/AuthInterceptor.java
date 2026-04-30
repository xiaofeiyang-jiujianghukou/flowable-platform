package com.flowable.usercenter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowable.usercenter.entity.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "session:";
    private static final long EXPIRE_HOURS = 2;
    private static final long RENEW_THRESHOLD_MINUTES = 30;

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(StringRedisTemplate redis) { this.redis = redis; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        if ("OPTIONS".equals(req.getMethod())) return true;

        String token = req.getHeader("Authorization");
        if (token == null || token.isBlank()) { resp.setStatus(401); return false; }
        if (token.startsWith("Bearer ")) token = token.substring(7);

        String json = redis.opsForValue().get(TOKEN_PREFIX + token);
        if (json == null) { resp.setStatus(401); return false; }

        SessionUser user = objectMapper.readValue(json, SessionUser.class);

        // 滑动窗口：剩余时间不足30分钟时续约2小时
        Long ttl = redis.getExpire(TOKEN_PREFIX + token, TimeUnit.SECONDS);
        if (ttl != null && ttl < TimeUnit.MINUTES.toSeconds(RENEW_THRESHOLD_MINUTES)) {
            redis.expire(TOKEN_PREFIX + token, EXPIRE_HOURS, TimeUnit.HOURS);
        }

        req.setAttribute("sessionUser", user);
        return true;
    }
}
