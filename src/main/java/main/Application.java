package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service", "dao", "config", "model", "dto", "exception","main"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("=================================================");
        System.out.println("   SERVER SPRING BOOT ĐÃ CHẠY TẠI PORT 8080!");
        System.out.println("   API URL: http://localhost:8080");
        System.out.println("=================================================");
    }
}