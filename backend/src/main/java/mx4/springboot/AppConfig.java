/*
 * 2020.04.08  - Created
 */
package mx4.springboot;

import java.util.Arrays;
import mx4.springboot.security.filters.JWTRequestFilter;
import mx4.springboot.services.DataRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Provides custom spring configuration including for security, CORS support, etc.
 *
 * @author Ilamah, Osho
 */
@Configuration
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private DataRepositoryUserDetailsService mongoUserDetailsService;

    @Autowired
    private JWTRequestFilter customJWTRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(mongoUserDetailsService);
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    /**
//     * FOR DEVELOPEMENT ONLY - to allow seperate dev server for front and backend. TODO - remove.
//     * @return 
//     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        //https://stackoverflow.com/a/41000211
        //https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/cors.html
        //https://github.com/spring-projects/spring-boot/issues/5834
        //https://github.com/axios/axios/issues/858
        // https://gist.github.com/CSKNK/4e4adde53a9c54f94e25e8a72f1251e8  https://gist.github.com/CSKNK/4e4adde53a9c54f94e25e8a72f1251e8
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3500", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        //configuration.setAllowCredentials(Boolean.TRUE);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override // see pp 104 in Craig Walls as well as https://dzone.com/articles/spring-security-and-custom-passwordnbspencoding
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();//TODO please remove - for development CORS support only

        //TODO See pp109/110 of Craig Wall as it appears there is more to this
        http.csrf().disable();// post will not work otherwise? Except with tymeleaf??,  See https://stackoverflow.com/questions/50486314/how-to-solve-403-error-in-spring-boot-post-request

        //http.authorizeRequests().antMatchers("**").permitAll();
        http.authorizeRequests()
                //
                // paths that require no authentication
                .antMatchers("/", "/signin", "/signout", "/fonts/**", "/authenticate", "/signup").permitAll()
                //
                //paths that require authenticated admin
                //.antMatchers("")
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                //
                // paths that require authenticated user
                //.antMatchers("/api/**", "/dashboard/**").authenticated().anyRequest().hasAnyAuthority("USER", "ADMIN") 
                .antMatchers("/api/**", "/dashboard/**").hasAnyAuthority("USER", "ADMIN").anyRequest().authenticated();

        //tell spring not to setup sessions - we will handle per request authentication by ourselves
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //inject our specialized JWT filter (before the default Spring Auth Filter) our filter will add the same artefacts to the SecurityContext for an authenticated user so 
        //that the default filter will not need to do anything more
        http.addFilterBefore(customJWTRequestFilter, UsernamePasswordAuthenticationFilter.class);

        //We are no longer using sessions
        ////configure logout support
        //http.logout()
        //        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //A filter/listener is setup to monitor these requests, and clear the session automagically
        //        .logoutSuccessUrl("/");
        //TODO configure exception handling  ||  more configs to be done see ExceptionHandlingConfigurer
        http.exceptionHandling();
        //	accessDeniedPage

        //switch on HTTP Basic based login - will work for non web client - but there are better options
        //TODO this is unsafe unless we are over https! Please Switch OFF!! options are Basic | OAuth2 | OpenID | API Keys
        //http.httpBasic();
    }

    @Override

    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/assets/**", "/static/**", "/css/**", "/js/**", "/img/**","/favicon.ico");
    }

    //Removed as we are working with SPA - thymeleaf and viewControlelr registry not needed
    //@Override
    //public void addViewControllers(ViewControllerRegistry registry) {
    //    registry.addViewController("/home").setViewName("home");
    //    registry.addViewController("/").setViewName("home");
    //    registry.addViewController("/dashboard").setViewName("dashboard");
    //    registry.addViewController("/login").setViewName("login");
    //}
}
