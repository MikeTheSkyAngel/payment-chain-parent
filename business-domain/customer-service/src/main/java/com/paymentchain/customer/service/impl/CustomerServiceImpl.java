package com.paymentchain.customer.service.impl;

import com.paymentchain.commons.dto.CustomerDTO;
import com.paymentchain.commons.exception.EntityAlreadyExistsException;
import com.paymentchain.commons.exception.NotFoundException;
import com.paymentchain.commons.statuses.CustomerStatus;
import com.paymentchain.customer.entity.Customer;
import com.paymentchain.customer.repository.CustomerRepository;
import com.paymentchain.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO request) {
        validateExistingCustomer(request.getName());
        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .status(CustomerStatus.ACTIVE)
                .createdTime(new Date())
                .build();
        customer = customerRepository.save(customer);
        return customer.toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers(String name, String phone, CustomerStatus status, Pageable pageable) {
        status = Objects.isNull(status) ? CustomerStatus.ACTIVE : status;
        Page<Customer> result = customerRepository.filterCustomers(name, phone, status.name(), pageable);
        List<CustomerDTO> response = new ArrayList<>();
        if (result.getTotalElements() == 0) {
            return response;
        }
        response = result.getContent().stream().map(Customer::toDTO).collect(Collectors.toList());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long id) throws NotFoundException {
        Customer customer = findCustomerById(id);
        return customer.toDTO();
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO request) throws NotFoundException {
        Customer existingCustomer = findCustomerById(id);
        validateExistingCustomer(id, request.getName());
        existingCustomer.setName(request.getName());
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setUpdatedTime(new Date());
        existingCustomer = customerRepository.save(existingCustomer);
        return existingCustomer.toDTO();
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) throws NotFoundException {
        Customer customer = findCustomerById(id);
        customer.setStatus(CustomerStatus.REMOVED);
        customer.setUpdatedTime(new Date());
        customerRepository.save(customer);
    }

    private Customer findCustomerById(Long id) {
        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException(String.format("Customer with id [%s] not found", id));
        }
        Customer customer = optional.get();
        if (CustomerStatus.REMOVED.equals(customer.getStatus())) {
            throw new NotFoundException(String.format("Customer with id [%s] not found", id));
        }
        return customer;
    }

    private void validateExistingCustomer(String name) {
        validateExistingCustomer(null, name);
    }

    private void validateExistingCustomer(Long id, String name) {
        CustomerStatus status = CustomerStatus.ACTIVE;
        Optional<Customer> optional;
        if (Objects.nonNull(id)) {
            optional = customerRepository.findCustomerByIdNotAndNameIgnoringCaseAndStatus(id, name, status);
        } else {
            optional = customerRepository.findCustomerByNameIgnoringCaseAndStatus(name, status);
        }
        if (optional.isPresent()) {
            throw new EntityAlreadyExistsException(String.format("Customer with name [%s] already exists", name));
        }
    }

}
