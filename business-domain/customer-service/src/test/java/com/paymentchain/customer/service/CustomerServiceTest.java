package com.paymentchain.customer.service;

import com.paymentchain.commons.dto.CustomerDTO;
import com.paymentchain.commons.exception.EntityAlreadyExistsException;
import com.paymentchain.commons.exception.NotFoundException;
import com.paymentchain.commons.statuses.CustomerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Test
    @DisplayName("Given empty customer service When customer added Returns the customer")
    void testCreateCustomer() {
        CustomerDTO request = new CustomerDTO();
        request.setName("John");
        request.setPhone("1234567890");

        createCustomer(request);
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then try to create another customer with the same name Throws an existing customer exception")
    void testCreateCustomerWithExistingName() {
        testCreateCustomer();

        CustomerDTO request = new CustomerDTO();
        request.setName("John");
        request.setPhone("5566778899");

        assertThrows(EntityAlreadyExistsException.class, () -> customerService.createCustomer(request));
    }

    @Test
    @DisplayName("Given empty customer service When get all customers Returns empty list")
    void testGetAllCustomers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<CustomerDTO> customers = customerService.getAllCustomers(null, null, null, pageRequest);

        assertNotNull(customers);
        assertEquals(0, customers.size());
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then get all active customers Returns the active customers")
    void testGetAllActiveCustomers() {
        testCreateCustomer();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<CustomerDTO> customers = customerService.getAllCustomers(null, null, null, pageRequest);

        assertNotNull(customers);
        assertEquals(1, customers.size());
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then get all inactive customers Returns an empty list")
    void testGetAllInactiveCustomers() {
        testCreateCustomer();

        PageRequest pageRequest = PageRequest.of(0, 10);
        CustomerStatus status = CustomerStatus.REMOVED;
        List<CustomerDTO> customers = customerService.getAllCustomers(null, null, status, pageRequest);

        assertNotNull(customers);
        assertEquals(0, customers.size());
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then filter active customers by existing name Returns the customer")
    void testFilterActiveCustomersByExistingName() {
        testCreateCustomer();

        PageRequest pageRequest = PageRequest.of(0, 10);
        String searchName = "joh";

        List<CustomerDTO> customers = customerService.getAllCustomers(searchName, null, null, pageRequest);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        CustomerDTO customer = customers.get(0);

        assertTrue(customer.getName().toLowerCase().contains(searchName));
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then filter active customers by non-existing name Returns empty list")
    void testFilterActiveCustomersByNonExistingName() {
        testCreateCustomer();

        PageRequest pageRequest = PageRequest.of(0, 10);
        String searchName = "bab";

        List<CustomerDTO> customers = customerService.getAllCustomers(searchName, null, null, pageRequest);

        assertNotNull(customers);
        assertEquals(0, customers.size());
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then filter active customers by existing phone Returns the customer")
    void testFilterActiveCustomersByExistingPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String searchPhone = "123";

        List<CustomerDTO> customers = customerService.getAllCustomers(null, searchPhone, null, pageRequest);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        CustomerDTO customer = customers.get(0);

        assertTrue(customer.getPhone().contains(searchPhone));
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then filter active customers by non-existing phone Returns empty list")
    void testFilterActiveCustomersByNonExistingPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String searchPhone = "555667";

        List<CustomerDTO> customers = customerService.getAllCustomers(null, searchPhone, null, pageRequest);

        assertNotNull(customers);
        assertEquals(0, customers.size());
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then filter active customers by existing name and phone Returns the customer")
    void testFilterActiveCustomersByExistingNameAndPhone() {
        testCreateCustomer();
        PageRequest pageRequest = PageRequest.of(0, 10);
        String searchName = "john";
        String searchPhone = "123";

        List<CustomerDTO> customers = customerService.getAllCustomers(null, searchPhone, null, pageRequest);

        assertNotNull(customers);
        assertEquals(1, customers.size());

        CustomerDTO customer = customers.get(0);

        assertTrue(customer.getName().toLowerCase().contains(searchName));
        assertTrue(customer.getPhone().contains(searchPhone));
    }

    @Test
    @DisplayName("Given empty customer service When get customer by non-existing Id Throws an not found exception")
    void testGetCustomerByNonExistingId() {
        assertThrows(NotFoundException.class, () -> customerService.getCustomer(1L));
    }

    @Test
    @DisplayName("Given empty customer service When customer added Then get customer by Id Returns the customer")
    void testGetCustomerById() {
        testCreateCustomer();

        CustomerDTO customer = customerService.getCustomer(1L);

        assertNotNull(customer);
    }

    @Test
    @DisplayName("Given no customers When update customer info Throws NotFoundException")
    void testUpdateNonExistingCustomer() {
        CustomerDTO request = new CustomerDTO();
        request.setName("Mike");
        request.setPhone("1234567890");

        assertThrows(NotFoundException.class, () -> customerService.updateCustomer(1L, request));
    }

    @Test
    @DisplayName("Given a customer When update customer info with him own name Returns the updated customer info")
    void testUpdateCustomerInfoWithHimOwnName() {
        testCreateCustomer();

        CustomerDTO request = new CustomerDTO();
        request.setName("John");
        request.setPhone("1234567890");

        CustomerDTO response = customerService.updateCustomer(1L, request);

        assertNotNull(response);
        assertNotNull(response.getUpdatedTime());
        assertEquals("John", response.getName());
        assertEquals("1234567890", response.getPhone());
        assertEquals(CustomerStatus.ACTIVE.name(), response.getStatus());
    }

    @Test
    @DisplayName("Given two customers When update customer info with an existing name Throws EntityAlreadyExistsException")
    void testUpdateCustomerInfoWithExistingName() {
        testCreateCustomer();

        CustomerDTO request = new CustomerDTO();
        request.setName("Mike");
        request.setPhone("9876543210");

        CustomerDTO customer = createCustomer(request);

        CustomerDTO updateRequest = new CustomerDTO();
        updateRequest.setName("John");
        updateRequest.setPhone("9876543210");

        assertThrows(EntityAlreadyExistsException.class, () -> customerService.updateCustomer(customer.getId(), updateRequest));
    }

    @Test
    @DisplayName("Given a customer When update customer info Returns the updated customer info")
    void testUpdateCustomer() {
        testCreateCustomer();

        CustomerDTO request = new CustomerDTO();
        request.setName("Mike");
        request.setPhone("1234567890");

        CustomerDTO response = customerService.updateCustomer(1L, request);

        assertNotNull(response);
        assertNotNull(response.getUpdatedTime());
        assertEquals("Mike", response.getName());
        assertEquals("1234567890", response.getPhone());
        assertEquals(CustomerStatus.ACTIVE.name(), response.getStatus());
    }

    @Test
    @DisplayName("Given no customers When try to delete a non existing customer Throws NotFoundException")
    void testDeleteNonExistingCustomer() {
        assertThrows(NotFoundException.class, () -> customerService.deleteCustomer(1L));
    }

    @Test
    @DisplayName("Given a customer When delete the customer Then get customer by Id Throws NotFoundException")
    void testDeleteExistingCustomer() {
        testCreateCustomer();

        customerService.deleteCustomer(1L);

        assertThrows(NotFoundException.class, () -> customerService.getCustomer(1L));
    }

    private CustomerDTO createCustomer(CustomerDTO request) {
        CustomerDTO response = customerService.createCustomer(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getCreatedTime());
        assertEquals(CustomerStatus.ACTIVE.name(), response.getStatus());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getPhone(), response.getPhone());

        return response;
    }

}