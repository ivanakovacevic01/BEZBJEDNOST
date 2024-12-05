package com.example.security.controller;

import com.example.security.model.*;
import com.example.security.service.*;

import javax.websocket.server.PathParam;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IClientService clientService;
    @Autowired
    private IActivationLinkService activationLinkService;
    @Autowired
    private TfaAuthentication tfaAuthentication;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token,
                                                  @RequestParam("signature") String hmacSignature) {
//        boolean isSignatureValid = HMACUtils.generateHMACSignature(token).equals(hmacSignature);
        logger.info("activateAccount method in UserController started.");
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey("SECRET")
                .parseClaimsJws(token);
        String username = claims.getBody().getSubject();
        long timestamp = claims.getBody().get("timestamp", Long.class);
        long currentTime = System.currentTimeMillis();
        System.out.println(timestamp);
        System.out.println(currentTime);
        boolean isTokenValid = ((currentTime - timestamp) < 10 * 60 * 1000);//10 min
        boolean isTokenUsed = this.activationLinkService.isLinkAlreadyUsed(token);
        System.out.println("TTTTTTT"+isTokenValid+isTokenUsed);

        if (isTokenValid && !isTokenUsed) {
            userService.enable(username);
            activationLinkService.create(username, token);
            return ResponseEntity.ok("Vaš nalog je uspešno aktiviran!");
        } else if (isTokenUsed) {
            return ResponseEntity.badRequest().body("Već iskorišćen aktivacioni link.");
        } else {
            return ResponseEntity.badRequest().body("Neispravan ili istekao aktivacioni link.");
        }
    }
    @PreAuthorize("hasRole('employee')")
    @GetMapping("/employeeProfile/{email}")
    public User getEmployee(@PathVariable("email") String email) {
        logger.info("getEmployee method in UserController started.");
        System.out.println(email);
        return userService.findByEmail(email);
    }
    @PreAuthorize("hasAnyRole('employee','admin','client')")
    @PutMapping("/update/{hashPassword}/{role}")
    public User update(@RequestBody User user, @PathVariable("hashPassword") Boolean hashPassword, @PathVariable("role") String role) {
        logger.info("update method in UserController started.");
        if(user.getEmail()!=null) {
            boolean isEmailUnique = true;
            User storedUserWithSameEmail = userService.findByEmail(user.getEmail());

            if(storedUserWithSameEmail!=null && storedUserWithSameEmail.getId()!=user.getId())
                isEmailUnique = false;

            if(isEmailUnique) {
                String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
                boolean isPasswordValid = true;
                if (hashPassword)
                    isPasswordValid = user.getPassword().matches(regex);
                boolean isInvalidFields;

                if(role.equals("ROLE_CLIENT")) {
                    Client storedClient = clientService.getByUserId(user.getId());
                    if(storedClient.getType().toString().equals("FIZICKO"))
                        isInvalidFields = user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getPhone().isEmpty() || user.getAddress().getCountry().isEmpty()
                                || user.getAddress().getCity().isEmpty() || user.getAddress().getStreet().isEmpty() ||
                                user.getAddress().getStreetNumber().isEmpty() || !isPasswordValid;
                    else
                        isInvalidFields = user.getPhone().isEmpty() || user.getAddress().getCountry().isEmpty()
                                || user.getAddress().getCity().isEmpty() || user.getAddress().getStreet().isEmpty() ||
                                user.getAddress().getStreetNumber().isEmpty() || !isPasswordValid;

                }

                else
                    isInvalidFields = user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getPhone().isEmpty() || user.getAddress().getCountry().isEmpty()
                        || user.getAddress().getCity().isEmpty() || user.getAddress().getStreet().isEmpty() ||
                        user.getAddress().getStreetNumber().isEmpty() || !isPasswordValid;
                if (isInvalidFields)
                    return null;
                return userService.save(user, hashPassword, role);
            }
            else
                return null;
        } else
            return null;

    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/adminProfile/{email}")
    public User getAdmin(@PathVariable("email") String email) {
        System.out.println(email);
        logger.info("getAdmin method in UserController started.");
        return userService.findByEmail(email);
    }
    @PreAuthorize("hasRole('client')")
    @GetMapping("/clientProfile/{email}")
    public User getClientProfile(@PathVariable("email") String email) {
        logger.info("getClientProfile method in UserController started.");
        return userService.findByEmail(email);
    }
    @PreAuthorize("hasRole('client')")
    @GetMapping("/client/{email}")
    public Client getClient(@PathVariable("email") String email) {
        logger.info("getClient method in UserController started.");
        return clientService.findByEmail(email);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/creatingUser/{role}")
    public User createUser(@RequestBody User user, @PathVariable String role) {
        logger.info("createUser method in UserController started.");
        if(user.getEmail()!=null && userService.findByEmail(user.getEmail())==null) {
            String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
            boolean isPasswordValid = user.getPassword().matches(regex);

            boolean isInvalidFields=user.getFirstName().isEmpty()  || user.getLastName().isEmpty() || user.getPhone().isEmpty() || user.getAddress().getCountry().isEmpty()
                    || user.getAddress().getCity().isEmpty() || user.getAddress().getStreet().isEmpty() ||
                    user.getAddress().getStreetNumber().isEmpty() || !isPasswordValid;
            if(isInvalidFields)
                return  null;
            user.setSecret(tfaAuthentication.generateNewSecret());
            user.setBlocked(false);
            return userService.saveUser(user, role);
        } else
            return null;

    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/employees")
    public ArrayList<User> getEmployees() {
        logger.info("getEmployees method in UserController started.");
        return userService.getEmployees();
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/clients")
    public ArrayList<User> getClients() {
        logger.info("getClients method in UserController started.");
        return userService.getClients();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePass credentials) {
        logger.info("changePassword method in UserController started.");
        User user = userService.findByEmail(credentials.getEmail());
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        boolean isPasswordValid = credentials.getPassword().matches(regex);

        if(!isPasswordValid)
            return new ResponseEntity<RegistrationRequest>(HttpStatus.BAD_REQUEST);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.isPasswordChanged()) {
            userService.changePassword(credentials.getEmail(), credentials.getPassword());
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Password already changed.");
        }
    }
    @PreAuthorize("hasAnyRole('employee','admin','client')")
    @GetMapping("/getAll/{email}")
    public User getUser(@PathVariable("email") String email) {
        logger.info("getUser method in UserController started.");
        return userService.findByEmail(email);
    }
    @GetMapping("/firstLogin/{email}")
    public ResponseEntity<?> authenticateUser(@PathVariable("email") String email) {
        logger.info("authenticateUser method in UserController started.");
        System.out.println("mejl "+email);
        User user=userService.findByEmail(email);
        if (user.isPasswordChanged()) {
            Map<String, Object> response = new HashMap<>();
            response.put("isPasswordChanged", true);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/block/{email}")
    public ResponseEntity<List<User>> blockUser(@PathVariable("email") String email){
        logger.info("blockUser method in UserController started.");
        return ResponseEntity.ok(userService.block(email));
    }
    @PreAuthorize("@permissionService.hasPermission('ALL_USERS_BLOCKING')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getClientsAndEmployees(){
        logger.info("getClientsAndEmployees method in UserController started.");
        return ResponseEntity.ok(userService.getAll());
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/unblock/{email}")
    public ResponseEntity<List<User>> unblockUser(@PathVariable("email") String email){
        logger.info("unblockUser method in UserController started.");
        return ResponseEntity.ok(userService.unblock(email));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('client')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        logger.info("deleteUser method in UserController started.");
        userService.deleteUserData(id);
        logger.info("deleteUser method in UserController completed.");
        return ResponseEntity.ok("Obrisan.");
    }

}
