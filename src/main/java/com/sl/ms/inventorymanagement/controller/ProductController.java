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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
	private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

	// Get all the products in the system
	@RequestMapping("/products")
	public List<SlProduct> getAllProducts() {
		LOG.info("Path:/products   RequestMethod:GET classMethod:getAllProducts --- entering");
		List<SlProduct> products = productService.getAllProducts();
		LOG.info("Path:/products   RequestMethod:GET classMethod:getAllProducts --- exiting");

		return products;
	}

	// create multiple products in a single request
	@RequestMapping(method = RequestMethod.POST, value = "/products")
	public String createProducts(@RequestBody List<SlProduct> products) {
		LOG.info("Path:/products   RequestMethod:POST classMethod:createProducts --- entering");
		String msg = productService.createProducts(products);
		LOG.info("Path:/products   RequestMethod:POST classMethod:createProducts --- exiting");
		return msg;
	}

	// Get a product details for the given id
	@RequestMapping("/products/{id}")
	public SlProduct getAProduct(@PathVariable int id) {
		LOG.info("Path:/products/" + id + "   RequestMethod:GET classMethod:getAProduct --- entering");
		SlProduct product = productService.getAProduct(id);
		LOG.info("Path:/products/" + id + "   RequestMethod:GET classMethod:getAProduct --- exiting");
		return product;
	}

	// create a new product
	@RequestMapping(method = RequestMethod.POST, value = "/products/{id}")
	public String createAProduct(@RequestBody SlProduct product) {
		LOG.info("Path:/products/{id}   RequestMethod:POST classMethod:createAProduct --- entering");
		String msg = productService.createAProduct(product);
		LOG.info("Path:/products/{id}   RequestMethod:POST classMethod:createAProduct --- exiting");
		return msg;
	}

	// Update the product with the given details
	@RequestMapping(method = RequestMethod.PUT, value = "/products/{id}")
	public String updateProduct(@RequestBody SlProduct product) {
		LOG.info("Path:/products/{id}   RequestMethod:PUT classMethod:updateProduct --- entering");
		productService.createAProduct(product);
		LOG.info("Path:/products/{id}   RequestMethod:PUT classMethod:updateProduct --- exiting");

		return "Product updated successfully";
	}

	// Delete the product for the given id
	@RequestMapping(method = RequestMethod.DELETE, value = "/products/{id}")
	public String deleteProduct(@PathVariable int id) {
		LOG.info("Path:/products/{id}   RequestMethod:DELETE classMethod:deleteProduct --- entering");
		String msg = productService.deleteAProduct(id);

		LOG.info("Path:/products/{id}   RequestMethod:DELETE classMethod:deleteProduct --- exiting");
		return msg;
	}

	@RequestMapping("/supportedproducts")
	@Cacheable("supportedproducts")
	public List<Product> getUniqueProducts() throws InterruptedException {
		LOG.info("Path:/supportedproducts   RequestMethod:GET classMethod:getUniqueProducts --- entering");
		Thread.sleep(1000 * 3);
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
		LOG.info("Path:/supportedproducts   RequestMethod:GET classMethod:getUniqueProducts --- exiting");

		return lstProd;
	}

	public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
		final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

		return t -> {
			final List<?> keys = Arrays.stream(keyExtractors).map(ke -> ke.apply(t)).collect(Collectors.toList());

			return seen.putIfAbsent(keys, Boolean.TRUE) == null;
		};
	}

	@RequestMapping(method = RequestMethod.POST, value = "/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest)
			throws Exception {
		LOG.info("Path:/authenticate   RequestMethod:POST classMethod:createAuthenticationToken --- entering");

		try {

			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			LOG.error("Path:/authenticate   RequestMethod:POST classMethod:createAuthenticationToken --- error:"
					+ e.getMessage());
			throw new Exception("Bad user/password", e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUserName());
		final String jwt = jwtUtil.generateToken(userDetails);
		ResponseEntity<?> reponse = ResponseEntity.ok(new AuthenticationResponse(jwt));
		LOG.info("Path:/authenticate   RequestMethod:POST classMethod:createAuthenticationToken --- exiting");

		return reponse;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/file")
	public String handleFileUpload(@RequestParam("file") MultipartFile file)
			throws JsonParseException, JsonMappingException, IOException {
		LOG.info("Path:/file   RequestMethod:POST classMethod:handleFileUpload --- entering");
		productService.uploadFile(file);
		LOG.info("Path:/file   RequestMethod:POST classMethod:handleFileUpload --- entering");

		return "Successfully created the products with the file details";
	}
}
