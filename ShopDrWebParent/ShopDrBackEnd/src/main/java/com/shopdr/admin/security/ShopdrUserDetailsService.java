package com.shopdr.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopdr.admin.user.UserRepository;
import com.shopdr.common.entity.User;

public class ShopdrUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByEmail(email);
		if(user != null) {
			return new ShopdrUserDetails(user);
		}
		
		throw new UsernameNotFoundException("Could not find user with email: " + email);
		
	}

}
