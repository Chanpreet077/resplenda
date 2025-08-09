package com.example.resplenda.security;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("---- Incoming Headers ----");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			System.out.println(headerName + ": " + request.getHeader(headerName));
		}
		System.out.println("--------------------------");

		try {
			final String authHeader = request.getHeader("Authorization");

			// ✅ Debug print for header
			System.out.println("JWT Header: " + authHeader);

			String username = null;
			String jwt = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				jwt = authHeader.substring(7);

				// ✅ Debug print for token
				System.out.println("Extracted Token: " + jwt);
				System.out.println("Username from token: " + username);

				try {
					username = jwtUtil.getUsernameFromToken(jwt);

					// ✅ Debug print for extracted username
					System.out.println("Username from token: " + username);

				} catch (Exception e) {
					System.out.println("JWT parsing error: " + e.getMessage());
				}
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				// ✅ Debug print for extracted username
				System.out.println("Username from token: " + username);
				if (jwtUtil.validateToken(jwt)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// ✅ Debug print for authentication
					System.out.println("Setting authentication for user: " + username);

					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			// Log error or handle invalid token scenario here
			System.out.println("JWT processing failed: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

}
