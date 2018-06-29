package ftn.bsep9.controller;

import ftn.bsep9.model.User;
import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserSecurityService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenUtils tokenUtils;
    private UserSecurityService userSecurityService;


    public UserController(AuthenticationManager authenticationManager, UserService userService, TokenUtils tokenUtils, UserSecurityService userSecurityService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userSecurityService = userSecurityService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddress = (ip == null || ip.length() == 0) ? request.getRemoteAddr() : ip;
        System.out.println("ip: " + ipAddress); // ex. 192.168.1.4

        String username = user.getUsername();
        if (! userSecurityService.canUserTryToLogin(username)) {
            // blocked username LoginTry is STILL an IP try!
            userSecurityService.saveLoginTry(null, ipAddress);
            return new ResponseEntity<>("Too many tries! Please try later!",
                    HttpStatus.FORBIDDEN);
        }

        // IP this could be moved to IpInterceptor#preHandle, but
        // that will mean IP would get blocked after any X requests
        userSecurityService.saveLoginTry(username, ipAddress); // !!!

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, user.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails details = userService.loadUserByUsername(username);
            return new ResponseEntity<>(tokenUtils.generateToken(details), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Login failed. Bad username or password.", HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping(value = "/password")
//    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public ResponseEntity<String> changePassword(@RequestParam Map<String, String> params) {
        if (userService.changePassword(params)) {
            return new ResponseEntity<>("Your have changed your password", HttpStatus.OK);
        }
        return new ResponseEntity<>("Try again", HttpStatus.BAD_REQUEST);
    }


}
