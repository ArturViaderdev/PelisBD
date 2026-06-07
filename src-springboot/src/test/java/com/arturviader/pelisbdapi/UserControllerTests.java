package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.exception.AlreadyRegistreredEmail;
import com.arturviader.pelisbdapi.exception.AlreadyRegistreredUserName;
import com.arturviader.pelisbdapi.service.EmailServiceForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=test")
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailServiceForTests emailServiceForTests;

    @Test
    @Transactional
    public void registerNewUserTestAndLogin() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"juanpalomp@mail.es\",\n" +
                                "  \"userName\": \"Juan\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}")).andExpect(status().isCreated());
        String body = emailServiceForTests.getLastText();
        String token = body.substring(body.indexOf("token=") + 6);
        Assertions.assertNotNull(token);
        mockMvc.perform(get("/api/auth/confirmemail")
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
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"juan@mail.es\",\n" +
                        "  \"userName\": \"Juan\",\n" +
                        "  \"password\": \"prova1234\"\n" +
                        "}"));
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"juan@mail.es\",\n" +
                        "  \"userName\": \"Palomo\",\n" +
                        "  \"password\": \"prova1234\"\n" +
                        "}")).andExpect(status().isConflict())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof AlreadyRegistreredEmail));
    }

    @Test
    @Transactional
    public void registerNewUserUserNameAlreadyExistsTest() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"juan@mail.es\",\n" +
                        "  \"userName\": \"Palomo\",\n" +
                        "  \"password\": \"prova1234\"\n" +
                        "}"));
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"pedro@mail.es\",\n" +
                                "  \"userName\": \"Palomo\",\n" +
                                "  \"password\": \"prova1234\"\n" +
                                "}")).andExpect(status().isConflict())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof AlreadyRegistreredUserName));
    }
}
