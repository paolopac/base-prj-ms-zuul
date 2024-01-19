package com.faraday.webapp.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.faraday.webapp.dto.response.ErrorDTORes;
import com.faraday.webapp.filter.JwtTokenAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private SecurityJWT securityJWT;

	private static final String[] WHITE_LIST = { 
    "/api/ms0/auth/**",
    "/api/ms0/user/create/**",
    "/api/ms0/swagger-ui.html",
    "/api/ms0/actuator/**",
    "/api/ms0/v2/api-docs", // NON DIRETTAMENTE ESPOSTI MA NECESSARI AFFINCHE' VENGA VISUALIZZATA LA DOCUMENTAZIONE SWAGGER
    "/api/ms0/configuration/ui", // NON DIRETTAMENTE ESPOSTI MA NECESSARI AFFINCHE' VENGA VISUALIZZATA LA DOCUMENTAZIONE SWAGGER
    "/api/ms0/swagger-resources/**",// NON DIRETTAMENTE ESPOSTI MA NECESSARI AFFINCHE' VENGA VISUALIZZATA LA DOCUMENTAZIONE SWAGGER
    "/api/ms0/configuration/security",// NON DIRETTAMENTE ESPOSTI MA NECESSARI AFFINCHE' VENGA VISUALIZZATA LA DOCUMENTAZIONE SWAGGER
    "/api/ms0/webjars/**" };// NON DIRETTAMENTE ESPOSTI MA NECESSARI AFFINCHE' VENGA VISUALIZZATA LA DOCUMENTAZIONE SWAGGER
	private static final String[] ADMIN_MATCHER = { "/api/ms0/user/create/**" };
	

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http.csrf().disable()
     .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // si istruisce spring security di non di non utilizzare sessioni HTTP 
     .exceptionHandling()
     .accessDeniedHandler(accessDeniedHandler()).and()
     .exceptionHandling()
     .authenticationEntryPoint(authenticationEntryPoint()).and()
     // SI AGGIUNGE UN FILTRO DI VALIDAZIONE DEI TOKEN AD OGNI RICHIESTA
     .addFilterAfter(new JwtTokenAuthenticationFilter(securityJWT), UsernamePasswordAuthenticationFilter.class)
      // SI DEFINISCONO LE REGOLE DI AUTENTICAZIONE
     .authorizeRequests()
     // Abilita l'URL POST specificato nel config
     .antMatchers(WHITE_LIST).permitAll()
     .antMatchers(ADMIN_MATCHER).hasAnyRole("ADMIN")
     // QUALSIASI ALTRA RICHIESTA E' NECESSARIO SIA AUTENTICATA
     .anyRequest().authenticated();
	}

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      // Puoi creare un oggetto ErrorResponse personalizzato
      ErrorDTORes errorResponse = new ErrorDTORes();
      errorResponse.setDate(null);
      errorResponse.setCode(HttpServletResponse.SC_FORBIDDEN);
      errorResponse.setMessage("Accesso negato: " + accessDeniedException.getMessage());
      // Converti l'oggetto ErrorResponse in JSON e scrivi nella response
      String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
      response.getWriter().write(jsonResponse);
    };
  }

   @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
          // SI CREA UNA RESPONSE PERSONALIZZATA IN CASO DI ERRORI IN FASE DI VERIFICA DEL TOKEN
          ErrorDTORes errorResponse = new ErrorDTORes();
          errorResponse.setDate(null);
          errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
          errorResponse.setMessage("Error in validation token " + authException.getMessage());
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }
}
