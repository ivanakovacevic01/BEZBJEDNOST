package com.example.security.service;

import com.example.security.model.ActivationLink;
import com.example.security.repository.ActivationLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ActivationLinkService implements IActivationLinkService{
    @Autowired
    private ActivationLinkRepository activationLinkRepository;
    @Override
    public boolean isLinkAlreadyUsed(String token) {
        Optional<ActivationLink> activationLink=this.activationLinkRepository.findByToken(token);
        return activationLink.isPresent();
    }

    @Override
    public ActivationLink create(String username, String token) {
        ActivationLink activationLink=new ActivationLink();
        activationLink.setToken(token);
        activationLink.setUsername(username);
        return this.activationLinkRepository.save(activationLink);
    }
}
