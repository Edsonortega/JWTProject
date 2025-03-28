package com.example.JWTProject.aspect;

import com.example.JWTProject.entity.User;
import com.example.JWTProject.ratelimit.RateLimit;
import com.example.JWTProject.security.JwtTokenProvider;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;

    public RateLimitAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = jwtTokenProvider.getUserFromToken(username);

        if(user.isHasMembership() && rateLimit.skipForMembers()){
            return joinPoint.proceed();
        }
        //String methodName = joinPoint.getSignature().toShortString();
        //Bucket bucket = buckets.computeIfAbsent(methodName, k -> createNewBucket(rateLimit));

        String key = username + ":" + joinPoint.getSignature().toShortString();
        Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket(rateLimit));

        if(bucket.tryConsume(1)){
            return joinPoint.proceed();
        }
        else{
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            if(response != null){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }
    }

    private Bucket createNewBucket(RateLimit rateLimit) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(rateLimit.capacity())
                        .refillGreedy(rateLimit.capacity(), Duration.ofSeconds(rateLimit.time()))
                        .build())
                .build();
    }
}
