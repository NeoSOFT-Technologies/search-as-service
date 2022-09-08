package com.neosoft.app.infrastructure.adaptor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.domain.port.spi.ProductPersistencePort;
import com.neosoft.app.infrastructure.entity.Product;
import com.neosoft.app.infrastructure.entity.mapper.ProductEntityMapper;
import com.neosoft.app.infrastructure.repository.ProductRepository;


@Service
@Transactional
public class ProductJPAAdapter implements ProductPersistencePort {

	@Autowired
	private final ProductRepository productRepository;
	
	@Autowired
	private final ProductEntityMapper productEntityMapper;
	
	public ProductJPAAdapter(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
		this.productRepository = productRepository;
		this.productEntityMapper = productEntityMapper;
	}

	@Override
	public List<Product> getAll() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> getOne(int productId) {
		return productRepository.findById(productId);
	}

	@Override
	public boolean isExistsById(int productId) {
		return productRepository.existsById(productId);
	}

	@Override
	public Optional<Product> addOne(ProductDTO productDTO) {
		return Optional.ofNullable(productRepository.save(productEntityMapper.dtoToEntity(productDTO)));
	}

	@Override
	public void removeOne(int productId) {
		productRepository.deleteById(productId);
	}
	
}
