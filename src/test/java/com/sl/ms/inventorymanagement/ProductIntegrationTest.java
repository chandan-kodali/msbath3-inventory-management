package com.sl.ms.inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sl.ms.inventorymanagement.controller.ProductController;
import com.sl.ms.inventorymanagement.model.AuthenticationRequest;
import com.sl.ms.inventorymanagement.model.SlProduct;
import com.sl.ms.inventorymanagement.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProductService service;

	private String token;
	
	public  MockHttpServletRequestBuilder myFactoryRequest;
	
	@BeforeEach
	public void init() throws Exception{
		
		AuthenticationRequest user = new AuthenticationRequest("inventory", "inventory");
		//System.out.println(mvc.perform(post("/authenticate").param("user name", "inventory").param("password", "password")).andReturn().getResponse().getContentAsString());
		String res = mvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andReturn().getResponse().getContentAsString();
		String tokken = "Bearer "+res.substring(8);
		token = tokken.substring(0, tokken.length()-2);
		
		
	}
	
	@Test
	public void givenProduct_whenGetProducts_thenReturnJsonArray() throws Exception {
		SlProduct product = new SlProduct(1, "Rice", 50, 120);
		
		given(service.getAProduct(1)).willReturn(product);
		
		mvc.perform(get("/products/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.productId").value("1"));
	}
	
	@Test
	public void checkJwtToken() {
		assertNotEquals(null, token);
	}
	
	@Test
	public void unauthorisedAccessTest() throws Exception{
		mvc.perform(get("/products/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(403));
	}
	
	@Test
	public void TestSaveAProduct() throws Exception {
		SlProduct product = new SlProduct(1, "Rice", 50, 120);
		String json = new ObjectMapper().writeValueAsString(product);
		mvc.perform(post("/products/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
	}
	
	@Test
	public void TestSavemultipleProducts() throws Exception {
		SlProduct product1 = new SlProduct(1, "Rice", 50, 120);
		SlProduct product2 = new SlProduct(2, "Banana", 60, 600);
List<SlProduct> lstProducts = new ArrayList<SlProduct>();
		String json = new ObjectMapper().writeValueAsString(lstProducts);
		mvc.perform(post("/products").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
	}
	
	@Test
	public void TestToGetAllProducts() throws Exception {
		
		mvc.perform(get("/products").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
	}
	
	@Test
	public void TestToGetUniquelProducts() throws Exception {
		
		mvc.perform(get("/supportedproducts").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
	}
	
	@Test
	public void TestToupdateProduct() throws Exception {
		SlProduct product1 = new SlProduct(1, "Rice", 50, 120);
		String json = new ObjectMapper().writeValueAsString(product1);
		mvc.perform(put("/products/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
				
	}

	@Test
	public void TestTodeleteProduct() throws Exception {
		
		mvc.perform(delete("/products/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
	}
}
