package org.spbstu.aleksandrov.billingsystem.crm;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    // TODO database
    private final Map<String, User> users;

    public UserRepository() {
        users = Map.of(
                "76390869089", User.builder().
                        login("76390869089").
                        phoneNumber(76390869089L).
                        password("password").
                        roles(List.of(new UserRole("ROLE_CUSTOMER"))).build(),
                "admin", User.builder().
                        login("admin").
                        password("admin").
                        roles(List.of(new UserRole("ROLE_ADMIN"), new UserRole("ROLE_CUSTOMER"), new UserRole("ROLE_MANAGER"))).build(),
                "manager", User.builder().
                        login("manager").
                        password("manager").
                        roles(List.of(new UserRole("ROLE_MANAGER"))).build()
        );
    }

    public User getUserByLogin(String login) {
        return users.get(login);
    }

}
