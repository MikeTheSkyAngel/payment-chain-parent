package com.paymentchain.customer.repository;

import com.paymentchain.commons.statuses.CustomerStatus;
import com.paymentchain.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    String GET_CUSTOMERS_BY_FILTER = "SELECT c.* FROM customer c WHERE c.status = :status " +
            "AND ((:name IS NOT NULL AND UPPER(c.name) LIKE UPPER(CONCAT('%', :name, '%'))) OR :name IS NULL) " +
            "AND ((:phone IS NOT NULL AND c.phone LIKE CONCAT('%', :phone, '%')) OR :phone IS NULL)";
    String COUNT_CUSTOMERS_BY_FILTER = "SELECT COUNT(c.id) FROM customer c WHERE c.status = :status " +
            "AND ((:name IS NOT NULL AND UPPER(c.name) LIKE UPPER(CONCAT('%', :name, '%'))) OR :name IS NULL) " +
            "AND ((:phone IS NOT NULL AND c.phone LIKE CONCAT('%', :phone, '%')) OR :phone IS NULL)";

    @Query(value = GET_CUSTOMERS_BY_FILTER, countQuery = COUNT_CUSTOMERS_BY_FILTER, nativeQuery = true)
    Page<Customer> filterCustomers(@Param("name") String name, @Param("phone") String phone, @Param("status") String status, Pageable pageable);

    Optional<Customer> findCustomerByNameIgnoringCaseAndStatus(String name, CustomerStatus status);

    Optional<Customer> findCustomerByIdNotAndNameIgnoringCaseAndStatus(Long id, String name, CustomerStatus status);

}
