/**
 * 
 */
package net.rickcee.fix.generator.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author rickcee
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable().authorizeRequests()
		.antMatchers("/HealthCheck").permitAll()
		.antMatchers("/public/**").permitAll()
		.antMatchers("/actuator/**").permitAll()
		.antMatchers("/FixWebUI").permitAll()
		.antMatchers("/secured/**").authenticated()
		.anyRequest().permitAll()
		
		.and().formLogin()
			.loginPage("/login").permitAll()
			.defaultSuccessUrl("/FixWebUI")
			.failureForwardUrl("/login")
			.failureUrl("/login")
			.usernameParameter("username")
			.passwordParameter("password")
		
		.and().logout()
			.logoutSuccessUrl("/login")
			.invalidateHttpSession(true)
		
		;
		// @formatter:on
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/js/**");
		web.ignoring().antMatchers("/images/**");
		web.ignoring().antMatchers("/fonts/**");
		web.ignoring().antMatchers("/json/**");
		web.ignoring().antMatchers("/angularservices/**");
		web.ignoring().antMatchers("/controllers/**");
		web.ignoring().antMatchers("/templates/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("user").roles("USER").and()
			.withUser("admin").password("admin").roles("ADMIN")
		;
	}

}
