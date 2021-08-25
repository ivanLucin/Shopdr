package com.shopdr.admin.setting.country;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import com.shopdr.common.entity.Country;

public interface Countryrepository extends CrudRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
}
