package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Service.RedisTokenService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisDisabledCondition;

@Service
@Conditional(RedisDisabledCondition.class)
public class NoOpRedisTokenService implements RedisTokenService {
    
    @Override
    public void whitelistToken(String token) {
        // No-op
    }
    
    @Override
    public void blacklistToken(String token) {
        // No-op
    }
    
    @Override
    public boolean isTokenWhitelisted(String token) {
        return true; // Consider all tokens as whitelisted when Redis is disabled
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        return false; // Consider no tokens as blacklisted when Redis is disabled
    }
    
    @Override
    public void removeToken(String token) {
        // No-op
    }
} 