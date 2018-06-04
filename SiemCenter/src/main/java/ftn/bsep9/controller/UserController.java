package ftn.bsep9.controller;

import com.sun.media.jfxmedia.Media;
import ftn.bsep9.model.User;
import ftn.bsep9.security.TokenUtils;
import ftn.bsep9.service.UserService;
import javafx.application.Application;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.server.header.ContentTypeOptionsServerHttpHeadersWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public String login (Model model) {
        model.addAttribute("user_form",true);
        model.addAttribute("title", "Login");
        return "auth/login";
    }


    @PostMapping(value = "/login")
    public String login(@RequestParam Map<String, String> body, Model model,
                              RedirectAttributes redirectAttributes
    ) {
        try {
            System.out.println(body.get("username"));
            System.out.println(body.get("password"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password"));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails details = userService.loadUserByUsername(body.get("username"));

            redirectAttributes.addFlashAttribute("token", tokenUtils.generateToken(details));

            return "redirect:/api/logs/all";
        } catch (Exception ex) {
            model.addAttribute("errorMessage","Wrong username or password. " + ex.getMessage());
            return "auth/login";
        }
    }


}
