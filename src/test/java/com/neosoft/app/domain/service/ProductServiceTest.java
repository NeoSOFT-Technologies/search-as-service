package com.neosoft.app.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.infrastructure.entity.Product;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(
        properties = {
           "schema-delete-record-file.testPath: src/test/resources/TableDeleteRecordTest.csv",
           "tenant-cache.tenant: tenantName"
        }
)
class ProductServiceTest {

	@InjectMocks
	ProductService manageTableService;

	Product createProductDTO = new Product();

	Product newProductDTO = new Product();

	Response responseDTO = new Response();

	@BeforeEach
	void setUp() {

	}
	
	
}