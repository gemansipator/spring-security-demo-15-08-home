package site.javadev.springsecuritydemo1508home.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "person_security")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Поле не может быть пустым")
    @Size(min = 2, max = 20, message = "Поле должно быть от 2 до 20 символов")
    @Column(name = "username")
    private String username;

//    @NotEmpty(message = "Поле не может быть пустым")
//    @Size(min = 4,  message = "Значение должно быть от 4 символов")
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    public Person() {

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
