package com.inn.cafe.wrapper;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserWrapper {

    private Integer id;

    private String name;

    private String contactNumber;

    private String email;

    private boolean status;
}
