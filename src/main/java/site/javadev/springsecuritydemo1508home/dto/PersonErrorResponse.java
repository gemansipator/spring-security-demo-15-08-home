package site.javadev.springsecuritydemo1508home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PersonErrorResponse {
    private Date date;
    private Status status;
}
