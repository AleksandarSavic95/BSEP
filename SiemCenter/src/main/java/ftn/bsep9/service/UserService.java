package ftn.bsep9.service;

import ftn.bsep9.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);

    User register(User user);

}
