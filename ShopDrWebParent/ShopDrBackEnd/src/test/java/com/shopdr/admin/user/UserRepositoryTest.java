package com.shopdr.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopdr.common.entity.Role;
import com.shopdr.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User newUser = new User("ivanlucin@gmail.com", "admin_il", "Mate", "Lucin");
		newUser.addRole(roleAdmin);
		
		User savedUser = repo.save(newUser);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void CreateNewUserWithTwoRoles() {
		User userTR = new User("TwoRoles@gmail.com", "TwoRoles", "Stipe", "Matic");
				Role roleEditor = new Role(2);
				Role roleAssistant = new Role(3);
				
				userTR.addRole(roleEditor);
				userTR.addRole(roleAssistant);
				
				User saveUser = repo.save(userTR);
				
				assertThat(saveUser.getId()).isGreaterThan(0);
				
				
	}
	
	@Test
	public void testUpdateUserDetails() {
		User findUser = repo.findById(1).get();
		findUser.setEnabled(true);
		findUser.setEmail("il46864@unist.hr");
		
		repo.save(findUser);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 3;
		repo.deleteById(userId);
		
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "il46864@unist.hr";
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
}
