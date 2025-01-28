package site.javadev.springsecuritydemo1508home.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.javadev.springsecuritydemo1508home.model.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUsername(String username);
}
