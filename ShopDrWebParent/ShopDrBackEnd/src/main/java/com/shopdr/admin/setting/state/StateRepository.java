package com.shopdr.admin.setting.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopdr.common.entity.Country;
import com.shopdr.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
}
