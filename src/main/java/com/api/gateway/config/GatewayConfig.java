package com.api.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.api.gateway.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

	@Autowired
	private JwtAuthenticationFilter filter;

	public GatewayConfig(JwtAuthenticationFilter filter) {
		this.filter = filter;
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {

		// lb://AUTH-SERVICE
		return builder.routes()
				.route("AUTH-SERVICE", r -> r.path("/authenticate/**").filters(f -> f.filter(filter)).uri("lb://AUTH-SERVICE"))
				.route("DEV-ISSUE-BOOK", r -> r.path("/dib/**").filters(f -> f.filter(filter)).uri("lb://DEV-ISSUE-BOOK"))
				.build();
	}
}