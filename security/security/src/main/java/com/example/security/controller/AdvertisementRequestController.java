package com.example.security.controller;

import com.example.security.model.AdvertisementRequest;
import com.example.security.model.User;
import com.example.security.service.IAdvertisementRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/advertisementRequest", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class AdvertisementRequestController {

    @Autowired
    private IAdvertisementRequestService advertisementRequestService;
    @PreAuthorize("hasRole('employee')")
    @GetMapping("")
    public ArrayList<AdvertisementRequest> getAll() {
        return advertisementRequestService.getAll();
    }
    @PreAuthorize("hasRole('client')")

    @PostMapping("/creating/{id}")
    public AdvertisementRequest save(@RequestBody AdvertisementRequest request,@PathVariable("id") Long id) {
        if(request.getActivityEnd()!=null && request.getActivityStart()!=null && request.getExpirationTime()!=null && !request.getDescription().equals("")
        && (new Date(request.getExpirationTime().getTime())).after(new Date()) && (new Date(request.getActivityStart().getTime()).before(new Date(request.getActivityEnd().getTime()))) && (new Date(request.getActivityEnd().getTime()).before(new Date(request.getExpirationTime().getTime()))))
             return advertisementRequestService.save(request,id);
        return null;
    }
    @PreAuthorize("hasRole('employee')")
    @GetMapping("/{id}")
    public AdvertisementRequest getById(@PathVariable("id") Long id) {
        return advertisementRequestService.getById(id);
    }
    @PreAuthorize("hasRole('employee')")
    @GetMapping("/clientId/{id}")
    public Long getClientIdByRequestId(@PathVariable("id") Long id) {
        return advertisementRequestService.getClientIdByRequestId(id);
    }
    @PreAuthorize("hasRole('employee')")
    @PutMapping("/reject")
    public AdvertisementRequest rejectRequest(@RequestBody AdvertisementRequest request) {
        return advertisementRequestService.rejectRequest(request);
    }

}
