package org.xenon.echo.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private Bucket newLoginBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                  .capacity(5)
                  .refillIntervally(5, Duration.ofMinutes(2))
                  .build())
                .build();
    }

    private Bucket newResetBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(3)
                        .refillIntervally(3,Duration.ofMinutes(10))
                        .build())
                .build();
    }

    public boolean tryConsumeLogin(String key){
        Bucket bucket = cache.computeIfAbsent("LOGIN_" + key, k -> newLoginBucket());
        return bucket.tryConsume(1);
    }

    public boolean tryConsumeReset(String key){
        Bucket bucket = cache.computeIfAbsent("RESET_" + key, k -> newResetBucket());
        return bucket.tryConsume(1);
    }
}
