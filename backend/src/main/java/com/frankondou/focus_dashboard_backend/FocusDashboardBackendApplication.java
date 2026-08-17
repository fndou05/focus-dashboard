package com.frankondou.focus_dashboard_backend;

import java.lang.String;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FocusDashboardBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FocusDashboardBackendApplication.class, args);
        System.out.print("");
	}

}

@RestController
class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from the backend";
    }
}

System.out.println()