package com.shopdr.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.shopdr.admin.security.ShopdrUserDetails;
import com.shopdr.admin.setting.SettingService;
import com.shopdr.common.entity.Order;


@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "customer", "asc", null);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(Model model,
			@PathVariable(name = "pageNum") int pageNum,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {

		Page<Order> page = orderService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		
		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/orders");
		
		
		return "orders/orders";
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra) {
		try {
			Order order = orderService.get(id);			
			model.addAttribute("order", order);
			return "orders/order_details_modal";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);;
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
}
