package com.shopdr.shipping;

import org.springframework.data.repository.CrudRepository;

import com.shopdr.common.entity.Country;
import com.shopdr.common.entity.ShippingRate;


public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {

	public ShippingRate findByCountryAndState(Country country, String state);
}
