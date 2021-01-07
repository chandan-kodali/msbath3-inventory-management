package com.sl.ms.inventorymanagement.repository;

import org.springframework.data.repository.CrudRepository;

import com.sl.ms.inventorymanagement.model.SlProduct;

public interface ProductRepository extends CrudRepository<SlProduct, Integer>{

}
