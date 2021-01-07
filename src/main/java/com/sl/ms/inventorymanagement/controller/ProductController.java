package com.sl.ms.inventorymanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sl.ms.inventorymanagement.model.AuthenticationRequest;
import com.sl.ms.inventorymanagement.model.AuthenticationResponse;
import com.sl.ms.inventorymanagement.model.Product;
import com.sl.ms.inventorymanagement.model.SlProduct;
import com.sl.ms.inventorymanagement.service.JwtUtil;
import com.sl.ms.inventorymanagement.service.MyUserDetailsService;
import com.sl.ms.inventorymanagement.service.ProductService;

@RestController
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private MyUserDetailsService userDetailsService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AuthenticationManager authManager;

	// Get all the products in the system
	@RequestMapping("/products")
	private List<SlProduct> getAllProducts() {
		return productService.getAllProducts();
	}

	// create multiple products in a single request
	@RequestMapping(method = RequestMethod.POST, value = "/products")
	private String createProducts(@RequestBody List<SlProduct> products) {
		return productService.createProducts(products);
	}

	// Get a product details for the given id
	@RequestMapping("/products/{id}")
	private SlProduct getAProduct(@PathVariable int id) {
		return productService.getAProduct(id);
	}

	// create a new product
	@RequestMapping(method = RequestMethod.POST, value = "/products/{id}")
	private String getAProduct(@RequestBody SlProduct product) {
		return productService.createAProduct(product);
	}

	// Update the product with the given details
	@RequestMapping(method = RequestMethod.PUT, value = "/products/{id}")
	private String updateProduct(@RequestBody SlProduct product) {
		 productService.createAProduct(product);
	return "Product updated successfully";
	}

	// Delete the product for the given id
	@RequestMapping(method = RequestMethod.DELETE, value = "/products/{id}")
	private String deleteProduct(@PathVariable int id) {
		return productService.deleteAProduct(id);
	}

	// Delete the product for the given id
	@RequestMapping("/supportedproducts")
	private List<Product> getUniqueProducts() {
		List<SlProduct> listProducts = new ArrayList<>();
		listProducts = productService.getAllProducts();
		List<Product> lstProd = new ArrayList<Product>();
		List<SlProduct> list = listProducts.stream().filter(distinctByKeys(SlProduct::getProductId))
				.collect(Collectors.toList());
		list.forEach(product -> {
			Product p = new Product();
			p.setProduct_id(product.getProductId());
			p.setProduct_name(product.getName());
			lstProd.add(p);
		});
		return lstProd;
	}

	private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
		final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

		return t -> {
			final List<?> keys = Arrays.stream(keyExtractors).map(ke -> ke.apply(t)).collect(Collectors.toList());

			return seen.putIfAbsent(keys, Boolean.TRUE) == null;
		};
	}

	@RequestMapping(method = RequestMethod.POST, value = "/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest)
			throws Exception {
		try {

			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Bad user/password", e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUserName());
		final String jwt = jwtUtil.generateToken(userDetails);
		ResponseEntity<?> reponse = ResponseEntity.ok(new AuthenticationResponse(jwt));
		return reponse;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/file")
	public String handleFileUpload(@RequestParam("file") MultipartFile file)
			throws JsonParseException, JsonMappingException, IOException {
		System.out.println("file::" + file);
		productService.uploadFile(file);
		return "Successfully created the products with the file details";
	}
}
