package edu.uoc.pds.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.pds.user.application.rest.AuthenticationRESTController;
import edu.uoc.pds.user.application.rest.UserRESTController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest(classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRESTControllerIntegrationTest {

    private MockMvc mockMvc;
    @Autowired protected WebApplicationContext webApplicationContext;

    private static final String REST_ROLES_PATH = "/roles";
    private static final String REST_COMPANIES_PATH = "/companies";
    private static final String REST_USERS_PATH = "/users";
    private static final String TEST_EMAIL = "isaquinto@uoc.edu";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("verify if there are roles in the system")
    public void findAllRoles() throws Exception {
        mockMvc.perform(get(REST_ROLES_PATH ).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserRESTController.class))
                .andExpect(handler().methodName("findAllRoles"))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("verify if there are companies in the system")
    public void findAllCompanies() throws Exception {
        mockMvc.perform(get(REST_COMPANIES_PATH ).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserRESTController.class))
                .andExpect(handler().methodName("findAllCompanies"))
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    @DisplayName("verify if there are users in the system")
    public void findAllUsers() throws Exception {
        mockMvc.perform(get(REST_USERS_PATH )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserRESTController.class))
                 .andExpect(handler().methodName("findAllUsers"))
                .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    @DisplayName("verify if user can be found by email")
    public void findUserByEmail() throws Exception {
        mockMvc.perform(get(REST_USERS_PATH+ "/" + TEST_EMAIL).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(UserRESTController.class))
                .andExpect(handler().methodName("findUserByEmail"))
                .andExpect(jsonPath("$.email", equalTo(TEST_EMAIL)));
    }


    @Test
    @DisplayName("verify if users can create company")
    public void createCompany() throws Exception {

        String inputString = "{\n" +
                "    \"company\": {\n" +
                "       \"name\": \"Rukawa SL\",\n" +
                "        \"description\": \"Some description\"\n" +
                "    }\n" +
                "}";

        mockMvc.perform(post(REST_COMPANIES_PATH  )
                        .content(CREATE_JSON(inputString))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(UserRESTController.class))
                .andExpect(handler().methodName("createCompany"))
                .andExpect(jsonPath("$", equalTo(3)));
    }


    @Test
    @DisplayName("verify if users can be created")
    public void createUser() throws Exception {

        String inputString = "{\n" +
                "    \"user\": {\n" +
                "        \"fullName\": \"test\",\n" +
                "        \"password\": \"testdsgsdgsdgdsg\",\n" +
                "        \"email\": \"test@test.com\",\n" +
                "        \"mobileNumber\": \"11111111111\",\n" +
                "        \"company\": {\n" +
                "            \"id\": 1\n" +
                "        }, \n" +
                "        \"roles\": [\n" +
                "            {\"name\": \"ROLE_ORGANIZER\"},\n" +
                "            {\"name\": \"ROLE_ADMINISTRATOR\"}\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        mockMvc.perform(post(REST_USERS_PATH )
                        .content(CREATE_JSON(inputString))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(UserRESTController.class))
                .andExpect(handler().methodName("createUser"))
                .andExpect(jsonPath("$.email", equalTo("test@test.com")));
    }



    static String CREATE_JSON(String inputString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Object jsonString = mapper.readValue(inputString, Object.class);
        return mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonString);
    }


}