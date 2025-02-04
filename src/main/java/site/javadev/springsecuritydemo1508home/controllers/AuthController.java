package site.javadev.springsecuritydemo1508home.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import site.javadev.springsecuritydemo1508home.dto.PersonDTO;
import site.javadev.springsecuritydemo1508home.model.Person;
import site.javadev.springsecuritydemo1508home.security.PersonDetails;
import site.javadev.springsecuritydemo1508home.service.PeopleService;
import site.javadev.springsecuritydemo1508home.util.JWTUtil;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor // Lombok автоматически генерирует конструктор для финальных полей
public class AuthController {

    private final PeopleService peopleService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Обработчик запроса на вход пользователя.
     * Аутентифицирует пользователя и возвращает JWT-токен.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid PersonDTO authDTO) {
        // Создаем объект для аутентификации пользователя
        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

        try {
            // Аутентифицируем пользователя
            authenticationManager.authenticate(userToken);
        } catch (Exception e) {
            // Если аутентификация не удалась, возвращаем ошибку 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Incorrect login or password"));
        }

        // Генерируем JWT-токен для пользователя
        String token = jwtUtil.generateToken(authDTO.getUsername());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    /**
     * Обработчик регистрации нового пользователя.
     * Сохраняет пользователя в базе и возвращает JWT-токен.
     */
    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> registration(@RequestBody @Valid PersonDTO personDTO) {
        // Проверяем, что пароль не пустой и содержит не менее 4 символов
        if (personDTO.getPassword() == null || personDTO.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Password must be at least 4 characters"));
        }

        // Проверяем, существует ли пользователь с таким именем
        Optional<Person> existingPerson = peopleService.findByUsername(personDTO.getUsername());
        if (existingPerson.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already exists"));
        }

        // Передаем DTO в сервис, который преобразует его в Person и сохраняет
        Person person = peopleService.registerPerson(personDTO);

        // Генерируем JWT-токен для нового пользователя
        String token = jwtUtil.generateToken(person.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("jwt-token", token));
    }

    /**
     * Получение информации о пользователе по его ID.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<PersonDTO> getUserById(@PathVariable Long id) {
        return peopleService.findById(id)
                .map(person -> ResponseEntity.ok(peopleService.convertPersonToDTO(person)))
                .orElse(ResponseEntity.notFound().build()); // Если пользователь не найден, возвращаем 404
    }

    /**
     * Обновление информации о пользователе.
     */
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

    /**
     * Удаление пользователя по ID.
     */
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

    /**
     * Возвращает имя текущего аутентифицированного пользователя.
     */
    @GetMapping("/show")
    public ResponseEntity<String> showAuthenticatedUsers() {
        // Получаем объект аутентификации из SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof PersonDetails principal) {
            return ResponseEntity.ok(principal.getUsername()); // Возвращаем имя пользователя
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
