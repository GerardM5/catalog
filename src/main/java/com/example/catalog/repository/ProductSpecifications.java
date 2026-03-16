package com.example.catalog.repository;

import com.example.catalog.dto.ProductSearchRequest;
import com.example.catalog.model.Product;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> withFilters(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), request.getId()));
            }
            if (StringUtils.hasText(request.getReferencia())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("referencia"), request.getReferencia()));
            }
            if (StringUtils.hasText(request.getNombre())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("nombre"), request.getNombre()));
            }
            if (StringUtils.hasText(request.getDescripcion())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("descripcion"), request.getDescripcion()));
            }
            if (request.getPeso() != null) {
                predicates.add(criteriaBuilder.equal(root.get("peso"), request.getPeso()));
            }
            if (request.getVolumen() != null) {
                predicates.add(criteriaBuilder.equal(root.get("volumen"), request.getVolumen()));
            }
            if (StringUtils.hasText(request.getColor())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("color"), request.getColor()));
            }
            if (request.getAncho() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ancho"), request.getAncho()));
            }
            if (request.getLargo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("largo"), request.getLargo()));
            }
            if (request.getAlto() != null) {
                predicates.add(criteriaBuilder.equal(root.get("alto"), request.getAlto()));
            }
            if (request.getPrecio() != null) {
                predicates.add(criteriaBuilder.equal(root.get("precio"), request.getPrecio()));
            }
            if (StringUtils.hasText(request.getEmpresa())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("empresa"), request.getEmpresa()));
            }
            if (StringUtils.hasText(request.getCategoria())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("categoria"), request.getCategoria()));
            }
            if (StringUtils.hasText(request.getOrigenPdf())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("origenPdf"), request.getOrigenPdf()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate likeIgnoreCase(
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            jakarta.persistence.criteria.Path<String> path,
            String value
    ) {
        return criteriaBuilder.like(criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%");
    }
}
