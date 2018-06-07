package ftn.bsep9.controller;

import ftn.bsep9.model.User;
import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserService;
import org.apache.http.protocol.HTTP;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
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


    @GetMapping("/test-public")
    public ResponseEntity<String> testPublic() {
        return ResponseEntity.ok("Test public - success");
    }


    @GetMapping("/test-operator")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<String> testPrivate() {
        System.out.println("\n OPERATOR token stigao");
        return ResponseEntity.ok("Test private - success");
    }


    @GetMapping("/test-admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> testAdmin() {
        System.out.println("\n ADMIN token stigao");
        return ResponseEntity.ok("Test admin - success");
    }


    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            System.out.println("\n LOGIN POST");
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());
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


//    @PostMapping(value = "/password")
////    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
//    public ResponseEntity<String> changePassword(@RequestParam Map<String, String> params) {
//        if (userService.changePassword(params)) {
//            return "logs-view";
//        }
//        return new ResponseEntity<String>(HttpStatus.OK, 'asd');
//    }


}
