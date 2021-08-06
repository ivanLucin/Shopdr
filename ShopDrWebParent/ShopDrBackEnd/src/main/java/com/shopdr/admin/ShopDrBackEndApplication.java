package com.shopdr.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shopdr.common.entity", "com.shopdr.admin.user"})
public class ShopDrBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopDrBackEndApplication.class, args);
	}

}
