package com.sl.ms.inventorymanagement.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sl.ms.inventorymanagement.controller.ProductController;
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
	private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

	
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
		SlProduct product = new SlProduct();
		
		product=  productRepo.findById(id).get();
		
		return product;
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
		
		List<SlProduct> lstProducts = readFile(file);
		productRepo.saveAll(lstProducts);
		invRepo.save(new SlInv(LocalDate.now(),new ObjectMapper().writeValueAsString(lstProducts)));
		
		return "File data uploaded successfully..";
		
	}
	
	public List<SlProduct> readFile(MultipartFile file) throws FileNotFoundException {
		List<SlProduct> list = new ArrayList<>();

		try {

			// StoreItems storeItems = StoreItems.getInstance();
			Resource resource = file.getResource();
			FileReader reader = null;
			InputStream input = resource.getInputStream();

			BufferedReader readFile = new BufferedReader(new InputStreamReader(input));

			readFile.lines().skip(1).forEach(line -> {
				SlProduct product = new SlProduct();
				String[] cols = line.split(",");
				System.out.println("cols::"+cols);
				product.setProductId(Integer.parseInt(cols[0]));
				product.setName(cols[1]);
				product.setPrice(Double.parseDouble(cols[2]));
				product.setQuantity(Integer.parseInt(cols[3]));
				list.add(product);
			});

			readFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	

}
