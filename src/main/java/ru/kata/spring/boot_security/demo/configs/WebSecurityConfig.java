package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private RoleService roleService;
    private final SuccessUserHandler successUserHandler;
    @Autowired
    public WebSecurityConfig(UserService userService, RoleService roleService, SuccessUserHandler successUserHandler) {
        this.userService = userService;
        this.roleService = roleService;
        this.successUserHandler = successUserHandler;
    }


    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers( "/login").permitAll()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/user").hasAnyAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);

        return authenticationProvider;
    }

    @PostConstruct
    public void init() {
    Role roleAdmin = new Role(1L, "ADMIN");
    Role roleUser = new Role(2L, "USER");
    roleService.addRole(roleAdmin);
    roleService.addRole(roleUser);

    User admin = new User();
    admin.setName("admin");
    admin.setSurname("admin");
    admin.setEmail("admin@admin.com");
    admin.setAge(23);
    admin.setPassword("admin");
    admin.addRoleToCollection(roleAdmin);
    admin.addRoleToCollection(roleUser);
    userService.saveUser(admin);

    User user = new User();
    user.setName("user");
    user.setSurname("userov");
    user.setEmail("user@user.com");
    user.setAge(43);
    user.setPassword("user");
    user.addRoleToCollection(roleUser);
    userService.saveUser(user);
    }

////     аутентификация inMemory
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("user")
//                        .password("user")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

}