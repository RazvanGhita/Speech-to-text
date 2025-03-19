package com.wtztechnologies.speechtotext.authentication.service;

import com.google.gson.Gson;
import com.wtztechnologies.speechtotext.authentication.JwtTokenUtil;
import com.wtztechnologies.speechtotext.authentication.model.JwtAuthorities;
import com.wtztechnologies.speechtotext.authentication.model.JwtPrincipal;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.wtztechnologies.speechtotext.entities.UserDetails existingUser = userDetailsRepository
				.findByUsername(username).orElse(null);
		if (existingUser != null) {
			return new User(existingUser.getUsername(), existingUser.getPassword(),
					jwtTokenUtil.getAuthority(existingUser));
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	public JwtAuthorities decodeJWT(UsernamePasswordAuthenticationToken principal) {
		JwtPrincipal jwtPrincipal = (JwtPrincipal) principal.getPrincipal();
		String json = principal.getAuthorities().toString().replace('[', ' ');
		json = json.replace(']', ' ');
		Gson g = new Gson();
		String username = jwtTokenUtil.getAllClaimsFromToken(jwtPrincipal.getJwtToken()).get("sub").toString();
		String role = jwtTokenUtil.getAllClaimsFromToken(jwtPrincipal.getJwtToken()).get("role").toString();
		JwtAuthorities s = g.fromJson(json, JwtAuthorities.class);
		if (s != null) {
			s.setUsername(username);
		} else {
			s = new JwtAuthorities();
			s.setUsername(username);
			s.setRole(role);
		}
		return s;
	}

	// public void checkUserAuthority(String tokeRole, String accesRole) throws
	// Response{
	// if (!tokeRole.equals(accesRole)) {
	// new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	// }
	// }
}