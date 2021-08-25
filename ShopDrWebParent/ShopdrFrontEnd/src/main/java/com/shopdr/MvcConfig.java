package com.shopdr;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	
		
		
		String categoryImagesName = "../category-images";
		Path categoryImageDir = Paths.get(categoryImagesName);
		
		String categoryImagesPath = categoryImageDir.toFile().getAbsolutePath();	
				
		registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");
	
		
		String brandLogosDirName = "../brand-logos";
		Path brandLogosDir = Paths.get(brandLogosDirName);
		
		String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/brand-logos/**").addResourceLocations("file:/" + brandLogosPath + "/");
		
		String productImagesDirName = "../product-images";
		Path productImagesDir = Paths.get(productImagesDirName);
		
		String productImagesPath = productImagesDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/product-images/**").addResourceLocations("file:/" + productImagesPath + "/");
	}

}
