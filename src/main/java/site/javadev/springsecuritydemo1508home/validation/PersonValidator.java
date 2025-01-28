package site.javadev.springsecuritydemo1508home.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import site.javadev.springsecuritydemo1508home.model.Person;
import site.javadev.springsecuritydemo1508home.security.PersonDetailsService;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonValidator(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        try {
            personDetailsService.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException e) {
            return;
        }

        errors.rejectValue("username", "user.found.name",
                "User with name " + person.getUsername() + " already exists");
    }





    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }
}
