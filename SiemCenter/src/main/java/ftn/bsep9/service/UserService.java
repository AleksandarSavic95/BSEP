package ftn.bsep9.service;

import ftn.bsep9.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;


public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);

    User register(User user);

    Boolean changePassword(Map<String, String> params);
}
