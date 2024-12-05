package com.example.security.service;

import com.example.security.model.Advertisement;
import com.example.security.model.AdvertisementRequest;
import com.example.security.model.Client;
import com.example.security.model.RequestStatus;
import com.example.security.repository.AdvertisementRepository;
import com.example.security.repository.AdvertisementRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class AdvertisementService implements  IAdvertisementService{
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private AdvertisementRequestRepository advertisementRequestRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AdvertisementRequestService advertisementRequestService;

    @Override
    public ArrayList<Advertisement> getAll() {
        return (ArrayList<Advertisement>) advertisementRepository.findAll();
    }

    @Override
    public Advertisement save(Advertisement advertisement,Long id, Long requestId) {
        AdvertisementRequest request=advertisementRequestService.getById(requestId);
        advertisement.setClient(request.getClient());
        advertisement.setRequest(request);
        if(new Date(advertisement.getRequest().getExpirationTime().getTime()).before(new Date())){
            request.setRequestStatus(RequestStatus.REJECTED);
            advertisementRequestRepository.save(request);
            return null;
        }
        advertisementRequestService.update(request);
        return advertisementRepository.save(advertisement);
    }

    @Override
    public ArrayList<Advertisement> findAllByClientId(Long id) {
        return advertisementRepository.findAllByClientId(id);
    }

    public void deleteAll(ArrayList<Advertisement> advertisements) {
        advertisementRepository.deleteAll(advertisements);
    }
    @Override
    public Advertisement getById(Long id) {
        return advertisementRepository.getById(id);
    }
}
