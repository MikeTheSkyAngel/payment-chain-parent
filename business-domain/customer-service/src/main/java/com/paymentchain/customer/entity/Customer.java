package com.paymentchain.customer.entity;

import com.paymentchain.commons.dto.CustomerDTO;
import com.paymentchain.commons.statuses.CustomerStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

import static com.paymentchain.commons.Constants.DATE_TIME_FORMAT;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    public CustomerDTO toDTO() {
        String updatedDate = Objects.nonNull(updatedTime) ? DATE_TIME_FORMAT.format(updatedTime) : null;
        return CustomerDTO.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .status(status.name())
                .createdTime(DATE_TIME_FORMAT.format(createdTime))
                .updatedTime(updatedDate)
                .build();
    }

}
