package com.shopdr.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopdr.admin.FileUploadUtil;
import com.shopdr.admin.user.UserNotFoundException;
import com.shopdr.common.entity.Category;
import com.shopdr.common.entity.Role;
import com.shopdr.common.entity.User;

import antlr.StringUtils;


@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories",listCategories);
		
		return "categories";
	}
	
	@GetMapping("categories/new")
	public String newCategory(Model model) {
		List<Category> listCategoriesInForm = service.listCategoriesUsedInForm();
		
		model.addAttribute("category", new Category());
		model.addAttribute("listCategoriesInForm", listCategoriesInForm);
		model.addAttribute("pageTitle", "Create New Category");
		return "category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAtributes) throws IOException {
		
		if(!multipartFile.isEmpty()) {
		String FileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(FileName);
		
		Category savedCategory = service.save(category);
		String uploadDir = "../category-images/" + savedCategory.getId();
		
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.saveFile(uploadDir, FileName, multipartFile);
		
		}
		else {
			service.save(category);
		}
		
		redirectAtributes.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
		
		
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model,  RedirectAttributes redirectAttributes) {
		try {
		Category category = service.get(id);
		List<Category> listCategoriesInForm = service.listCategoriesUsedInForm();
		
		model.addAttribute("category", category);
		model.addAttribute("listCategoriesInForm", listCategoriesInForm);
		model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
		
		return "category_form";
		}
		
		catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
		
		
	}
	
}
