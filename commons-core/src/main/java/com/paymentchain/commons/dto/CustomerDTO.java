package com.paymentchain.commons.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerDTO implements Serializable {

    private Long id;
    private String name;
    private String phone;
    private String status;
    private String createdTime;
    private String updatedTime;

}
