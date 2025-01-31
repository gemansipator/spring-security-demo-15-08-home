package site.javadev.springsecuritydemo1508home.controllers;


import com.zaxxer.hikari.HikariConfig;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, String> login(@RequestBody PersonDTO authDTO) {
        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

        try {
            authenticationManager.authenticate(userToken);
        } catch (Exception e) {
            return Map.of("error", "incorrect login or password");
        }

        String token = jwtUtil.generateToken(authDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/registration")
    public Map<String, String> registration(@RequestBody @Valid PersonDTO personDTO) {
        Person person = peopleService.convertDTOToPerson(personDTO);
        peopleService.savePerson(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return Map.of("jwt-token", token);
    }

    @GetMapping("/user/{id}")
    public Optional<PersonDTO> getUserById(@PathVariable Long id) {
        return peopleService.findById(id).map(peopleService::convertPersonToDTO);
    }

    @PutMapping("/user/{id}")
    public Map<String, String> updateUser(@PathVariable Long id, @RequestBody @Valid PersonDTO updatedPerson) {
        peopleService.updatePerson(id, updatedPerson);
        return Map.of("message", "User updated successfully");
    }

    @DeleteMapping("/user/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        peopleService.deletePerson(id);
        return Map.of("message", "User deleted successfully");
    }

    @GetMapping("/show")
    public String showAuthenticatedUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails principal = (PersonDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
}
