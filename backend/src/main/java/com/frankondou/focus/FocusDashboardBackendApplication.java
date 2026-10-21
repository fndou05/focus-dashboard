package com.frankondou.focus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// App entry point; auto-configures and scans this package and below
@SpringBootApplication
public class FocusDashboardBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FocusDashboardBackendApplication.class, args);
    }
}