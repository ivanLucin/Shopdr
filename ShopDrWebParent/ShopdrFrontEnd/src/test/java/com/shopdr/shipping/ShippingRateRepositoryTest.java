package com.shopdr.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopdr.common.entity.Country;
import com.shopdr.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShippingRateRepositoryTest {

@Autowired private ShippingRateRepository repo;
	
	@Test
	public void testFindByCountryAndState() {
		Country usa = new Country(14);
		String state = "Queensland";
		ShippingRate shippingRate = repo.findByCountryAndState(usa, state);
		
		assertThat(shippingRate).isNotNull();
		System.out.println(shippingRate);
	}
}
