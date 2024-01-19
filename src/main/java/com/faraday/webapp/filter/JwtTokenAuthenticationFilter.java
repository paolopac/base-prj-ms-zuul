package com.faraday.webapp.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.faraday.webapp.security.SecurityJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter
{
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);
	
	private final SecurityJWT securityJWT;

	public JwtTokenAuthenticationFilter(SecurityJWT securityJWT)
	{
		this.securityJWT = securityJWT;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException
	{

		// 1. SI ESTRAE L'HEADER DALLA REQUEST
		String header = request.getHeader(securityJWT.getHeader());

		// 2. VALIDAZIONE DELL'HEADER E CONTROLLO DEL PREFIX
		if (header == null || !header.startsWith(securityJWT.getPrefix()))
		{
			chain.doFilter(request, response); // SE NON VALIDO PROSEGUE CON IL FILTRO SUCCESSIVO 
			return;
		}

		// 3. SI ESTRAE LA SOLA STRINGA RIFERITA AL TOKEN
		String token = header.replace(securityJWT.getPrefix(), "");

		try
		{  //se il token è scaduto viena lanciata una eccezione		 
			// 4. DECODIFICA DEL TOKEN
			Claims claims = 
      Jwts.parser()
					.setSigningKey(securityJWT.getSecret().getBytes())
					.parseClaimsJws(token)
					.getBody();

      // ESTRAZIONE EMAIL DATO TOKEN
			String email = claims.getSubject();
			
			logger.warn("Elaborazione token utente (email): " + email);
			
			if (email != null)
			{
        // ESTRAZIONE RUOLI DATO TOKEN
				@SuppressWarnings("unchecked")
				List<String> roles = (List<String>) claims.get("authorities");

				// 5. SI RAPPRESENENTEA ALL'INTERNO DEL CONTESTO SPRING SECURITY L'UTENTE
        // ATTUALMENTE LOGGATO
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          email, 
          null,
          roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

				// 6. SI LOGGA L'UTENTE
				SecurityContextHolder.getContext().setAuthentication(auth);
        logger.info("Utente (email) "+ email +" loggato");
			}

		} catch (Exception e) {
			logger.error(" ERRORE VALIDAZIONE TOKEN "+ e.getMessage());
			SecurityContextHolder.clearContext();
		}

		// SI PROSEGUE CON IL SUCCESSIVO FILTRO 
		chain.doFilter(request, response);
	}
}
