package com.example.security.controller;

import com.example.security.model.Advertisement;
import com.example.security.model.AdvertisementRequest;
import com.example.security.model.PackageType;
import com.example.security.service.IAdvertisementService;
import com.example.security.service.IRateLimiterService;
import com.example.security.service.RateLimiterService;
import com.example.security.util.AdvertisementSimulator;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class AdvertisementController {
    private final Cache<Long, Bucket> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Autowired
    private IRateLimiterService rateLimiterService;


    @Autowired
    private IAdvertisementService advertisementService;
    @PreAuthorize("hasRole('employee')")
    @GetMapping("")
    public ArrayList<Advertisement> getAll() {
        return advertisementService.getAll();
    }

    @PreAuthorize("hasRole('employee')")
    @PostMapping("/creating/{id}/{requestId}")
    public Advertisement save(@RequestBody Advertisement advertisement,@PathVariable("id") Long id,@PathVariable("requestId") Long requestId) {
        if(!advertisement.getSlogan().equals("") && !advertisement.getDescription().equals("") && advertisement.getDuration()>0)
            return advertisementService.save(advertisement,id,requestId);
        return null;
    }
    @PreAuthorize("hasRole('client')")

    @GetMapping("/gettingByClient/{id}")
    public ArrayList<Advertisement> findAllByClientId(@PathVariable("id") Long id) {
        return advertisementService.findAllByClientId(id);
    }

    @GetMapping("/visiting/{id}/{packageType}")
    public ResponseEntity<Advertisement> getAdvertisement(@PathVariable Long id, @PathVariable String packageType) {
        boolean allowed = rateLimiterService.tryAcquire(packageType, id);
        if (!allowed) {
            return ResponseEntity.status(429).build();
        }
        Advertisement retVal = advertisementService.getById(id);
        return ResponseEntity.ok(retVal);
    }
    @GetMapping("/{id}")
    public Advertisement getById(@PathVariable("id") Long id) {
        return advertisementService.getById(id);
    }


}
