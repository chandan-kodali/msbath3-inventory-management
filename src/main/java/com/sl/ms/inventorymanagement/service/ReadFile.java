package com.sl.ms.inventorymanagement.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sl.ms.inventorymanagement.model.SlProduct;
@Service
public class ReadFile {

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