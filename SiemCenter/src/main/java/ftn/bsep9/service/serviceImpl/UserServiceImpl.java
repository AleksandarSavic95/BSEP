package ftn.bsep9.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private final String PERMISSIONS_FILE = "src/main/resources/security/roles-permissions.json";

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

        // check if old password matches the user's password
        if (!passwordEncoder.matches(params.get("old-password"), user.getPassword())) {
            return false;
        }

        // password must have more than 6 characters
        if (params.get("new-password").length() < 6) {
            return false;
        }

        // password must contain at least one digit
        if (!params.get("new-password").matches(".*[0-9].*")) {
            return false;
        }

        // password must contain both small and capital letters
        if (!params.get("new-password").matches("(.*[A-Z].*[a-z].*)|(.*[a-z].*[A-Z].*)")) {
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

        ObjectMapper mapper = new ObjectMapper();

        try {
            HashMap<String, List<String>> permissions = mapper.readValue(new File(PERMISSIONS_FILE), HashMap.class);
            for (String permission : permissions.get(user.getRole().name())) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission));
                System.out.println(permission);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

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
