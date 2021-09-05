package com.shopdr.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopdr.common.entity.Address;
import com.shopdr.common.entity.Country;
import com.shopdr.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {

@Autowired private AddressRepository repo;
	
	@Test
	public void testAddNew() {
		Integer customerId = 5;
		Integer countryId = 234; // USA
		
		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("Ante");
		newAddress.setLastName("Stipic");
		newAddress.setPhoneNumber("6575784165");
		newAddress.setAddressLine1("4213 Spinut Street");
		newAddress.setAddressLine2("21000 Split");
		newAddress.setCity("Chino");
		newAddress.setState("California");
		newAddress.setPostalCode("21000");
		
		Address savedAddress = repo.save(newAddress);
		
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCustomer() {
		Integer customerId = 5;
		List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
		assertThat(listAddresses.size()).isGreaterThan(0);
		
		listAddresses.forEach(System.out::println);
	}
	
	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	@Test
	public void testUpdate() {
		Integer addressId = 1;
		String phoneNumber = "646-232-3932";
		
		Address address = repo.findById(addressId).get();
		address.setPhoneNumber(phoneNumber);

		Address updatedAddress = repo.save(address);
		assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	@Test
	public void testDeleteByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;
		
		repo.deleteByIdAndCustomer(addressId, customerId);
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		assertThat(address).isNull();
	}	
	
	@Test
	public void testGetDefault() {
		Integer customerId = 5;
		Address address = repo.findDefaultByCustomer(customerId);
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	
}
