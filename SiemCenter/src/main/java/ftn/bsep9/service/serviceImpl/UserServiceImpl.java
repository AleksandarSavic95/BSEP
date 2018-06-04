package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.exceptions.UserAlreadyExistsException;
import ftn.bsep9.model.Role;
import ftn.bsep9.model.User;
import ftn.bsep9.repository.UserRepository;
import ftn.bsep9.security.SecurityUtils;
import ftn.bsep9.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRole(Role.OPERATOR);
        return userRepository.save(user);
    }

    @Override
    public Boolean changePassword(Map<String, String> params) {
        User user = getLoggedUser();
        if (user == null) {
            return false;
        }
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        if (!(params.get("new-password").equals(params.get("repeat-password")))) {
            return false;
        }
        if (!passwordEncoder.matches(params.get("old-password"), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(params.get("new-password")));
        System.out.println(user.getPassword());
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities
        );
    }

    private User getLoggedUser() {
        String username = SecurityUtils.getUsernameOfLoggedUser();
        if (username == null) {
            return null;
        }
        return userRepository.findByUsername(username);
    }
}
