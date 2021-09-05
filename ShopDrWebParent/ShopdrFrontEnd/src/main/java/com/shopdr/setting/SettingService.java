package com.shopdr.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopdr.common.entity.Setting;
import com.shopdr.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired private SettingRepository repo;
	
	public EmailSettingBag getEmailSetting () {
	 List<Setting> settings =	repo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		
		return new EmailSettingBag(settings);
	 
	}
	
}
