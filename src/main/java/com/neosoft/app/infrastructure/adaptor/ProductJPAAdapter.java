package com.neosoft.app.infrastructure.adaptor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.domain.port.spi.ProductPersistencePort;
import com.neosoft.app.infrastructure.entity.Product;
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
	public List<ProductDTO> getAll() {
		return productEntityMapper.entitiesToDTOs(productRepository.findAll());
	}

	@Override
	public Optional<ProductDTO> getOne(int productId) {
		
		Optional<Product> getProduct = productRepository.findById(productId);
		
		if(getProduct.isPresent()) {
			return Optional.ofNullable(productEntityMapper.entityToDTO(getProduct.get()));
		} else
			return Optional.ofNullable(null);
		
	}

	@Override
	public boolean isExistsById(int productId) {
		return productRepository.existsById(productId);
	}

	@Override
	public Optional<ProductDTO> addOne(ProductDTO productDTO) {
		return Optional.ofNullable(
				productEntityMapper.entityToDTO(
						productRepository.save(productEntityMapper.dtoToEntity(productDTO))));
	}

	@Override
	public void removeOne(int productId) {
		productRepository.deleteById(productId);
	}
	
}
