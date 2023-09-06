package com.paymentchain.customer.repository;

import com.paymentchain.commons.statuses.CustomerStatus;
import com.paymentchain.customer.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("Given empty customer repository When customer added Returns the customer")
    void testCreateCustomer() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setPhone("1234567890");
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer);
        assertNull(savedCustomer.getUpdatedTime());
        assertEquals(1, savedCustomer.getId());
        assertEquals(CustomerStatus.ACTIVE, savedCustomer.getStatus());
        assertEquals(customer.getName(), savedCustomer.getName());
        assertEquals(customer.getPhone(), savedCustomer.getPhone());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then get all active customers Returns the active customers")
    void testGetAllActiveCustomers() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();

        Page<Customer> result = customerRepository.filterCustomers(null, null, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        List<Customer> customers = result.getContent();

        assertNotNull(customers);
        assertEquals(1, customers.size());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then get all inactive customers Returns empty list")
    void testGetAllInactiveCustomers() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.REMOVED.name();

        Page<Customer> result = customerRepository.filterCustomers(null, null, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then filter active customers by name Returns the customer")
    void testFilterActiveCustomersByName() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();
        String searchName = "joh";

        Page<Customer> result = customerRepository.filterCustomers(searchName, null, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        List<Customer> customers = result.getContent();

        assertNotNull(customers);
        assertEquals(1, customers.size());

        Customer customer = customers.get(0);

        assertTrue(customer.getName().toLowerCase().contains(searchName));
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then filter active customers by non-existing name Returns empty list")
    void testFilterActiveCustomersByNonExistingName() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();
        String searchName = "bab";

        Page<Customer> result = customerRepository.filterCustomers(searchName, null, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then filter active customers by phone Returns the customer")
    void testFilterActiveCustomersByPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();
        String searchPhone = "123";

        Page<Customer> result = customerRepository.filterCustomers(null, searchPhone, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        List<Customer> customers = result.getContent();

        assertNotNull(customers);
        assertEquals(1, customers.size());

        Customer customer = customers.get(0);

        assertTrue(customer.getPhone().contains(searchPhone));
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then filter active customers by non-existing phone Returns empty list")
    void testFilterActiveCustomersByNonExistingPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();
        String searchPhone = "555667";

        Page<Customer> result = customerRepository.filterCustomers(null, searchPhone, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then filter active customers by name and phone Returns the customer")
    void testFilterActiveCustomersByNameAndPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String status = CustomerStatus.ACTIVE.name();
        String searchName = "john";
        String searchPhone = "123";

        Page<Customer> result = customerRepository.filterCustomers(searchName, searchPhone, status, pageRequest);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        List<Customer> customers = result.getContent();

        assertNotNull(customers);
        assertEquals(1, customers.size());

        Customer customer = customers.get(0);

        assertTrue(customer.getName().toLowerCase().contains(searchName));
        assertTrue(customer.getPhone().contains(searchPhone));
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then get customer by Id Returns the customer")
    void testGetCustomerById() {
        testCreateCustomer();

        Optional<Customer> optional = customerRepository.findById(1L);

        assertTrue(optional.isPresent());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then update the customer Returns the customer")
    void testUpdateCustomer() {
        testCreateCustomer();

        Long customerId = 1L;

        Optional<Customer> optional = customerRepository.findById(customerId);

        assertTrue(optional.isPresent());

        Customer customer = optional.get();
        customer.setName("Mike");
        customer.setUpdatedTime(new Date());

        customer = customerRepository.save(customer);

        assertEquals("Mike", customer.getName());
    }

    @Test
    @DisplayName("Given empty customer repository When customer added Then update the customer status to REMOVED Returns the customer")
    void testDeleteCustomer() {
        testCreateCustomer();

        Long customerId = 1L;

        Optional<Customer> optional = customerRepository.findById(customerId);

        assertTrue(optional.isPresent());

        Customer customer = optional.get();
        customer.setStatus(CustomerStatus.REMOVED);
        customer.setUpdatedTime(new Date());

        customer = customerRepository.save(customer);

        assertEquals(CustomerStatus.REMOVED, customer.getStatus());
    }

}