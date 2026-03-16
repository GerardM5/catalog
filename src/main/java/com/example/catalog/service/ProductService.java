package com.example.catalog.service;

import com.example.catalog.dto.CreateProductRequest;
import com.example.catalog.dto.PageMetadataResponse;
import com.example.catalog.dto.ProductExportFormat;
import com.example.catalog.dto.ProductPageResponse;
import com.example.catalog.dto.ProductResponse;
import com.example.catalog.dto.ProductSearchRequest;
import com.example.catalog.exception.ConflictException;
import com.example.catalog.exception.NotFoundException;
import com.example.catalog.mapper.ProductMapper;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.example.catalog.repository.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductExportWriter productExportWriter;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            ProductExportWriter productExportWriter
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productExportWriter = productExportWriter;
    }

    @Transactional(readOnly = true)
    public ProductPageResponse search(ProductSearchRequest request, Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(ProductSpecifications.withFilters(request), pageable);

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(productsPage.getContent().stream()
                .map(productMapper::toResponse)
                .toList());
        response.setPagination(toPageMetadata(productsPage));
        return response;
    }

    @Transactional(readOnly = true)
    public ProductExportFile export(ProductSearchRequest request, Pageable pageable, ProductExportFormat format) {
        ProductPageResponse response = search(request, pageable);
        return productExportWriter.write(response.getContent(), format);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsById(request.getId())) {
            throw new ConflictException("A product with id " + request.getId() + " already exists");
        }

        Product savedProduct = productRepository.save(productMapper.toEntity(request));
        return productMapper.toResponse(savedProduct);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " was not found"));
    }

    private PageMetadataResponse toPageMetadata(Page<Product> productsPage) {
        PageMetadataResponse metadata = new PageMetadataResponse();
        metadata.setPage(productsPage.getNumber());
        metadata.setSize(productsPage.getSize());
        metadata.setTotalElements(productsPage.getTotalElements());
        metadata.setTotalPages(productsPage.getTotalPages());
        metadata.setNumberOfElements(productsPage.getNumberOfElements());
        metadata.setFirst(productsPage.isFirst());
        metadata.setLast(productsPage.isLast());
        metadata.setEmpty(productsPage.isEmpty());
        return metadata;
    }
}
