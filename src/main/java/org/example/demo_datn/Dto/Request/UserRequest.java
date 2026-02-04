package org.example.demo_datn.Dto.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Date birthday;

}
