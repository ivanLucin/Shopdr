package com.shopdr.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopdr.common.entity.AuthenticationType;
import com.shopdr.common.entity.Country;
import com.shopdr.common.entity.Customer;
import com.shopdr.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CountryRepository coutryRepo;
	
	@Autowired 
	private CustomerRepository customerRepo;
	
	@Autowired PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		return coutryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);
		return customer == null;
	}
	public void registerCustomer(Customer customer) {
		encodedPassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
		
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}
	
	
	private void encodedPassword(Customer customer) {
		String endocedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(endocedPassword);
	}
	
	
		public boolean verify(String verificationCode) {
			Customer customer = customerRepo.findByVerificationCode(verificationCode);
			
			if (customer == null || customer.isEnabled()) {
				return false;
			} else {
				customerRepo.enable(customer.getId());
				return true;
			}
		}
		
		public void update(Customer customerInForm) {
			Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
			
			if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
				if (!customerInForm.getPassword().isEmpty()) {
					String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
					customerInForm.setPassword(encodedPassword);			
				} else {
					customerInForm.setPassword(customerInDB.getPassword());
				}		
			}
			
			customerInForm.setEnabled(customerInDB.isEnabled());
			customerInForm.setCreatedTime(customerInDB.getCreatedTime());
			customerInForm.setVerificationCode(customerInDB.getVerificationCode());
			customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
			customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

			
			customerRepo.save(customerInForm);
		}
		
		public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
			Customer customer = customerRepo.findByEmail(email);
			if (customer != null) {
				String token = RandomString.make(30);
				customer.setResetPasswordToken(token);
				customerRepo.save(customer);
				
				return token;
			} else {
				throw new CustomerNotFoundException("Could not find any customer with the email " + email);
			}
		}
		
		public Customer getByResetPasswordToken(String token) {
			return customerRepo.findByResetPasswordToken(token);
		}
		
		
		public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
			Customer customer = customerRepo.findByResetPasswordToken(token);
			if (customer == null) {
				throw new CustomerNotFoundException("No customer found: invalid token");
			}
			
			customer.setPassword(newPassword);
			customer.setResetPasswordToken(null);
			encodedPassword(customer);
			
			customerRepo.save(customer);
		}
		
		

}

