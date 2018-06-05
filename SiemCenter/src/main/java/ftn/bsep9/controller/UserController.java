package ftn.bsep9.controller;

import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserService;
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
    @GetMapping("/test-operator")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<String> testPrivate() {
        System.out.println("\n OPERATOR token stigao");
        return ResponseEntity.ok("Test private - success");
    }


    @ResponseBody
    @GetMapping("/test-admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> testAdmin() {
        System.out.println("\n ADMIN token stigao");
        return ResponseEntity.ok("Test admin - success");
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user_form", true);
        model.addAttribute("title", "Login");
        return "auth/login";
    }


    @PostMapping(value = "/login")
    public String login(@RequestParam Map<String, String> body, Model model,
                        RedirectAttributes redirectAttributes, HttpSession session
    ) {
        try {
            System.out.println(body.get("username"));
            System.out.println(body.get("password"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password"));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails details = userService.loadUserByUsername(body.get("username"));

            redirectAttributes.addFlashAttribute("token", tokenUtils.generateToken(details));
//            session.setAttribute("token","tokenUtils.generateToken(details)");
//            session.setAttribute("sesasd","SESasd");
            return "redirect:/api/logs/all";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Wrong username or password. " + ex.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/change-password")
//    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public String changePassword(Model model) {
        return "auth/change-password";
    }

    @PostMapping(value = "/password")
//    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public String changePassword(@RequestParam Map<String, String> params) {
        if (userService.changePassword(params)) {
            return "logs-view";
        }
        return "logs-view";
    }


}
