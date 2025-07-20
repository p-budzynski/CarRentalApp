package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.dto.EmployeeDto;
import pl.kurs.entity.Employee;
import pl.kurs.entity.Position;
import pl.kurs.repository.EmployeeRepository;
import pl.kurs.repository.PositionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class EmployeeControllerTest {
    private static final String FIRST_NAME = "Anna";
    private static final String LAST_NAME = "Wielka";
    private static final String PHONE_NUMBER = "500600700";
    private static final String E_MAIL = "a.wielka@mail.com";
    private static final String POSITION_NAME = "Receptionist";
    private static final long NON_EXISTENT_EMPLOYEE_ID = 999L;
    private final Position receptionist = new Position("Receptionist");
    private final Position mechanic = new Position("Mechanic");
    private final Position accountant = new Position("Accountant");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @BeforeTestMethod
    void setUp() {
        saveTestPositions();
    }

    @Test
    void shouldReturnEmployeeAsXmlForGetById() throws Exception {
        //given
        Employee testEmployee = employeeRepository.save(createTestEmployee());

        //when
        MvcResult mvcResult = mockMvc.perform(get("/employees/" + testEmployee.getId())
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        EmployeeDto employeeDto = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDto.class);
        assertEmployeeDto(employeeDto, testEmployee);
    }

    @Test
    void shouldReturnEmployeeAsJsonForGetById() throws Exception {
        //given
        Employee testEmployee = employeeRepository.save(createTestEmployee());

        //when
        MvcResult mvcResult = mockMvc.perform(get("/employees/" + testEmployee.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //then
        EmployeeDto employeeDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDto.class);
        assertEmployeeDto(employeeDto, testEmployee);
    }

    @Test
    void shouldReturn404WhenEmployeeNotFound() throws Exception {
        //when then
        mockMvc.perform(get("/employees/" + NON_EXISTENT_EMPLOYEE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidEmployeeId() throws Exception {
        //when then
        mockMvc.perform(get("/employees/invalid-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForNegativeEmployeeId() throws Exception {
        //when then
        mockMvc.perform(get("/employees/-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForZeroEmployeeId() throws Exception {
        //when then
        mockMvc.perform(get("/employees/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmployeePageAsJson() throws Exception {
        //given
        employeeRepository.save(createTestEmployee());
        employeeRepository.save(new Employee("Katarzyna", "Mała", mechanic, "505606707", "k.mala@mail.com"));

        //when then
        mockMvc.perform(get("/employees")
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
    void shouldReturnEmployeePageAsXml() throws Exception {
        //given
        employeeRepository.save(createTestEmployee());
        employeeRepository.save(new Employee("Katarzyna", "Mała", receptionist, "505606707", "k.mala@mail.com"));

        //when then
        mockMvc.perform(get("/employees")
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
    void shouldCreateEmployeeSuccessfully() throws Exception {
        //given
        Position testPosition = positionRepository.save(new Position("position1"));
        EmployeeDto testEmployeeDto = createTestEmployeeDto();
        testEmployeeDto.setPositionId(testPosition.getId());

        //when then
        MvcResult result = mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        //then
        EmployeeDto createdEmployee = objectMapper.readValue(result.getResponse().getContentAsString(), EmployeeDto.class);
        assertThat(createdEmployee.getId()).isNotNull();
        assertThat(employeeRepository.findById(createdEmployee.getId())).isPresent();
    }

    @Test
    void shouldReturn400ForInvalidEmployeeData() throws Exception {
        //given
        EmployeeDto invalidEmployeeDto = new EmployeeDto("", LAST_NAME, null, PHONE_NUMBER, E_MAIL);

        //when then
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmployeeDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMissingRequestBody() throws Exception {
        //when then
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        //given
        Employee testEmployee = employeeRepository.save(createTestEmployee());
        EmployeeDto testEmployeeDto = new EmployeeDto(testEmployee.getId(), "Ewa", "Konewa", testEmployee.getPosition().getId(), "070088800", "e.konewa@mail.com");

        //when then
        mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.firstName").value(testEmployeeDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testEmployeeDto.getLastName()))
                .andExpect(jsonPath("$.id").value(testEmployee.getId()))
                .andReturn();
    }

    @Test
    void shouldReturn400ForUpdateWithoutId() throws Exception {
        //given
        EmployeeDto employeeDto = new EmployeeDto("Ewa", "Konewa", 1L, "070088800", "e.konewa@mail.com");

        //when then
        mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUpdateNonExistentEmployee() throws Exception {
        //given
        EmployeeDto testEmployeeDto = createTestEmployeeDto();
        testEmployeeDto.setId(NON_EXISTENT_EMPLOYEE_ID);

        //when then
        mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEmployeeDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCarSuccessfully() throws Exception {
        //given
        Employee testEmployee = createTestEmployee();
        Employee savedEmployee = employeeRepository.save(testEmployee);


        //when then
        mockMvc.perform(delete("/employees/" + savedEmployee.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertThat(employeeRepository.findById(savedEmployee.getId())).isEmpty();
    }

    @Test
    void shouldSearchEmployeeByFirstNameAndLastName() throws Exception {
        //given
        Employee testEmployee = createTestEmployee();
        employeeRepository.save(testEmployee);
        employeeRepository.save(new Employee("Johny", "Rambo", accountant, "600500400", "j.rambo@mail.com"));
        employeeRepository.save(new Employee("Bruce", "Lee", mechanic, "800500400", "b.lee@mail.com"));

        //when then
        mockMvc.perform(get("/employees/search")
                        .param("firstName", FIRST_NAME)
                        .param("lastName", LAST_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.content[0].lastName").value(LAST_NAME));
    }

    @Test
    void shouldSearchEmployeeByPosition() throws Exception {
        //given
        Employee testEmployee = createTestEmployee();
        employeeRepository.save(testEmployee);
        employeeRepository.save(new Employee("Johny", "Rambo", accountant, "600500400", "j.rambo@mail.com"));
        employeeRepository.save(new Employee("Bruce", "Lee", mechanic, "800500400", "b.lee@mail.com"));

        //when then
        mockMvc.perform(get("/employees/search")
                        .param("position", POSITION_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.content[0].lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.content[0].positionId").value(testEmployee.getPosition().getId()));
    }

    private Employee createTestEmployee() {
        return new Employee(FIRST_NAME, LAST_NAME, receptionist, PHONE_NUMBER, E_MAIL);
    }

    private EmployeeDto createTestEmployeeDto() {
        return new EmployeeDto(FIRST_NAME, LAST_NAME, 1L, PHONE_NUMBER, E_MAIL);
    }

    private void saveTestPositions() {
        positionRepository.save(receptionist);
        positionRepository.save(mechanic);
        positionRepository.save(accountant);
    }

    private void assertEmployeeDto(EmployeeDto employeeDto, Employee expectedEmployee) {
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(expectedEmployee.getId());
        assertThat(employeeDto.getFirstName()).isEqualTo(expectedEmployee.getFirstName());
        assertThat(employeeDto.getLastName()).isEqualTo(expectedEmployee.getLastName());
        assertThat(employeeDto.getPositionId()).isEqualTo(expectedEmployee.getPosition().getId());
        assertThat(employeeDto.getPhoneNumber()).isEqualTo(expectedEmployee.getPhoneNumber());
        assertThat(employeeDto.getEmail()).isEqualTo(expectedEmployee.getEmail());
    }
}
