package com.shopdr.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopdr.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
}
