package com.shopdr.admin.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopdr.common.entity.Setting;
import com.shopdr.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired private SettingRepository repo;
	
	public List<Setting> listAllSettings() {
		return (List<Setting>) repo.findAll();
	}
	
	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}
	
	public List<Setting> getMailServerSettings() {
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplatesSettings() {
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	
}
