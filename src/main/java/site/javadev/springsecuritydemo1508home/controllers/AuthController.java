package site.javadev.springsecuritydemo1508home.controllers;


import com.zaxxer.hikari.HikariConfig;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.javadev.springsecuritydemo1508home.dto.PersonDTO;
import site.javadev.springsecuritydemo1508home.model.Person;
import site.javadev.springsecuritydemo1508home.security.PersonDetails;
import site.javadev.springsecuritydemo1508home.service.PeopleService;
import site.javadev.springsecuritydemo1508home.util.JWTUtil;
import site.javadev.springsecuritydemo1508home.validation.PersonValidator;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final PeopleService peopleService;
    private final JWTUtil jwtUtil;
    private final ModelMapper mapper;
    private final PersonValidator personValidator;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(PeopleService peopleService, JWTUtil jwtUtil, ModelMapper mapper, PersonValidator personValidator, AuthenticationManager authenticationManager) {
        this.peopleService = peopleService;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.personValidator = personValidator;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody PersonDTO authDTO) {
        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

        try {
            authenticationManager.authenticate(userToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Incorrect login or password"));
        }

        String token = jwtUtil.generateToken(authDTO.getUsername());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> registration(@RequestBody @Valid PersonDTO personDTO) {
        Person person = peopleService.convertDTOToPerson(personDTO);
        peopleService.savePerson(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("jwt-token", token));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PersonDTO> getUserById(@PathVariable Long id) {
        return peopleService.findById(id)
                .map(person -> ResponseEntity.ok(peopleService.convertPersonToDTO(person)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody @Valid PersonDTO updatedPerson) {
        boolean isUpdated = peopleService.updatePerson(id, updatedPerson);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "User updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        boolean isDeleted = peopleService.deletePerson(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @GetMapping("/show")
    public ResponseEntity<String> showAuthenticatedUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof PersonDetails principal) {
            return ResponseEntity.ok(principal.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
