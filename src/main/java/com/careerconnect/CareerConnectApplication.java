package com.careerconnect;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CareerConnectApplication {

    public static void main(String[] args) {
        loadDotenv();
        SpringApplication.run(CareerConnectApplication.class, args);
    }

    public static void loadDotenv() {
//            System.out.println("DB Driver: " + System.getProperty("DB_DRIVER"));
//            System.out.println("DB URL: " + System.getProperty("DB_URL"));
//            System.out.println("DB Username: " + System.getProperty("DB_USERNAME"));
        try {
            Dotenv dotenv = Dotenv.configure().load();
//            System.setProperty("DB_DRIVER", dotenv.get("DB_DRIVER"));
//            System.setProperty("DB_URL", dotenv.get("DB_URL"));
//            System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));


            // Kiểm tra các giá trị được lấy từ dotenv
//            System.out.println("DB Driver: " + System.getProperty("DB_DRIVER"));
//            System.out.println("DB URL: " + System.getProperty("DB_URL"));
//            System.out.println("DB Username: " + System.getProperty("DB_USERNAME"));

            //for
            for (var key : dotenv.entries()) {
//            System.out.println(key.getKey() + " = " + key.getValue());
                System.setProperty(key.getKey(), key.getValue());
            }
        }catch (Exception ignored) {}

    }
}
