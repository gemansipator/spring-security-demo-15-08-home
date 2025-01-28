package site.javadev.springsecuritydemo1508home.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.javadev.springsecuritydemo1508home.dto.PersonDTO;
import site.javadev.springsecuritydemo1508home.model.Person;
import site.javadev.springsecuritydemo1508home.repositories.PeopleRepository;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }


    public void savePerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");

        peopleRepository.save(person);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void doAdminSomething() {
        System.out.println("Admin is doing something");
    }


    public Person convertDTOToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertPersonToDTO(Person byId) {
        return modelMapper.map(byId, PersonDTO.class);
    }

}
