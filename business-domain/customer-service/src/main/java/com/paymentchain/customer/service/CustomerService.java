package com.paymentchain.customer.service;

import com.paymentchain.commons.dto.CustomerDTO;
import com.paymentchain.commons.exception.NotFoundException;
import com.paymentchain.commons.statuses.CustomerStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO request);

    List<CustomerDTO> getAllCustomers(String name, String phone, CustomerStatus status, Pageable pageable);

    CustomerDTO getCustomer(Long id) throws NotFoundException;

    CustomerDTO updateCustomer(Long id, CustomerDTO request) throws NotFoundException;

    void deleteCustomer(Long id) throws NotFoundException;

}
