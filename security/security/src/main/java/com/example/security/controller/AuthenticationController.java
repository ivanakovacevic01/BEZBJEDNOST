package com.example.security.controller;
import com.example.security.dto.*;
import io.jsonwebtoken.*;

import java.util.*;
import com.example.security.model.Client;
import com.example.security.model.PackageType;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.service.*;
import com.example.security.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.accessibility.AccessibleTable;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    @Autowired
    private TokenUtils tokenUtils;

    //@Autowired
    //private AuthenticationManager authenticationManager;
    @Autowired
    private IUserService userService;
    @Autowired
    private IClientService clientService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ActivationLinkService activationLinkService;
    @Autowired
    private TfaAuthentication tfaAuthentication;

   /* @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        User user=this.userService.findByEmail(authenticationRequest.getEmail());
        if(user!=null && !user.isBlocked()) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword() + user.getSalt()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
         /*   List<String> roles = new ArrayList<>();
            for (Role role : user.getRoles())
                roles.add(role.getName());
            String accessToken = tokenUtils.generateToken(user.getEmail(), roles);
            String refreshToken = tokenUtils.generateRefreshToken(user.getEmail());*/
            /*UserTokenState userTokenState=new UserTokenState();
            userTokenState.setSecretImageUri(tfaAuthentication.generateQRCodeUri(user.getSecret()));
            return ResponseEntity.ok(userTokenState);
        }else{
            return new ResponseEntity<UserTokenState>(HttpStatus.UNAUTHORIZED);
        }
    }*/

    @PostMapping("/verify")
    public ResponseEntity<UserTokenState> verifyTfaCode(
            @RequestBody VerificationRequestDto verificationRequest, HttpServletResponse response) {
        User user=this.userService.findByEmail(verificationRequest.getEmail());
        if(user!=null && !user.isBlocked()) {
            if (tfaAuthentication.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
                throw new BadCredentialsException("Code is not correct");
            }
            List<String> roles = new ArrayList<>();
            for (Role role : user.getRoles())
                roles.add(role.getName());
            String accessToken = tokenUtils.generateToken(user.getEmail(), roles);
            String refreshToken = tokenUtils.generateRefreshToken(user.getEmail());
            UserTokenState userTokenState=new UserTokenState();
            userTokenState.setAccesstoken(accessToken);
            userTokenState.setRefreshtoken(refreshToken);
            return ResponseEntity.ok(userTokenState);
        }else{
            return new ResponseEntity<UserTokenState>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/passwordlesslogin")
    public ResponseEntity<String> passwordlessLogin(
            @RequestBody PasswordlessLoginRequest authenticationRequest, HttpServletResponse response) {
        System.out.println(authenticationRequest.getEmail());
        User user=this.userService.findByEmail(authenticationRequest.getEmail());
        if(user!=null && !user.isBlocked()) {
            Client client=clientService.getByUserId(user.getId());
            if(client.getPackageType().equals(PackageType.GOLD) || client.getPackageType().equals(PackageType.STANDARD) ){
                emailService.sendPassworlessLogingEmail(user.getEmail());
                return ResponseEntity.ok("Check email");
            }else{
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/passwordlessAuth")
    public ResponseEntity<UserTokenState> passwordlessAuth(
            @RequestBody PasswordlessAuthRequest authenticationRequest) {
        boolean isSignatureValid = HMACUtils.generateHMACSignature(authenticationRequest.getToken()).equals(authenticationRequest.getSignature());

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey("SECRET")
                .parseClaimsJws(authenticationRequest.getToken());
        String username = claims.getBody().getSubject();
        long timestamp = claims.getBody().get("timestamp", Long.class);

        long currentTime = System.currentTimeMillis();
        boolean isTokenValid= (currentTime - timestamp) < 10 * 60 * 1000;//10 min
        System.out.println(currentTime);
        System.out.println(timestamp);

        boolean isTokenUsed=this.activationLinkService.isLinkAlreadyUsed(authenticationRequest.getToken());
        System.out.println(isTokenValid+" "+isSignatureValid+" "+isTokenUsed);

        if (/*isSignatureValid && */!isTokenUsed /*&& isTokenValid*/) {;
            activationLinkService.create(username,authenticationRequest.getToken());
            List<String> roles = new ArrayList<>();
            User user=this.userService.findByEmail(username);
            for (Role role : user.getRoles())
                roles.add(role.getName());
            String accessToken = tokenUtils.generateToken(user.getEmail(), roles);
            String refreshToken = tokenUtils.generateRefreshToken(user.getEmail());
            HttpHeaders responseHeaders = new HttpHeaders();

            responseHeaders.add("accessToken",
                    accessToken);
            responseHeaders.add("refreshToken",
                    refreshToken);

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(new UserTokenState());
        }
       else{
            return new ResponseEntity<UserTokenState>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/regeneratingJwtToken")
    public ResponseEntity<UserTokenState> regeneratingJwtToken(
            @RequestBody UserTokenState regeneratingAccessToken) {

        try {
            // Validate refresh token
            Jws<Claims> claimsRefresh = Jwts.parser()
                    .setSigningKey("SECRET")
                    .parseClaimsJws(regeneratingAccessToken.getRefreshtoken());

            String usernameRefresh = claimsRefresh.getBody().getSubject();
            Date expirationRefresh = claimsRefresh.getBody().get("exp", Date.class);
            Date now = new Date();

            if (now.getTime() < expirationRefresh.getTime()) {
                User user = this.userService.findByEmail(usernameRefresh);
                if (user == null) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                List<String> roles = new ArrayList<>();
                for (Role role : user.getRoles()) {
                    roles.add(role.getName());
                }

                String accessToken = tokenUtils.generateToken(user.getEmail(), roles);

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add("accessToken",
                        accessToken);

                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(new UserTokenState());
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestBody PasswordlessLoginRequest authenticationRequest, HttpServletResponse response) {
        User user=this.userService.findByEmail(authenticationRequest.getEmail());
        if(user!=null && !user.isBlocked()) {
            userService.resetToken(user.getEmail());
            return ResponseEntity.ok("Check email");
        }else{
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/setNewPassword")
    public ResponseEntity<String> setNewPassword(
            @RequestBody PasswordChangeDto request, HttpServletResponse response) {
        User user=userService.findByEmail(request.getEmail());
        if(user.getResetToken().equals(request.getToken())) {
            userService.setNewPassword(request.getEmail(), request.getPassword());
            return ResponseEntity.ok("Password changed");
        }else{
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

}
