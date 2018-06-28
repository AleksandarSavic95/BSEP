package ftn.bsep9.controller.interceptor;

import ftn.bsep9.service.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// No need for this since we pass UserSecurityService through constructor
// @Component
public class IpInterceptor extends HandlerInterceptorAdapter {
    // @Autowired
    private UserSecurityService userSecurityService;

    public IpInterceptor() {
    }

    public IpInterceptor(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }


    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        System.out.println("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI());

        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddress = (ip == null) ? request.getRemoteAddr() : ip;

        boolean ipCanTryToLogin = userSecurityService.canIpTryToLogin(ipAddress);
        if (! ipCanTryToLogin) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            try {
                response.getWriter().write("You shall not pass! Your IP is blocked!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // can't be here since preHandle is called before ANY request handling!
            // userSecurityService.saveLoginTry("", ipAddress);
        }

        return ipCanTryToLogin;
    }
}
