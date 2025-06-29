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
import pl.kurs.dto.CarDto;
import pl.kurs.dto.CarDtoList;
import pl.kurs.entity.Car;
import pl.kurs.repository.CarRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CarControllerTest {
    private static final String PRODUCER = "BMW";
    private static final String MODEL = "M5";
    private static final int YEAR_OF_PRODUCTION = 2025;
    private static final String REGISTRATION_NUMBER = "WA 12345";
    private static final BigDecimal PRICE_PER_DAY = new BigDecimal("1000");
    private static final long NON_EXISTENT_CAR_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    private Car testCar;

    @BeforeEach
    void setup() {
        testCar = createTestCar();
    }

    @Test
    void shouldReturnCarAsXmlForGetById() throws Exception {
        //given
        Car savedCar = carRepository.save(testCar);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/cars/" + savedCar.getId())
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        CarDto carDto = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), CarDto.class);
        assertCarDto(carDto, savedCar);
    }

    @Test
    void shouldReturnCarAsJsonForGetById() throws Exception {
        //given
        Car savedCar = carRepository.save(testCar);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/cars/" + savedCar.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //then
        CarDto carDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CarDto.class);
        assertCarDto(carDto, savedCar);
    }

    @Test
    void shouldReturn404WhenCarNotFound() throws Exception {
        //when then
        mockMvc.perform(get("/cars/" + NON_EXISTENT_CAR_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidCarId() throws Exception {
        //when then
        mockMvc.perform(get("/cars/invalid-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForNegativeCarId() throws Exception {
        //when then
        mockMvc.perform(get("/cars/-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForZeroCarId() throws Exception {
        //when then
        mockMvc.perform(get("/cars/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCarsPageAsJson() throws Exception {
        //given
        carRepository.save(testCar);
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal(600)));

        //when then
        mockMvc.perform(get("/cars")
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
    void shouldReturnCarsListAsXml() throws Exception {
        //given
        carRepository.save(testCar);
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal(600)));

        //when then
        MvcResult result = mockMvc.perform(get("/cars")
                        .accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/xml"))
                .andReturn();

        //then
        XmlMapper xmlMapper = new XmlMapper();
        CarDtoList carDtoList = xmlMapper.readValue(result.getResponse().getContentAsString(), CarDtoList.class);
        assertThat(carDtoList.getEntities()).hasSize(2);
    }

    @Test
    void shouldCreateCarSuccessfully() throws Exception {
        //given
        CarDto carDto = new CarDto("Mercedes", "C-Class", 2024, "WB 11111", new BigDecimal("800"));

        //when then
        MvcResult result = mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.producer").value("Mercedes"))
                .andExpect(jsonPath("$.model").value("C-Class"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        //then
        CarDto createdCar = objectMapper.readValue(result.getResponse().getContentAsString(), CarDto.class);
        assertThat(createdCar.getId()).isNotNull();
        assertThat(carRepository.findById(createdCar.getId())).isPresent();
    }

    @Test
    void shouldReturn400ForInvalidCarData() throws Exception {
        //given
        CarDto invalidCarDto = new CarDto("", null, 2024, "GA 45678", null);

        //when then
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMissingRequestBody() throws Exception {
        //when then
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCarSuccessfully() throws Exception {
        //given
        Car savedCar = carRepository.save(testCar);
        CarDto updateDto = new CarDto(savedCar.getId(), "Mercedes", "S-Class", 2024, "WC 22222", new BigDecimal("1200"));

        //when then
        mockMvc.perform(put("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.producer").value("Mercedes"))
                .andExpect(jsonPath("$.model").value("S-Class"))
                .andExpect(jsonPath("$.id").value(savedCar.getId()))
                .andReturn();
    }

    @Test
    void shouldReturn400ForUpdateWithoutId() throws Exception {
        //given
        CarDto carDto = new CarDto("Mercedes", "S-Class", 2024, "WC 22222", new BigDecimal("1200"));

        //when then
        mockMvc.perform(put("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUpdateNonExistentCar() throws Exception {
        //given
        CarDto carDto = new CarDto("Mercedes", "S-Class", 2024, "WC 22222", new BigDecimal("1200"));
        carDto.setId(NON_EXISTENT_CAR_ID);

        //when then
        mockMvc.perform(put("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCarSuccessfully() throws Exception {
        //given
        Car savedCar = carRepository.save(testCar);

        //when then
        mockMvc.perform(delete("/cars/" + savedCar.getId()))
                .andExpect(status().isOk());

        //then
        assertThat(carRepository.findById(savedCar.getId())).isEmpty();
    }

    @Test
    void shouldSearchCarsByProducer() throws Exception {
        //given
        carRepository.save(testCar);
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal("600")));

        //when then
        mockMvc.perform(get("/cars/search")
                        .param("producer", PRODUCER)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].producer").value(PRODUCER));
    }

    @Test
    void shouldSearchCarsByProducerAndModel() throws Exception {
        //given
        carRepository.save(testCar);
        carRepository.save(new Car("BMW", "X5", 2024, "WX 55555", new BigDecimal("1100")));
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal("600")));

        //when then
        mockMvc.perform(get("/cars/search")
                        .param("producer", PRODUCER)
                        .param("model", MODEL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].producer").value(PRODUCER))
                .andExpect(jsonPath("$.content[0].model").value(MODEL));
    }

    @Test
    void shouldSortCarsAscending() throws Exception {
        //given
        carRepository.save(createTestCar());
        carRepository.save(new Car("Volvo", "XC90", 2024, "WX 55555", new BigDecimal("1100")));
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal("600")));

        //when then
        mockMvc.perform(get("/cars/sort")
                        .param("property", "producer")
                        .param("direction", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].producer").value("Audi"))
                .andExpect(jsonPath("$.content[1].producer").value("BMW"))
                .andExpect(jsonPath("$.content[2].producer").value("Volvo"));
    }

    @Test
    void shouldSortCarsDescending() throws Exception {
        //given
        carRepository.save(createTestCar());
        carRepository.save(new Car("Volvo", "XC90", 2024, "WX 55555", new BigDecimal("1100")));
        carRepository.save(new Car("Audi", "A4", 2023, "WW 23456", new BigDecimal("600")));

        //when then
        mockMvc.perform(get("/cars/sort")
                        .param("property", "producer")
                        .param("direction", "desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].producer").value("Volvo"))
                .andExpect(jsonPath("$.content[1].producer").value("BMW"))
                .andExpect(jsonPath("$.content[2].producer").value("Audi"));
    }

    private Car createTestCar() {
        return new Car(PRODUCER, MODEL, YEAR_OF_PRODUCTION, REGISTRATION_NUMBER, PRICE_PER_DAY);
    }

    private void assertCarDto(CarDto carDto, Car expectedCar) {
        assertThat(carDto).isNotNull();
        assertThat(carDto.getId()).isEqualTo(expectedCar.getId());
        assertThat(carDto.getProducer()).isEqualTo(expectedCar.getProducer());
        assertThat(carDto.getModel()).isEqualTo(expectedCar.getModel());
        assertThat(carDto.getYearOfProduction()).isEqualTo(expectedCar.getYearOfProduction());
        assertThat(carDto.getRegistrationNumber()).isEqualTo(expectedCar.getRegistrationNumber());
        assertThat(carDto.getPricePerDay()).isEqualByComparingTo(expectedCar.getPricePerDay());
    }

}
