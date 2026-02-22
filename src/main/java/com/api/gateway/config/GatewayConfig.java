package com.api.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

		return builder.routes()
				.route("AUTH-SERVICE",
						r -> r.path("/authenticate/**").filters(f -> f.filter(filter)).uri("lb://AUTH-SERVICE"))
				.route("DEV-ISSUE-BOOK",
						r -> r.path("/dib/**").filters(f -> f.filter(filter)).uri("lb://DEV-ISSUE-BOOK"))
				.route("DEV-ISSUE-BOOK",
						r -> r.path("/api/issues/**").filters(f -> f.filter(filter)).uri("lb://DEV-ISSUE-BOOK"))
				.route("TAG-SERVICE",
						r -> r.path("/api/tags/**").filters(f -> f.filter(filter)).uri("lb://TAG-SERVICE"))
				.route("TAG-SERVICE",
						r -> r.path("/api/tag-assignments/**").filters(f -> f.filter(filter)).uri("lb://TAG-SERVICE"))
				.route("INTERVIEW-PREP-SERVICE",
						r -> r.path("/api/interview/**").filters(f -> f.filter(filter)).uri("lb://INTERVIEW-PREP-SERVICE"))
				.route("notification-service",
						r -> r.path("/notification/**").filters(f -> f.filter(filter)).uri("lb://notification-service"))
				.build();
	}
}