package org.xenon.echo.services.tests.integration;

import io.jsonwebtoken.security.Password;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.services.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("active")
@AutoConfigureMockMvc
public class AuthIntegrationTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @MockitoBean
        private EmailService emailService;

        @BeforeEach
        void setup(){
            userRepository.deleteAll();
        }

        @Test
        void shouldRegisterUserSuccessfully() throws Exception{
            String request = """
             {
                    "name":"Test Dude",
                    "email":"test@mail.com",
                    "password":"Password123"
             }
            """;

            mockMvc.perform(
                    post("/auth/register")
                            .contentType("application/json")
                            .content(request)
                    ).andExpect(status().isCreated());

            User user = userRepository.findByEmail("test@mail.com").orElse(null);
            assertNotNull(user);
            assertEquals("test@mail.com",user.getEmail());
            assertFalse(user.isVerified());
            verify(emailService).sendVerificationEmail(any(),any());
        }

        @Test
        void shouldBlockLoginWhenUserNotVerified() throws Exception{
            User user = new User();
            user.setEmail("test@mail.com");
            user.setPassword(passwordEncoder.encode("Password123"));
            user.setVerified(false);
            user.setRole(Role.USER);

            userRepository.save(user);

            String request = """
              {
              "email":"test@mail.com",
              "password":"Password123"
              }
            """;
            mockMvc.perform(
                    post("/auth/login")
                            .contentType("application/json")
                            .content(request)
            ).andExpect(status().isBadRequest());
        }
}
