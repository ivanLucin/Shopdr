package com.shopdr.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopdr.common.entity.Setting;
import com.shopdr.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);
}
