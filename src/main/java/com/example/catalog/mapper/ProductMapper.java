package com.example.catalog.mapper;

import com.example.catalog.dto.CreateProductRequest;
import com.example.catalog.dto.ProductResponse;
import com.example.catalog.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);
}
