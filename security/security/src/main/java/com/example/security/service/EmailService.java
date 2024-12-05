package com.example.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    public void sendPassworlessLogingEmail(String email) {
        String activationLink=HMACUtils.generateLogingLink(email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Log in");
        message.setText("Click the following link to log in: " + activationLink);
        javaMailSender.send(message);
    }
    public void sendActivationEmail(String recipientEmail,String username) {
        String activationLink=HMACUtils.generateActivationLink(username);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Activate Your Account");
        message.setText("Click the following link to activate your account: " + activationLink);
        javaMailSender.send(message);
    }

    public void sendRejectingExplanation(String recipientEmail, String explanation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your registration request is rejected");
        message.setText("Reason for rejecting id: " + explanation);
        javaMailSender.send(message);
    }

    public void sendResetToken(String recipientEmail,String token) {
        String activationLink=HMACUtils.generateResetTokenLink(token,recipientEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Reset password");
        message.setText("Click the following link to reset your password: " + activationLink);
        javaMailSender.send(message);
    }
}