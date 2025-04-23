package com.mayishen.core.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private Integer role;
}
