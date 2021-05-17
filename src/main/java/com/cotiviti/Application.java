package com.cotiviti;

import com.cotiviti.datainit.DatabaseInitialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cotiviti"})
public class Application implements CommandLineRunner {

    @Autowired
    private DatabaseInitialization databaseInitialization;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseInitialization.init();
    }
}
