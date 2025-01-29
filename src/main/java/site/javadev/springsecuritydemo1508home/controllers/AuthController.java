package site.javadev.springsecuritydemo1508home.controllers;


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

@Controller
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

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login"; // Теперь указывает на auth/login.html
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(userToken);
        } catch (Exception e) {
            return Map.of("error", "incorrect login or password");
        }

        String token = jwtUtil.generateToken(username);
        return Map.of("jwt-token", token);
    }



    @GetMapping("/registration")
    public String showRegistrationPage() {
        return "auth/registration"; // Указывает на templates/auth/registration.html
    }

    @PostMapping("/registration")
    public Map<String, String> registration(@RequestBody @Valid PersonDTO personDTO,
                                            BindingResult bindingResult) {
        System.out.println("Полученные данные: " + personDTO);

        Person person = peopleService.convertDTOToPerson(personDTO);
        System.out.println("Сконвертированный объект: " + person);

        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            return Map.of("message", "error body");
        }

        peopleService.savePerson(person);
        System.out.println("Человек сохранен в базе");

        String token = jwtUtil.generateToken(person.getUsername());
        System.out.println("Сгенерированный токен: " + token);

        return Map.of("jwt-token", token);
    }


    @GetMapping("/show")
    public String showAuthenticatedUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails principal = (PersonDetails) authentication.getPrincipal();

        return principal.getUsername();

    }
}
