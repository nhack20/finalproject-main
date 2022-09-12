package com.example.finalezlearning.auth.config;
import com.example.finalezlearning.auth.filter.AuthTokenFilter;
import com.example.finalezlearning.auth.filter.ExceptionHandlerFilter;
import com.example.finalezlearning.auth.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
// указывает Spring контейнеру, чтобы находил файл конфигурации в классе. debug = true - для просмотра лога какие бины были созданы, в production нужно ставить false
public class SpringConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsServiceImpl userDetailsService;
    private AuthTokenFilter authTokenFilter;
    private ExceptionHandlerFilter exceptionHandlerFilter;

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) { // внедряем наш компонент Spring @Service
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setAuthTokenFilter(AuthTokenFilter authTokenFilter) { // внедряем фильтр
        this.authTokenFilter = authTokenFilter;
    }

    @Autowired
    public void setExceptionHandlerFilter(ExceptionHandlerFilter exceptionHandlerFilter) {
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    //Spring security
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Spring security
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //Spring security
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public FilterRegistrationBean registration(AuthTokenFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /* если используется другая клиентская технология (не SpringMVC, а например Angular, React и пр.),
            то выключаем встроенную Spring-защиту от CSRF атак,
            иначе запросы от клиента не будут обрабатываться, т.к. Spring Security будет пытаться в каждом входящем запроcе искать спец. токен для защиты от CSRF
        */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable(); // на время разработки проекта не будет ошибок (для POST, PUT и др. запросов) - недоступен и т.д.
        http.formLogin().disable(); // отключаем, т.к. форма авторизации создается не на Spring технологии (например, Spring MVC + JSP), а на любой другой клиентской технологии
        http.httpBasic().disable(); // отключаем стандартную браузерную форму авторизации
        http.requiresChannel().anyRequest().requiresSecure(); // обязательное исп. HTTPS
        http.addFilterBefore(authTokenFilter, SessionManagementFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, AuthTokenFilter.class);
    }
}

