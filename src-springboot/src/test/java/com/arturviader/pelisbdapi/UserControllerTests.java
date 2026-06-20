package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.exception.AlreadyRegistreredEmail;
import com.arturviader.pelisbdapi.exception.AlreadyRegistreredUserName;
import com.arturviader.pelisbdapi.exception.EmailNotConfirmed;
import com.arturviader.pelisbdapi.exception.UserNotFound;
import com.arturviader.pelisbdapi.service.EmailServiceForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=test")
public class UserControllerTests {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${api.key:TEST-API-KEY}")
    private String apiKey;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailServiceForTests emailServiceForTests;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(post("/").header(API_KEY_HEADER, apiKey))
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional
    public void registerNewUserTestAndLogin() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"juanpalomp@mail.es\",\n" +
                                "  \"userName\": \"Juan\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        String body = emailServiceForTests.getLastText();
        String token = body.substring(body.indexOf("token=") + 6);
        Assertions.assertNotNull(token);

        mockMvc.perform(post("/api/auth/confirmemail")
                        .param("token", token))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "juanpalomp@mail.es",
                                  "password": "prova1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("token"));
    }

    @Test
    @Transactional
    public void registerNewUserEmailAlreadyExistsTest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"juan@mail.es\",\n" +
                        "  \"userName\": \"Juan\",\n" +
                        "  \"password\": \"prova1234\"\n" +
                        "}"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"juan@mail.es\",\n" +
                                "  \"userName\": \"Palomo\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isConflict())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof AlreadyRegistreredEmail));
    }

    @Test
    @Transactional
    public void registerNewUserUserNameAlreadyExistsTest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"juan@mail.es\",\n" +
                        "  \"userName\": \"Palomo\",\n" +
                        "  \"password\": \"prova1234\"\n" +
                        "}"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"pedro@mail.es\",\n" +
                                "  \"userName\": \"Palomo\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isConflict())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof AlreadyRegistreredUserName));
    }

    @Test
    @Transactional
    public void loginUserEmailNotConfirmedTest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"sinconfirmar@mail.es\",\n" +
                                "  \"userName\": \"Sinconfirmar\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"sinconfirmar@mail.es\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof EmailNotConfirmed));
    }

    @Test
    @Transactional
    public void loginUserNotFoundTest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"noexiste@mail.es\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof UserNotFound));
    }

    @Test
    @Transactional
    public void loginUserBadCredentialsTest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"juan@mail.es\",\n" +
                                "  \"userName\": \"Juan\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        String body = emailServiceForTests.getLastText();
        String token = body.substring(body.indexOf("token=") + 6);

        mockMvc.perform(post("/api/auth/confirmemail")
                        .param("token", token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"juan@mail.es\",\n" +
                                "  \"password\": \"passwordmalo123\"\n" +
                                "}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void confirmEmailInvalidTokenTest() throws Exception {
        mockMvc.perform(post("/api/auth/confirmemail")
                        .param("token", "token-invalido"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void registerNewUserInvalidBodyTest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"\",\n" +
                                "  \"userName\": \"\",\n" +
                                "  \"password\": \"\"\n" +
                                "}"))
                .andExpect(status().isBadRequest());
    }
}