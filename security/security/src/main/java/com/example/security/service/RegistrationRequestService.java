package com.example.security.service;

import com.example.security.dto.RegistrationRequestDto;
import com.example.security.model.RequestStatus;
import com.example.security.repository.RegistrationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RegistrationRequestService implements  IRegistrationRequestService{
    @Autowired
    RegistrationRequestRepository registrationRequestRepository;

    @Override
    public com.example.security.model.RegistrationRequest save(RegistrationRequestDto registrationRequestDto) {
        com.example.security.model.RegistrationRequest request=new com.example.security.model.RegistrationRequest();
        request.setRequestStatus(RequestStatus.NEW);
        request.setCity(registrationRequestDto.getCity());
        request.setCountry(registrationRequestDto.getCountry());
        request.setName(registrationRequestDto.getName());
        request.setCountry(registrationRequestDto.getCountry());
        request.setPhone(registrationRequestDto.getPhone());
        request.setPackageType(registrationRequestDto.getPackageType());
        request.setStreet(registrationRequestDto.getStreet());
        request.setStreetNumber(registrationRequestDto.getStreetNumber());
        request.setPib(registrationRequestDto.getPib());
        request.setSurname(registrationRequestDto.getSurname());
        request.setUsername(registrationRequestDto.getUsername());
        request.setPassword(registrationRequestDto.getPassword());
        request.setType(registrationRequestDto.getType());
        request.setEmail(registrationRequestDto.getEmail());
        return this.registrationRequestRepository.save(request);
    }

    @Override
    public com.example.security.model.RegistrationRequest accept(com.example.security.model.RegistrationRequest request) {
        request.setRequestStatus(RequestStatus.ACCEPTED);
        return registrationRequestRepository.save(request);
    }

    @Override
    public com.example.security.model.RegistrationRequest reject(com.example.security.model.RegistrationRequest request) {
        request.setRequestStatus(RequestStatus.REJECTED);
        request.setRejectingDate(System.currentTimeMillis());
        return registrationRequestRepository.save(request);    }


    @Override
    public List<com.example.security.model.RegistrationRequest> findAll() {
        return this.registrationRequestRepository.findAll();
    }

    @Override
    public com.example.security.model.RegistrationRequest findById(Long id) {
        return registrationRequestRepository.findById(id).orElseGet(null);
    }
}
