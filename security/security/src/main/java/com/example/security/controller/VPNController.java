package com.example.security.controller;

import com.example.security.dto.VPNMessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/api/vpn")
@CrossOrigin
public class VPNController {

    @GetMapping("/get-message")
    public ResponseEntity<VPNMessageDto> getMessage(){
        RestTemplate restTemplate=new RestTemplate();
        String response=restTemplate.getForObject("http://10.13.13.1:3000/",String.class);
        return ResponseEntity.ok(new VPNMessageDto(response));
    }
}
