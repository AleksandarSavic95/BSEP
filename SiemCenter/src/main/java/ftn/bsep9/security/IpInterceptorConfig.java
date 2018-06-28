package ftn.bsep9.security;

import ftn.bsep9.controller.interceptor.IpInterceptor;
import ftn.bsep9.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IpInterceptorConfig implements WebMvcConfigurer {

    private final UserSecurityService userSecurityService;

    @Autowired
    public IpInterceptorConfig(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new IpInterceptor(userSecurityService));
    }
}
