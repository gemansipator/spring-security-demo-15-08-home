package site.javadev.springsecuritydemo1508home.test_rest_template;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import site.javadev.springsecuritydemo1508home.test_rest_template.model.User;

public class Main {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://reqres.in/api/users/1";


//        String response = restTemplate.getForObject(url, String.class);
//        System.out.println(response);

//        Map<String, String> jsonToSend = new HashMap<>();
//        jsonToSend.put("name", "NikolayL");
//        jsonToSend.put("job", "devops");

        User user = new User("Alex", "Tech artist");

        HttpEntity<User> entity = new HttpEntity<>(user);

        User response = restTemplate.postForObject(url, entity, User.class);

        System.out.println(response);

    }
}
