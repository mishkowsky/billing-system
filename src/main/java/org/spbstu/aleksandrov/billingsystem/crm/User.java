package org.spbstu.aleksandrov.billingsystem.crm;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {
    private final String login;
    private final String password;
    private final long phoneNumber;
    private final List<UserRole> roles;
}
