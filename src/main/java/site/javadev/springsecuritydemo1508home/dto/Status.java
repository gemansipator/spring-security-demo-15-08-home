package site.javadev.springsecuritydemo1508home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Status {
    private Long statusCode;
    private String statusDesc;
}
