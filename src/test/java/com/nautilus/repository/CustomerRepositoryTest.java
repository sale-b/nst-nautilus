package com.nautilus.repository;

import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;
import config.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@Sql(statements = "delete from customer", executionPhase = AFTER_TEST_METHOD)
public class CustomerRepositoryTest extends BaseTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void insertTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customer);
        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertNotNull(customer.getCreatedOn());
        assertNotNull(customer.getModifiedOn());
        assertEquals("Petar Peric", customer.getName());
        assertEquals("Beograd", customer.getCity());
        assertEquals("Adresa", customer.getAddress());
        assertEquals("123123", customer.getPhone());
        assertEquals(LocalDate.of(2021, 2, 6), customer.getDate());
        assertEquals(Customer.LegalForm.LEGAL_ENTITY, customer.getLegalForm());
        assertEquals(Integer.valueOf(3), customer.getRequiredSanitisePeriodInMonths());
        assertEquals(Double.valueOf(0.0), customer.getDebt());
        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
        assertEquals(1, customerRepository.getAll().size());
    }

    @Test
    void updateTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customer = customerRepository.insert(customer);
        LocalDateTime modifiedOld = customer.getModifiedOn();
        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertNotNull(customer.getCreatedOn());
        assertNotNull(customer.getModifiedOn());

        customer.setName("Djordje Djokic");
        customer.setCity("Nis");
        customer.setAddress("Adresa 2");
        customer.setPhone("321321");
        customer.setDate(LocalDate.of(2021, 6, 2));
        customer.setLegalForm(Customer.LegalForm.INDIVIDUAL);
        customer.setRequiredSanitisePeriodInMonths(4);
        customer.setDebt(1.0);
        customer.setPackagingSmall(1);
        customer.setPackagingLarge(1);

        customerRepository.update(customer);

        assertEquals(1, customerRepository.getAll().size());
        assertEquals("Djordje Djokic", customer.getName());
        assertEquals("Nis", customer.getCity());
        assertEquals("Adresa 2", customer.getAddress());
        assertEquals("321321", customer.getPhone());
        assertEquals(LocalDate.of(2021, 6, 2), customer.getDate());
        assertEquals(Customer.LegalForm.INDIVIDUAL, customer.getLegalForm());
        assertEquals(Integer.valueOf(4), customer.getRequiredSanitisePeriodInMonths());
        assertEquals(Double.valueOf(1.0), customer.getDebt());
        assertEquals(Integer.valueOf(1), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(1), customer.getPackagingLarge());
        assertEquals(1, customerRepository.getAll().size());
        assertEquals(customer.getId(), customer.getId());
        assertEquals(customer.getCreatedOn(), customer.getCreatedOn());
        assertNotEquals(modifiedOld, customer.getModifiedOn());

        customer.setModifiedOn(LocalDateTime.now());
        assertFalse(customerRepository.update(customer).isPresent());

    }

    @Test
    void deleteByIdTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();
        customer = customerRepository.insert(customer);
        assertEquals(1, customerRepository.getAll().size());
        customerRepository.deleteById(customer);
        assertEquals(0, customerRepository.getAll().size());
    }

    @Test
    void findByIdTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customer);
        customer = customerRepository.findById(customer.getId());
        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertNotNull(customer.getCreatedOn());
        assertNotNull(customer.getModifiedOn());
        assertEquals("Petar Peric", customer.getName());
        assertEquals("Beograd", customer.getCity());
        assertEquals("Adresa", customer.getAddress());
        assertEquals("123123", customer.getPhone());
        assertEquals(LocalDate.of(2021, 2, 6), customer.getDate());
        assertEquals(Customer.LegalForm.LEGAL_ENTITY, customer.getLegalForm());
        assertEquals(Integer.valueOf(3), customer.getRequiredSanitisePeriodInMonths());
        assertEquals(Double.valueOf(0.0), customer.getDebt());
        assertEquals(Integer.valueOf(0), customer.getPackagingSmall());
        assertEquals(Integer.valueOf(0), customer.getPackagingLarge());
        assertEquals(1, customerRepository.getAll().size());
    }

    @Test
    void getAllTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        assertEquals(2, customerRepository.getAll().size());
    }

    @Test
    void deleteAllTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        assertEquals(2, customerRepository.getAll().size());
        customerRepository.deleteAll(customerRepository.getAll());
        assertEquals(0, customerRepository.getAll().size());
    }

    @Test
    void findDtoByTextFieldsTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now().minusMonths(5).minusDays(10))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();
        customerRepository.insert(customer);
        assertEquals(customer.getId(), customerRepository.findDtoByTextFields("tar").get(0).getId());
        assertEquals(customer.getId(), customerRepository.findDtoByTextFields("beo").get(0).getId());
        assertEquals(customer.getId(), customerRepository.findDtoByTextFields("312").get(0).getId());
        CustomerDto customerDto = customerRepository.findDtoByTextFields("dres").get(0);
        assertEquals(Integer.valueOf(-3), customerDto.getMonthsUntilSanitize());
        assertEquals(Integer.valueOf(4), customerDto.getMonthsWithoutFulfilledMonthlyObligation());
        assertEquals(Integer.valueOf(-3), customerDto.getMonthsUntilSanitize());
        assertEquals(LocalDate.now().minusMonths(5).minusDays(10), customerDto.getLastSanitiseDate());

    }

    @Test
    void findByTextFieldsTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now().minusMonths(5).minusDays(10))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();
        customerRepository.insert(customer);
        assertEquals(customer.getId(), customerRepository.findByTextFields("Pet".toLowerCase().trim()).get(0).getId());
        assertEquals(customer.getId(), customerRepository.findByTextFields("tar").get(0).getId());
        assertEquals(customer.getId(), customerRepository.findByTextFields("beo").get(0).getId());
        assertEquals(customer.getId(), customerRepository.findByTextFields("312").get(0).getId());
        assertEquals(customer.getId(), customerRepository.findByTextFields("dres").get(0).getId());
    }

    @Test
    void findDistinctCitiesTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.of(2021, 2, 6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        List<String> cities = customerRepository.findDistinctCities();

        assertEquals(2, cities.size());
        assertTrue(cities.contains("Beograd"));
        assertTrue(cities.contains("Smederevo"));
        assertEquals("Smederevo", customerRepository.findDistinctCities("eder").get(0));

    }


    @Test
    void getAllDtoTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customer = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now().minusMonths(5).minusDays(10))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();
        customerRepository.insert(customer);
        List<CustomerDto> customerDtoList = customerRepository.getAllDto();
        assertEquals(1, customerDtoList.size());
        assertEquals(customer.getId(), customerDtoList.get(0).getId());
        assertEquals(customer.getId(), customerDtoList.get(0).getId());
        assertEquals(customer.getId(), customerDtoList.get(0).getId());
        CustomerDto customerDto = customerDtoList.get(0);
        assertEquals(Integer.valueOf(-3), customerDto.getMonthsUntilSanitize());
        assertEquals(Integer.valueOf(4), customerDto.getMonthsWithoutFulfilledMonthlyObligation());
        assertEquals(Integer.valueOf(-3), customerDto.getMonthsUntilSanitize());
        assertEquals(LocalDate.now().minusMonths(5).minusDays(10), customerDto.getLastSanitiseDate());

    }

    @Test
    void getDtoWithUnfulfilledObligationTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now())
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.now().minusMonths(6))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        List<CustomerDto> customerDtoList = customerRepository.getDtoWithUnfulfilledObligation();
        assertEquals(1, customerDtoList.size());
        assertEquals(customerTwo.getId(), customerDtoList.get(0).getId());
        assertEquals(Integer.valueOf(1), customerRepository.countDtoWithUnfulfilledObligation());
    }

    @Test
    void getDtoWithSanitizeNeededTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now())
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.now().minusMonths(2).minusDays(5))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        List<CustomerDto> customerDtoList = customerRepository.getDtoWithSanitizeNeeded();
        assertEquals(1, customerDtoList.size());
        assertEquals(customerTwo.getId(), customerDtoList.get(0).getId());
        assertEquals(Integer.valueOf(1), customerRepository.countDtoWithSanitizeNeeded());
    }

    @Test
    void getDtoWithSanitizeLateTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now())
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.now().minusMonths(3).minusDays(5))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .debt(0.0)
                .packagingSmall(0)
                .packagingLarge(0)
                .build();

        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        List<CustomerDto> customerDtoList = customerRepository.getDtoWithSanitizeLate();
        assertEquals(1, customerDtoList.size());
        assertEquals(customerTwo.getId(), customerDtoList.get(0).getId());
        assertEquals(Integer.valueOf(1), customerRepository.countDtoWithSanitizeLate());
    }

    @Test
    void getDtoWithPackagingDebtTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now())
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.now().minusMonths(3).minusDays(5))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();


        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        customerTwo.setPackagingSmall(2);
        customerTwo.setPackagingLarge(0);
        customerTwo.setDebt(0.0);
        customerRepository.update(customerTwo);

        List<CustomerDto> customerDtoList = customerRepository.getDtoWithPackagingDebt();
        assertEquals(1, customerDtoList.size());
        assertEquals(customerTwo.getId(), customerDtoList.get(0).getId());
        assertEquals(Integer.valueOf(1), customerRepository.countDtoWithPackagingDebt());
    }

    @Test
    void getDtoWithDebtTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now())
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(LocalDate.now().minusMonths(3).minusDays(5))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();


        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        customerTwo.setPackagingSmall(0);
        customerTwo.setPackagingLarge(0);
        customerTwo.setDebt(500.0);
        customerRepository.update(customerTwo);

        List<CustomerDto> customerDtoList = customerRepository.getDtoWithDebt();
        assertEquals(1, customerDtoList.size());
        assertEquals(customerTwo.getId(), customerDtoList.get(0).getId());
        assertEquals(Integer.valueOf(1), customerRepository.countDtoWithDebt());
    }

    @Test
    void selectDatesWithUnfulfilledObligationForCustomerTest() {
        assertEquals(0, customerRepository.getAll().size());
        Customer customerOne = Customer.builder()
                .name("Petar Peric")
                .address("Adresa")
                .city("Beograd")
                .phone("123123")
                .date(LocalDate.now().minusMonths(3))
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();

        Customer customerTwo = Customer.builder()
                .name("Misa Misic")
                .address("Adresa")
                .city("Smederevo")
                .phone("123123")
                .date(null)
                .legalForm(Customer.LegalForm.LEGAL_ENTITY)
                .requiredSanitisePeriodInMonths(3)
                .build();


        customerRepository.insert(customerOne);
        customerRepository.insert(customerTwo);

        customerTwo.setPackagingSmall(0);
        customerTwo.setPackagingLarge(0);
        customerTwo.setDebt(500.0);
        customerRepository.update(customerTwo);

        List<LocalDate> datesList = customerRepository.selectDatesWithUnfulfilledObligationForCustomer(CustomerDto.builder().id(customerOne.getId()).build());
        assertEquals(2, datesList.size());
        assertTrue(datesList.contains(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
        assertTrue(datesList.contains(LocalDate.now().minusMonths(2).withDayOfMonth(1)));
    }

}
