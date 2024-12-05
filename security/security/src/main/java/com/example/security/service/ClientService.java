package com.example.security.service;

import com.example.security.model.*;
import com.example.security.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService implements IClientService {
    //@Autowired
    //private PasswordEncoder passwordEncoder;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private  TfaAuthentication tfaAuthentication;
    @Override
    public Client save(RegistrationRequest userRequest) {
        Client client=new Client();
        Address address=new Address();
        address.setCity(userRequest.getCity());
        address.setCountry(userRequest.getCountry());
        address.setStreet(userRequest.getStreet());
        address.setStreetNumber(userRequest.getStreetNumber());
        client.setPhone(userRequest.getPhone());
        client.setType(userRequest.getType());
        client.setPackageType(userRequest.getPackageType());
        if(client.getType().equals(ClientType.PRAVNO))
            client.setPib(userRequest.getPib());
        User user=new User();
        user.setBlocked(false);
        user.setSecret(tfaAuthentication.generateNewSecret());
        user.setEmail(userRequest.getEmail());
        user.setEnabled(false);
        user.setFirstName(userRequest.getName());
        user.setLastName(userRequest.getSurname());
        user.setUsername(userRequest.getUsername());
        user.setAddress(address);
        user.setPhone(userRequest.getPhone());
        String salt=generateSalt();
        user.setSalt(salt);
        //user.setPassword(passwordEncoder.encode(userRequest.getPassword()+salt));
        List<Role> roles = roleService.findByName("ROLE_CLIENT");
        user.setRoles(roles);
        client.setUser(user);
        clientRepository.save(client);
        return null;
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @Override
    public Client getByUserId(Long email) {
        Client u= clientRepository.getByUserId(email);
        return u;
    }

    @Override
    public Client getById(Long email) {
        Client u= clientRepository.getById(email);
        return u;
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}
