package site.javadev.springsecuritydemo1508home.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.javadev.springsecuritydemo1508home.dto.PersonDTO;
import site.javadev.springsecuritydemo1508home.model.Person;
import site.javadev.springsecuritydemo1508home.repositories.PeopleRepository;

import java.util.Optional;

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

    public Optional<Person> findByUsername(String username) {
        return peopleRepository.findByUsername(username);
    }

    public Optional<Person> findById(Long id) {
        return peopleRepository.findById(id);
    }

    public boolean updatePerson(Long id, PersonDTO updatedPerson) {
        peopleRepository.findById(id).ifPresent(person -> {
            person.setUsername(updatedPerson.getUsername());
            person.setEmail(updatedPerson.getEmail());
            person.setYearOfBirth(updatedPerson.getYearOfBirth());
            if (!updatedPerson.getPassword().isEmpty()) {
                person.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
            }
            peopleRepository.save(person);
        });
        return false;
    }

    public boolean deletePerson(Long id) {
        peopleRepository.deleteById(id);
        return false;
    }

    public Person convertDTOToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertPersonToDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
