package com.example.catalog.repository;

import com.example.catalog.dto.ProductSearchRequest;
import com.example.catalog.model.Product;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> withFilters(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getSearch())) {
                String normalizedSearch = containsPattern(request.getSearch());
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("referencia")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("empresa")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("categoria")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("origenPdf")), normalizedSearch)
                ));
            }
            addLowerBound(predicates, criteriaBuilder, root.get("precio"), request.getMinPrice());
            addUpperBound(predicates, criteriaBuilder, root.get("precio"), request.getMaxPrice());
            if (StringUtils.hasText(request.getCategoria())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("categoria"), request.getCategoria()));
            }
            if (StringUtils.hasText(request.getEmpresa())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("empresa"), request.getEmpresa()));
            }
            if (StringUtils.hasText(request.getColor())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("color"), request.getColor()));
            }
            if (StringUtils.hasText(request.getOrigenPdf())) {
                predicates.add(likeIgnoreCase(criteriaBuilder, root.get("origenPdf"), request.getOrigenPdf()));
            }
            addLowerBound(predicates, criteriaBuilder, root.get("ancho"), request.getMinAncho());
            addUpperBound(predicates, criteriaBuilder, root.get("ancho"), request.getMaxAncho());
            addLowerBound(predicates, criteriaBuilder, root.get("largo"), request.getMinLargo());
            addUpperBound(predicates, criteriaBuilder, root.get("largo"), request.getMaxLargo());
            addLowerBound(predicates, criteriaBuilder, root.get("peso"), request.getMinPeso());
            addUpperBound(predicates, criteriaBuilder, root.get("peso"), request.getMaxPeso());
            addLowerBound(predicates, criteriaBuilder, root.get("volumen"), request.getMinVolumen());
            addUpperBound(predicates, criteriaBuilder, root.get("volumen"), request.getMaxVolumen());

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate likeIgnoreCase(
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            jakarta.persistence.criteria.Path<String> path,
            String value
    ) {
        return criteriaBuilder.like(criteriaBuilder.lower(path), containsPattern(value));
    }

    private static String containsPattern(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }

    private static <T extends Comparable<? super T>> void addLowerBound(
            List<Predicate> predicates,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            jakarta.persistence.criteria.Path<T> path,
            T value
    ) {
        if (value != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
        }
    }

    private static <T extends Comparable<? super T>> void addUpperBound(
            List<Predicate> predicates,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            jakarta.persistence.criteria.Path<T> path,
            T value
    ) {
        if (value != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
        }
    }
}
