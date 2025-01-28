package site.javadev.springsecuritydemo1508home.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.javadev.springsecuritydemo1508home.security.PersonDetails;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails principal = (PersonDetails) authentication.getPrincipal();

        System.out.println(principal.getUsername());

        return "hello";
    }

}
