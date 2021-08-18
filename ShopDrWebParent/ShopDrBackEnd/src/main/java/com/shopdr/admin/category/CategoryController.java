package com.shopdr.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopdr.admin.user.UserService;
import com.shopdr.common.entity.Category;



@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model) {
		
		return listByPage(1, null, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@Param("keyword") String keyword, Model model) {
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
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
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categoris/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			
			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted succesfully");
		}
		catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/categories";
	}
	
	
	
}
