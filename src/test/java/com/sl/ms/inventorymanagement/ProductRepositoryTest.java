package com.sl.ms.inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sl.ms.inventorymanagement.model.SlProduct;
import com.sl.ms.inventorymanagement.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ProductRepositoryTest {
    
    @Autowired
    private ProductRepository productRepository;
    
    
    @Test
    public void whenFindById_thenReturnProduct() {
        // given
		SlProduct product = new SlProduct(1, "Rice", 50, 120);
		productRepository.save(product);

        // when
        Optional<SlProduct> found = productRepository.findById(product.getProductId());

        // then
        assertNotNull(found);
        assertEquals(found.get().getName(),"Rice");

    }
    
    

}
