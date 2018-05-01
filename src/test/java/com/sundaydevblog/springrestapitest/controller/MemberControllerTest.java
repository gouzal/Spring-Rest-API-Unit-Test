package com.sundaydevblog.springrestapitest.controller;

import com.sundaydevblog.springrestapitest.SpringRestApiTestApplication;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringRestApiTestApplication.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MemberControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldFetchAllAuthors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(MemberController.URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }

    @Test
    public void shouldFindAuthorById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(MemberController.URI + "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("Mary Brown"))
                .andExpect(jsonPath("$.email").value("mary.b@gmail.com"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andDo(print());
    }

    @Test
    public void shouldVerifyInvalidAuthorId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(MemberController.URI + "0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Member with ID: '0' not found."))
                .andDo(print());
    }

    @Test
    public void shouldVerifyInvalidAuthorArgument() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(MemberController.URI + "abc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Your request has issued a malformed or illegal request."))
                .andDo(print());
    }

    @Test
    public void shouldSaveAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(MemberController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Marilyn Monroe\", \"email\": \"mm@music.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("Marilyn Monroe"))
                .andExpect(jsonPath("$.email").value("mm@music.com"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andDo(print());

    }

    @Test
    public void shouldVerifyInvalidSaveAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(MemberController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\", \"email\": \"mm@music.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Your request has issued a malformed or illegal request."))
                .andDo(print());

    }

    @Test
    public void shouldUpdateAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(MemberController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 2, \"name\": \"C. S. Lewis\", \"email\": \"cslewis@books.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("C. S. Lewis"))
                .andExpect(jsonPath("$.email").value("cslewis@books.com"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andDo(print());
    }

    @Test
    public void shouldVerifyInvalidUpdateAuthorId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(MemberController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 999, \"name\": \"C. S. Lewis\", \"email\": \"cslewis@books.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Member with ID: '999' not found."))
                .andDo(print());
    }

    @Test
    public void shouldVerifyInvalidPropertyNameWhenUpdateAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(MemberController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 2, \"nnaammee\": \"C. S. Lewis\", \"email\": \"cslewis@books.com\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Your request has issued a malformed or illegal request."))
                .andDo(print());
    }

    @Test
    public void shouldRemoveAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(MemberController.URI + "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Member with ID: '1' deleted."))
                .andDo(print());
    }

    @Test
    public void shouldVerifyInvalidAuthorRemove() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(MemberController.URI + "999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Member with ID: '999' not found."))
                .andDo(print());
    }
}