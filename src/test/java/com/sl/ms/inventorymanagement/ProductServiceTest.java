package com.sl.ms.inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.sl.ms.inventorymanagement.exception.GLobalExceptionHandler;
import com.sl.ms.inventorymanagement.model.SlProduct;
import com.sl.ms.inventorymanagement.repository.InvRepository;
import com.sl.ms.inventorymanagement.repository.ProductRepository;
import com.sl.ms.inventorymanagement.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ProductServiceTest {

	
	
	@InjectMocks
	private ProductService productService;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private InvRepository invRepo;

	@InjectMocks
	private GLobalExceptionHandler globalExceptions;	
	
	
	@BeforeEach
	public void setUp() {
		SlProduct product = new SlProduct(1, "Rice", 50, 120);
		List<SlProduct> lstProducts = new ArrayList<SlProduct>();
		

		Mockito.when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

		Mockito.when(productRepository.findAll()).thenReturn(lstProducts);

	}

	@Test
	public void whenValidId_thenProductShouldBeFound() {
		SlProduct found = productService.getAProduct(1);
		assertEquals(found.getName(), "Rice");
	}

	@Test
	public void TestForAlltheProducts() {
		List<SlProduct> found = productService.getAllProducts();
		System.out.println("chandan:: found " + found.size());

		assertNotEquals(found, null);
	}

	@Test
	public void TestForDeleteAProduct() {
		productService.deleteAProduct(1);
	}
	@Test
	public void saveProductDetailsTest(){
	    //given
		SlProduct product = new SlProduct(1, "Banana", 60, 3000);
	    //when
		productService.createAProduct(product);
	    //then
	    verify(productRepository, times(1)).save(product);
	}
	
	@Test
	public void saveListOfProductsTest(){
	    //given
		SlProduct product1 = new SlProduct(1, "Banana", 60, 400);
		SlProduct product2 = new SlProduct(2, "Banana", 30, 600);
List<SlProduct> lstProducts = new ArrayList<SlProduct>(); 
lstProducts.add(product1);
lstProducts.add(product2);
	    //when
		productService.createProducts(lstProducts);
	    //then
		for(SlProduct p : lstProducts) {
			verify(productRepository, times(1)).save(p);
		}
	    
	}
	@Test
	public void updateAProductTest(){
	    //given
		SlProduct product1 = new SlProduct(1, "Orange", 60, 400);
	    //when
		productService.createAProduct(product1);
	    //then
		verify(productRepository, times(1)).save(product1);

	    
	}
	
	@Test
	void addInventoryFileTest() throws Exception {
		String   text = "productId,Name,Price,Quantity \n1,item1,120,10";
		File     file = new File("Inventory.csv");
		Writer writer = new BufferedWriter(new FileWriter(file));
		writer.write(text);
		writer.close();
		FileInputStream input = new FileInputStream(file);
		MockMultipartFile file1 = new MockMultipartFile("file",input);
		productService.uploadFile(file1);
		SlProduct found = productService.getAProduct(1);
		assertNotEquals(found.getName(), null);
		//List<SlProduct> lstProducts = productService.readFile(file1);
		//verify(productRepository,times(1)).save(lstProducts.get(0));
		
	}
	
	
	
}
