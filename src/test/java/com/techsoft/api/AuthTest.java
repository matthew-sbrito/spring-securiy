package com.techsoft.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest {
    private final MockMvc mockMvc;

    @Autowired
    public AuthTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void toBeReturnSuccessWithValidCredentials() throws Exception {
        URI uri = new URI("/auth/signIn");

        String content = "{ \"username\" : \"matheus\", \"password\": \"123456\" }";

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders
                .post(uri)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultMatcher response = MockMvcResultMatchers
                .status().is(200);

        mockMvc.perform(request).andExpect(response);
    }

    @Test
    void toBeReturnErrorWithInvalidCredentials() throws Exception {
        URI uri = new URI("/auth/signIn");

        String content = "{ \"username\" : \"matheus\", \"password\": \"123456\" }";

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders
                .post(uri)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultMatcher response = MockMvcResultMatchers
                .status().is(401);

        mockMvc.perform(request).andExpect(response);
    }
}
