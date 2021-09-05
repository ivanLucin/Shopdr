package com.shopdr.admin.product;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopdr.admin.FileUploadUtil;
import com.shopdr.admin.brand.BrandService;
import com.shopdr.admin.category.CategoryService;
import com.shopdr.admin.security.ShopdrUserDetails;
import com.shopdr.common.entity.Brand;
import com.shopdr.common.entity.Category;
import com.shopdr.common.entity.Product;
import com.shopdr.common.exception.ProductNotFoundException;




@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired 
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage (Model model) {
		
		return listByPage(1, model, null, 0);
	}
	
	@GetMapping("/products/new")
public String newProduct(Model model) {
		
		
		List<Brand> listBrands = brandService.listAll();
		
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		
		return "/Products/product_form";
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum")
	int pageNum, Model model,
	@Param("keyword") String keyword,
	@Param("categoryId") Integer categoryId) {
		
		Page<Product> page = productService.listByPage(pageNum, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("moduleURL", "/products");
		
		return "Products/products";
		
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipart,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopdrUserDetails loggedUser) throws IOException {
		
		if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if(loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			
			ra.addFlashAttribute("message", "The product has been saved successfully.");
			return "redirect:/products";
			}
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultipart, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
		
			
			Product savedProduct = productService.save(product);
			ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultipart, savedProduct);
			
			ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);
		
			ra.addFlashAttribute("message", "The product has been saved successfully.");
			return "redirect:/products";
	}
	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id,
			Model model,  RedirectAttributes redirectAttributes) {
		
		try {
			productService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;
			
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + "has been deleted");
			}
		catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());	
			}
		return "redirect:/products";
	}
	
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra, @AuthenticationPrincipal ShopdrUserDetails loggedUser) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			boolean isReadOnlySalesperson = false;
			
			if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if(loggedUser.hasRole("Salesperson")) {
					isReadOnlySalesperson = true;
				}
			}
				
			model.addAttribute("isReadOnlySalesperson", isReadOnlySalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			return "Products/product_form";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);			
			model.addAttribute("product", product);		
			
			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/products";
		}
	}	
	
}
