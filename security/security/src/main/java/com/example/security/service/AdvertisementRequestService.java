package com.example.security.service;

import com.example.security.model.*;
import com.example.security.repository.AddressRepository;
import com.example.security.repository.AdvertisementRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;

@Service
public class AdvertisementRequestService implements  IAdvertisementRequestService{

    @Autowired
    private AdvertisementRequestRepository advertisementRequestRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressRepository addressRepository;
    @Override
    public ArrayList<AdvertisementRequest> getAll() {
        return (ArrayList<AdvertisementRequest>) advertisementRequestRepository.findAll();
    }

    @Override
    public AdvertisementRequest save(AdvertisementRequest request,Long id) {
        request.setRequestStatus(RequestStatus.NEW);
        Client c=this.clientService.getByUserId(id);
        request.setClient(c);
        return this.advertisementRequestRepository.save(request);

    }

    @Override
    public AdvertisementRequest getById(Long id){
        return this.advertisementRequestRepository.getById(id);
    }

    @Override
    public AdvertisementRequest update(AdvertisementRequest request){
        request.setRequestStatus(RequestStatus.ACCEPTED);
        return this.advertisementRequestRepository.save(request);
    }

    @Override
    public AdvertisementRequest rejectRequest(AdvertisementRequest request) {
        Client client = clientService.getById(request.getClient().getId());
        if (client == null) {
            throw new EntityNotFoundException("Client not found");
        }
        request.setClient(client);
        request.setRequestStatus(RequestStatus.REJECTED);
        return advertisementRequestRepository.save(request);
    }
    @Override
    public ArrayList<AdvertisementRequest> findAllByClientId(Long id) {
        return advertisementRequestRepository.findAllByClientId(id);
    }

    public void deleteAll(ArrayList<AdvertisementRequest> advertisementRequests) {
        advertisementRequestRepository.deleteAll(advertisementRequests);
    }
    public Long getClientIdByRequestId(Long id){
        return this.advertisementRequestRepository.getClientIdByRequestId(id);
    }
}
