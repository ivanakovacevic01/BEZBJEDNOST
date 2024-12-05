package com.example.security.service;

import com.example.security.model.PackageType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class RateLimiterService implements IRateLimiterService {

    private static final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryAcquire(String packageType, Long advertisementId) {
        String key = packageType + "_" + advertisementId;
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucketForPackageAndAdvertisement);
        return bucket.tryConsume(1);
    }

    private Bucket createBucketForPackageAndAdvertisement(String key) {
        int capacity;
        switch (key.split("_")[0]) {
            case "BASIC":
                capacity = 5;
                break;
            case "STANDARD":
                capacity = 100;
                break;
            case "GOLD":
                capacity = 10000;
                break;
            default:
                throw new IllegalArgumentException("Invalid package type: " + key.split("_")[0]);
        }
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }
}
