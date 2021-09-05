package com.shopdr.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopdr.admin.FileUploadUtil;
import com.shopdr.admin.category.CategoryService;
import com.shopdr.common.entity.Brand;
import com.shopdr.common.entity.Category;


@Controller
public class BrandController {

	@Autowired
	private BrandService service;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		return listByPage(1, model, null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum")
	int pageNum, Model model,
	@Param("keyword") String keyword) {
		Page<Brand> page = service.listByPage(pageNum, keyword);
		List<Brand> listBrands = page.getContent();
		
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/brands");
		
		return "brands";
		
	}
	
	@GetMapping("brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAtributes) throws IOException {
		
		if(!multipartFile.isEmpty()) {
		String FileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
		brand.setLogo(FileName);
		
		Brand savedBrand = service.save(brand);
		String uploadDir = "../brand-logos/" + savedBrand.getId();
		
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.saveFile(uploadDir, FileName, multipartFile);
		
		}
		else {
			service.save(brand);
		}
		
		redirectAtributes.addFlashAttribute("message", "The brand has been saved successfully.");
		return "redirect:/brands";
		
		
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id, Model model,  RedirectAttributes redirectAttributes) {
		try {
		Brand brand = service.get(id);
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("brand", brand);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
		
		return "brand_form";
		}
		
		catch (BrandNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}
		
		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);
			
			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted succesfully");
		}
		catch (BrandNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/brands";
	}
}
