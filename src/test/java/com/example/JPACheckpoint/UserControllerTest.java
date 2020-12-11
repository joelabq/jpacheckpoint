package com.example.JPACheckpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

public class UserControllerTest {
    ObjectMapper objMap = new ObjectMapper();
    MockHttpServletRequestBuilder req;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    @Transactional
    @Rollback
     void init() throws JsonProcessingException {

        repository.save(new User("john@example.com","something-secret"));
        repository.save(new User("eliza@example.com","something-secret"));
        repository.save(new User("jason@example.com","1something-secret"));

        //Needed this to adjust as the tests go in different orders... Would love a better way
        this.repository.findAll().forEach(x -> System.out.println("Ids used for this test: "+x.getId()));
    }


    @Autowired
     UserRepository repository;

    @Test
    @Transactional
    @Rollback
    public void createUser() throws Exception {

        HashMap<String, Object> data = new HashMap<String,Object>(){
            {
                put("email", "john@example.com");
                put("password", "something-secret");
            }
        };

        String json = objMap.writeValueAsString(data);
        req = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",instanceOf(Number.class)))
                .andExpect(jsonPath("$.email", equalTo("john@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist() );
    }
    @Test
    @Transactional
    @Rollback
    public void getSingleUser() throws Exception {

        req = get("/users/5")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",instanceOf(Number.class)))
                .andExpect(jsonPath("$.email",equalTo("john@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());

    }

    @Test
    @Transactional
    @Rollback
    public void getUsers() throws Exception {


        req = get("/users")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id",instanceOf(Number.class)))
                .andExpect(jsonPath("$.[0]email",equalTo("john@example.com")))
                .andExpect(jsonPath("$.[1]email",equalTo("eliza@example.com")))
                .andExpect(jsonPath("$.[0]password").doesNotExist())
                .andExpect(jsonPath("$.[1]password").doesNotExist());

    }
    @Test
    @Transactional
    @Rollback
    public void updateUsers() throws Exception {

        HashMap<String, Object> data = new HashMap<String,Object>(){
            {
                put("email", "jason@gmail.com");
                put("password", "password");
            }
        };
        String json = objMap.writeValueAsString(data);

        req = patch("/users/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",instanceOf(Number.class)))
                .andExpect(jsonPath("$.email",equalTo("jason@gmail.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @Transactional
    @Rollback
    public void deleteUser() throws Exception {
        req = delete("/users/15");

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count",equalTo(2)));

    }

    @Test
    @Transactional
    @Rollback
    public void authenticateUser() throws Exception {

        HashMap<String, Object> data = new HashMap<String,Object>(){
            {
                put("email", "john@example.com");
                put("password", "something-secret");
            }
        };

        String json = objMap.writeValueAsString(data);

        req = post("/users/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        this.mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", equalTo(true)))
                .andExpect(jsonPath("$.user.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.user.email", equalTo("john@example.com") ))
                .andExpect(jsonPath("$.user.password").doesNotExist());


    }
}

