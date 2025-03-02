package site.javadev.springsecuritydemo1508home.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
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

    @Column(name = "email")
    private String email;

}
