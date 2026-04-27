package org.xenon.echo.services.tests.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.xenon.echo.entities.User;
import org.xenon.echo.repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setup(){
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setVerified(false);
    }

    @Test
    void shouldSaveUserToDatabase(){
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());

        User fromDb = userRepository.findByEmail("test@mail.com").orElse(null);
        assertNotNull(fromDb);
        assertEquals("test@mail.com",fromDb.getEmail());
    }
}
