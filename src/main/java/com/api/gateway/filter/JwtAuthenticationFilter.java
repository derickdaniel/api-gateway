package com.api.gateway.filter;

import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.api.gateway.exception.JwtTokenMalformedException;
import com.api.gateway.exception.JwtTokenMissingException;
import com.api.gateway.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Implementing GatewayFilter, this should be configured using code, for
 * application properties config. we need to use GatewayFilterFactory
 * implementation
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {

	Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		log.info("JwtAuthenticationFilter | filter is working");

		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

		final List<String> apiEndpoints = List.of("/signup", "/login", "/refreshtoken");

		Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream()
				.noneMatch(uri -> r.getURI().getPath().contains(uri));

		log.info("JwtAuthenticationFilter | filter | isApiSecured.test(request) : " + isApiSecured.test(request));

		if (isApiSecured.test(request)) {
			if (!request.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);

				return response.setComplete();
			}

			final String authorization = request.getHeaders().getOrEmpty("Authorization").get(0);
			final String token = authorization.replace("Bearer ", "");

			log.info("JwtAuthenticationFilter | filter | token : " + token);

			try {
				jwtUtils.validateJwtToken(token);
			} catch (ExpiredJwtException e) {
				log.info("JwtAuthenticationFilter | filter | ExpiredJwtException | error : " + e.getMessage());
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);

				return response.setComplete();

			} catch (IllegalArgumentException | MalformedJwtException | JwtTokenMissingException
					| io.jsonwebtoken.SignatureException | UnsupportedJwtException e) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);

				return response.setComplete();
			}

			Claims claims = jwtUtils.getClaims(token);
			exchange.getRequest().mutate().header("username", String.valueOf(claims.get("username"))).build();
		}

		return chain.filter(exchange);
	}
}
