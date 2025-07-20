package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.dto.CustomerDto;
import pl.kurs.entity.Customer;
import pl.kurs.repository.CustomerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerTest {
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Cena";
    private static final String E_MAIL = "j.cena@mail.com";
    private static final String PHONE_NUMBER = "500600700";
    private static final String DRIVING_LICENSE_NUMBER = "ABC 12345";
    private static final long NON_EXISTENT_CUSTOMER_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = createTestCustomer();
    }

    @Test
    void shouldReturnCustomerAsXmlForGetById() throws Exception {
        //given
        Customer savedCustomer = customerRepository.save(testCustomer);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/customers/" + savedCustomer.getId())
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        CustomerDto customerDto = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerDto.class);
        assertCustomerDto(customerDto, savedCustomer);
    }

    @Test
    void shouldReturnCustomerAsJsonForGetById() throws Exception {
        //given
        Customer savedCustomer = customerRepository.save(testCustomer);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/customers/" + savedCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //then
        CustomerDto customerDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerDto.class);
        assertCustomerDto(customerDto, savedCustomer);
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        //when then
        mockMvc.perform(get("/customers/" + NON_EXISTENT_CUSTOMER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidCustomerId() throws Exception {
        //when then
        mockMvc.perform(get("/customers/invalid-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForNegativeCustomerId() throws Exception {
        //when then
        mockMvc.perform(get("/customers/-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForZeroCustomerId() throws Exception {
        //when then
        mockMvc.perform(get("/customers/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCustomersPageAsJson() throws Exception {
        //given
        customerRepository.save(testCustomer);
        customerRepository.save(new Customer("Johny", "Rambo", "j.rambo@mail.com", "600500400", "DEF 67890"));

        //when then
        mockMvc.perform(get("/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andReturn();
    }

    @Test
    void shouldReturnCustomersPageAsXml() throws Exception {
        //given
        customerRepository.save(testCustomer);
        customerRepository.save(new Customer("Johny", "Rambo", "j.rambo@mail.com", "600500400", "DEF 67890"));

        //when then
        mockMvc.perform(get("/customers")
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andExpect(xpath("count(/PageImpl/content/content)").number(2.0))
                .andExpect(xpath("/PageImpl/page/totalElements").number(2.0))
                .andExpect(xpath("/PageImpl/page/number").number(0.0))
                .andExpect(xpath("/PageImpl/page/size").number(10.0))
                .andExpect(xpath("/PageImpl/page/totalPages").number(1.0))
                .andReturn();
    }

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        //given
        CustomerDto customerDto = new CustomerDto(FIRST_NAME, LAST_NAME, E_MAIL, PHONE_NUMBER, DRIVING_LICENSE_NUMBER);

        //when then
        MvcResult result = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Cena"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        //then
        CustomerDto createdCustomer = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerDto.class);
        assertThat(createdCustomer.getId()).isNotNull();
        assertThat(customerRepository.findById(createdCustomer.getId())).isPresent();
    }

    @Test
    void shouldReturn400ForInvalidCustomerData() throws Exception {
        //given
        CustomerDto invalidCustomerDto = new CustomerDto("", null, E_MAIL, PHONE_NUMBER, DRIVING_LICENSE_NUMBER);

        //when then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomerDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMissingRequestBody() throws Exception {
        //when then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() throws Exception {
        //given
        Customer savedCustomer = customerRepository.save(testCustomer);
        CustomerDto updateCustomer = new CustomerDto(savedCustomer.getId(),"Johny", "Rambo", "j.rambo@mail.com", "600500400", "DEF 67890");

        //when then
        mockMvc.perform(put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCustomer)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.firstName").value("Johny"))
                .andExpect(jsonPath("$.lastName").value("Rambo"))
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andReturn();
    }

    @Test
    void shouldReturn400ForUpdateWithoutId() throws Exception {
        //given
        CustomerDto customerDto = new CustomerDto(FIRST_NAME, LAST_NAME, E_MAIL, PHONE_NUMBER, DRIVING_LICENSE_NUMBER);

        //when then
        mockMvc.perform(put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUpdateNonExistentCustomer() throws Exception {
        //given
        CustomerDto customerDto = new CustomerDto(FIRST_NAME, LAST_NAME, E_MAIL, PHONE_NUMBER, DRIVING_LICENSE_NUMBER);
        customerDto.setId(NON_EXISTENT_CUSTOMER_ID);

        //when then
        mockMvc.perform(put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCustomerSuccessfully() throws Exception {
        //given
        Customer savedCustomer = customerRepository.save(testCustomer);

        //when then
        mockMvc.perform(delete("/customers/" + savedCustomer.getId()))
                .andExpect(status().isOk());

        //then
        assertThat(customerRepository.findById(savedCustomer.getId())).isEmpty();
    }

    @Test
    void shouldSearchCustomerByFirstName() throws Exception {
        //given
        customerRepository.save(testCustomer);
        customerRepository.save(new Customer("Johny", "Rambo", "j.rambo@mail.com", "600500400", "DEF 67890"));

        //when then
        mockMvc.perform(get("/customers/search")
                        .param("firstName", FIRST_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value(FIRST_NAME));
    }

    @Test
    void shouldSearchCustomerByFirstNameAndLastName() throws Exception {
        //given
        customerRepository.save(testCustomer);
        customerRepository.save(new Customer("Johny", "Rambo", "j.rambo@mail.com", "600500400", "DEF 67890"));
        customerRepository.save(new Customer("Bruce", "Lee", "b.lee@mail.com", "800500400", "DEF 99890"));

        //when then
        mockMvc.perform(get("/customers/search")
                        .param("firstName", FIRST_NAME)
                        .param("lastName", LAST_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.content[0].lastName").value(LAST_NAME));
    }

    private Customer createTestCustomer() {
        return new Customer(FIRST_NAME, LAST_NAME, E_MAIL, PHONE_NUMBER, DRIVING_LICENSE_NUMBER);
    }

    private void assertCustomerDto(CustomerDto customerDto, Customer expectedCustomer) {
        assertThat(customerDto).isNotNull();
        assertThat(customerDto.getId()).isEqualTo(expectedCustomer.getId());
        assertThat(customerDto.getFirstName()).isEqualTo(expectedCustomer.getFirstName());
        assertThat(customerDto.getLastName()).isEqualTo(expectedCustomer.getLastName());
        assertThat(customerDto.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(customerDto.getPhoneNumber()).isEqualTo(expectedCustomer.getPhoneNumber());
        assertThat(customerDto.getDrivingLicenseNumber()).isEqualTo(expectedCustomer.getDrivingLicenseNumber());
    }

}
