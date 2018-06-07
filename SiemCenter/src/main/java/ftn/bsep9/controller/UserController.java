package ftn.bsep9.controller;

import ftn.bsep9.model.User;
import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails details = userService.loadUserByUsername(user.getUsername());
            return new ResponseEntity<>(tokenUtils.generateToken(details), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Login failed. Bad username or password.", HttpStatus.UNAUTHORIZED);
        }
    }


//    @PostMapping(value = "/password")
////    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
//    public ResponseEntity<String> changePassword(@RequestParam Map<String, String> params) {
//        if (userService.changePassword(params)) {
//            return new ResponseEntity<String>(HttpStatus.OK, "asd");
//        }
//        return new ResponseEntity<String>(HttpStatus.OK, "asd");
//    }


}
