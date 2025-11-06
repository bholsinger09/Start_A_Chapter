package com.turningpoint.chapterorganizer;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootApplication
public class TestMemberCreation {
    
    public static void main(String[] args) {
        // Start the Spring Boot application
        ConfigurableApplicationContext context = SpringApplication.run(TestMemberCreation.class, args);
        
        try {
            // Wait a bit for the application to fully start
            Thread.sleep(10000);
            
            // Test member creation with missing chapter
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/members";
            
            Member member = new Member();
            member.setFirstName("Test");
            member.setLastName("User");
            member.setEmail("test@example.com");
            // Deliberately not setting chapter to trigger the error
            
            try {
                Member response = restTemplate.postForObject(url, member, Member.class);
                System.out.println("Member created successfully: " + response);
            } catch (HttpClientErrorException e) {
                System.out.println("Error creating member: " + e.getStatusCode());
                System.out.println("Response body: " + e.getResponseBodyAsString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the application context
            context.close();
        }
    }
}