package com.shopdr.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.mysql.cj.x.protobuf.Mysqlx.Error.Severity;
import com.shopdr.common.entity.Brand;
import com.shopdr.common.entity.Category;
import com.shopdr.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

	@Autowired
	private productRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		
		Brand brand = entityManager.find(Brand.class, 13);
		Category category = entityManager.find(Category.class, 20);
		
		Product product = new Product();
		product.setName("Samsung galaxy 21");
		product.setAlias("samsung g 21");
		product.setShortDescription("A good phone");
		product.setFullDescription("A good phone ofc");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(2000);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product saveProduct = repo.save(product);
		
		assertThat(saveProduct).isNotNull();
		assertThat(saveProduct.getId()).isGreaterThan(0);
		

	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> iterProducts = repo.findAll();
		
		iterProducts.forEach(System.out::println);
	}
	
	@Test
	public void getProdById() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		System.out.println(product);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateAProduct() {
		int id = 1;
		Product product = repo.findById(id).get();
		product.setPrice(1000);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(1000);
	}
	
	@Test
	public void deleteProducttest () {
		int id = 2;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	
}
