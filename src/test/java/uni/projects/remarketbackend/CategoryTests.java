package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.remarketbackend.dao.CategoryRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.CategoryDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import  uni.projects.remarketbackend.services.auth.AuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryTests {

    @Autowired private MockMvc mockMvc;

    @Autowired private CategoryService categoryService;
    @Autowired private AuthService authService;
    @Autowired private AccountService accountService;


    @Autowired private CategoryRepository categoryRepository;

    @Autowired private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CategoryDto testCategory;
    private AccountDto accountDto;

    private JwtAuthResponse tokens;


    @BeforeAll
    void setUp() throws Exception {
        accountDto = new AccountDto(
                "user123",
                "StrongPass123!",
                "johntest@example.com",
                Roles.ADMIN.getRole()
        );

        accountService.createUser(accountDto);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        testCategory = new CategoryDto(1L, "Test Category");
        categoryService.createCategory(testCategory);
    }

    @AfterAll
    void tearDown() {
        categoryRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE categories ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Order(1)
    void testCreateCategory() throws Exception {
        CategoryDto newCategory = new CategoryDto(2L, "New Category");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory))
                        .header("Authorization", "Bearer " + tokens.getAccessToken())

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    @Order(2)
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("New Category"));
    }

    @Test
    @Order(3)
    void testGetCategoryById() throws Exception {
        Long id = categoryRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    @Order(4)
    void testUpdateCategory() throws Exception {
        Long id = categoryRepository.findAll().get(0).getId();
        CategoryDto updatedCategory = new CategoryDto(id, "Updated Category");

        mockMvc.perform(put("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory))
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    @Order(5)
    void testDeleteCategory() throws Exception {
        Long id = categoryRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/categories/" + id)
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories/" + id))
                .andExpect(status().isBadRequest());
    }
}
