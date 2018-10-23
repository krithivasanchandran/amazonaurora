package Resilience.FailureRecovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * curl -X POST localhost:port/actuator/shutdown
 * Gracefully ShutsDown the Applications.
 * localhost:port/actuator/metrics
 * -- publishes the metrics of the Spring Boot Applications .
 */

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception{
        auth.inMemoryAuthentication().withUser("admin").password("krithi").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("act").password("act").roles("ACTUATOR");
    }

}