package ftn.bsep9.controller;

import ftn.bsep9.model.User;
import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class UserController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenUtils tokenUtils;


    public UserController(AuthenticationManager authenticationManager, UserService userService, TokenUtils tokenUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }


    @ResponseBody
    @GetMapping("/test-public")
    public ResponseEntity<String> testPublic() {
        return ResponseEntity.ok("Test public - success");
    }


    @ResponseBody
    @GetMapping("/test-private")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<String> testPrivate() {
        return ResponseEntity.ok("Test private - success");
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user_form", true);
        model.addAttribute("title", "Login");
        return "auth/login";
    }


    @ResponseBody
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails details = userService.loadUserByUsername(user.getUsername());
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", tokenUtils.generateToken(details));

            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public String changePassword(Model model) {
        return "auth/change-password";
    }

    @PutMapping(value = "/password")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public String changePassword(@RequestBody Map<String, String> params) {
        if (userService.changePassword(params)) {
            return "Valja";
        }
        return "Ne valja";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user_form", true);
        model.addAttribute("title", "Register");
        return "auth/register";
    }

    @ResponseBody
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user = userService.register(user);

        return ResponseEntity.ok("Successfully registered");
    }

}
