package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import pl.kurs.dto.ReservationDto;
import pl.kurs.dto.ReservationDtoList;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.repository.CarRepository;
import pl.kurs.repository.CustomerRepository;
import pl.kurs.repository.ReservationRepository;
import pl.kurs.repository.StatusRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ReservationControllerTest {
    private static final LocalDate START_DATE = LocalDate.of(2025, 4, 20);
    private static final LocalDate END_DATE = LocalDate.of(2025, 4, 25);
    private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(1000);
    private static final long NON_EXISTENT_RESERVATION_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StatusRepository statusRepository;

    private Reservation testReservation;
    private Car testCar;
    private Customer testCustomer;
    private Status testStatus;

    @BeforeEach
    void setUp() {
        testReservation = createTestReservation();
    }

    @Test
    void shouldReturnReservationAsXmlForGetById() throws Exception {
        //given
        Reservation savedReservation = reservationRepository.save(testReservation);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/reservations/" + savedReservation.getId())
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReservationDto reservationDto = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), ReservationDto.class);
        assertReservationDto(reservationDto, savedReservation);
    }

    @Test
    void shouldReturnReservationAsJsonForGetById() throws Exception {
        //given
        Reservation savedReservation = reservationRepository.save(testReservation);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/reservations/" + savedReservation.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //then
        ReservationDto reservationDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReservationDto.class);
        assertReservationDto(reservationDto, savedReservation);
    }

    @Test
    void shouldReturn404WhenReservationNotFound() throws Exception {
        //when then
        mockMvc.perform(get("/reservations/" + NON_EXISTENT_RESERVATION_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidReservationId() throws Exception {
        //when then
        mockMvc.perform(get("/reservations/invalid-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForNegativeReservationId() throws Exception {
        //when then
        mockMvc.perform(get("/reservations/-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForZeroReservationId() throws Exception {
        //when then
        mockMvc.perform(get("/reservations/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnReservationsPageAsJson() throws Exception {
        //given
        reservationRepository.save(testReservation);
        reservationRepository.save(new Reservation(testCar, testCustomer, LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 4), new BigDecimal(750), testStatus));

        //when then
        mockMvc.perform(get("/reservations")
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
    void shouldReturnReservationsListAsXml() throws Exception {
        //given
        reservationRepository.save(testReservation);
        reservationRepository.save(new Reservation(testCar, testCustomer, LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 4), new BigDecimal(750), testStatus));

        //when
        MvcResult result = mockMvc.perform(get("/reservations")
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReservationDtoList reservationDtoList = xmlMapper.readValue(result.getResponse().getContentAsString(), ReservationDtoList.class);
        assertThat(reservationDtoList.getEntities()).hasSize(2);
    }

    @Test
    void shouldCreateReservationSuccessfully() throws Exception {
        //given
        testCar = carRepository.save(testCar);
        testCustomer = customerRepository.save(testCustomer);
        testStatus = statusRepository.save(testStatus);
        ReservationDto reservationDto = new ReservationDto(testCar.getId(), testCustomer.getId(), START_DATE, END_DATE, TOTAL_AMOUNT, testStatus.getId());

        //when then
        MvcResult result = mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.carId").value(testCar.getId()))
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        //then
        ReservationDto createdReservations = objectMapper.readValue(result.getResponse().getContentAsString(), ReservationDto.class);
        assertThat(createdReservations.getId()).isNotNull();
        assertThat(reservationRepository.findById(createdReservations.getId())).isPresent();
    }

    @Test
    void shouldReturn400ForInvalidReservationData() throws Exception {
        //given
        ReservationDto invalidReservationDto = new ReservationDto(null, null, START_DATE, END_DATE, null, null);

        //when then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMissingRequestBody() throws Exception {
        //when then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        //given
        Reservation savedReservation = reservationRepository.save(testReservation);
        ReservationDto updateDto = new ReservationDto(savedReservation.getId(), savedReservation.getCar().getId(), savedReservation.getCustomer().getId(),
                LocalDate.of(2025, 6, 22), LocalDate.of(2025, 6, 24), new BigDecimal(500), savedReservation.getStatus().getId());

        //when then
        mockMvc.perform(put("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.startDate").value("22-06-2025"))
                .andExpect(jsonPath("$.endDate").value("24-06-2025"))
                .andExpect(jsonPath("$.id").value(savedReservation.getId()))
                .andReturn();
    }

    @Test
    void shouldReturn400ForUpdateWithoutId() throws Exception {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L, START_DATE, END_DATE, TOTAL_AMOUNT, 1L);

        //when then
        mockMvc.perform(put("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUpdateNonExistentReservation() throws Exception {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L, START_DATE, END_DATE, TOTAL_AMOUNT, 1L);
        reservationDto.setId(NON_EXISTENT_RESERVATION_ID);

        //when then
        mockMvc.perform(put("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCancelReservationById() throws Exception {
        //given
        statusRepository.save(new Status("CANCELLED"));
        Reservation savedReservation = reservationRepository.save(testReservation);

        //when
        mockMvc.perform(put("/reservations/" + savedReservation.getId() + "/cancel"))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        Optional<Reservation> updatedReservation = reservationRepository.findById(savedReservation.getId());
        assertThat(updatedReservation).isPresent();
        assertThat(updatedReservation.get().getStatus().getName()).isEqualTo("CANCELLED");
    }

    private Reservation createTestReservation() {
        testCar = new Car("BMW", "M135i", 2022, "WX 43210", new BigDecimal(200));
        testCustomer = new Customer("John", "Cena", "j.cena@mail.com", "500600700", "ABC 12345");
        testStatus = new Status("TEST");
        return new Reservation(testCar, testCustomer, START_DATE, END_DATE, TOTAL_AMOUNT, testStatus);
    }

    private void assertReservationDto(ReservationDto reservationDto, Reservation expectedReservation) {
        assertThat(reservationDto).isNotNull();
        assertThat(reservationDto.getId()).isEqualTo(expectedReservation.getId());
        assertThat(reservationDto.getCarId()).isEqualTo(expectedReservation.getCar().getId());
        assertThat(reservationDto.getCustomerId()).isEqualTo(expectedReservation.getCustomer().getId());
        assertThat(reservationDto.getStartDate()).isEqualTo(expectedReservation.getStartDate());
        assertThat(reservationDto.getEndDate()).isEqualTo(expectedReservation.getEndDate());
        assertThat(reservationDto.getTotalAmount()).isEqualTo(expectedReservation.getTotalAmount());
        assertThat(reservationDto.getStatusId()).isEqualTo(expectedReservation.getStatus().getId());
    }
}
