package com.sl.ms.inventorymanagement.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sl.ms.inventorymanagement.model.SlInv;
import com.sl.ms.inventorymanagement.model.SlProduct;
import com.sl.ms.inventorymanagement.repository.InvRepository;
import com.sl.ms.inventorymanagement.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private InvRepository invRepo;
	
	@Autowired
	private ReadFile readFile;
	public List<SlProduct> getAllProducts(){
		List<SlProduct> lstProducts = new ArrayList<SlProduct>();
		
		productRepo.findAll().forEach(lstProducts::add);
		return lstProducts;
	}

	public String createProducts(List<SlProduct> products) {
		products.forEach(product->productRepo.save(product));
		return "new products created successfully..";

	}

	public SlProduct getAProduct(int id) {
		return productRepo.findById(id).get();
	}

	public String createAProduct(SlProduct product) {
		productRepo.save(product);
		return "New product created successfully..";
	}

	public String deleteAProduct(int id) {
		SlProduct product = productRepo.findById(id).get();
		product.setQuantity(0);
		productRepo.save(product);
		return "Product deleted successfully..";
	}

	public String uploadFile(MultipartFile file) throws JsonParseException, JsonMappingException, IOException {
		
		List<SlProduct> lstProducts = readFile.readFile(file);
		productRepo.saveAll(lstProducts);
		invRepo.save(new SlInv(LocalDate.now(),new ObjectMapper().writeValueAsString(lstProducts)));
		return "File data uploaded successfully..";
		
	}

	

}
