package com.example.catalog.controller;

import com.example.catalog.dto.CreateProductRequest;
import com.example.catalog.dto.ProductExportFormat;
import com.example.catalog.dto.ProductPageResponse;
import com.example.catalog.dto.ProductResponse;
import com.example.catalog.dto.ProductSearchRequest;
import com.example.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Validated
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Catalog product operations")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(
            summary = "Search products",
            description = "Filters by any product field and supports pagination and sorting using page, size and sort query params."
    )
    public ResponseEntity<ProductPageResponse> search(
            @ParameterObject @ModelAttribute ProductSearchRequest request,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(productService.search(request, pageable));
    }

    @GetMapping("/export")
    @Operation(
            summary = "Export products",
            description = "Uses the same filters as product search. If page and size are omitted, exports all matching results; otherwise exports the requested page as csv or xlsx."
    )
    public ResponseEntity<ByteArrayResource> export(
            @ParameterObject @ModelAttribute ProductSearchRequest request,
            @ParameterObject Pageable pageable,
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "csv") String format
    ) {
        ProductExportFormat exportFormat = parseFormat(format);
        Pageable exportPageable = resolveExportPageable(pageable, httpServletRequest);
        var file = productService.export(request, exportPageable, exportFormat);
        ByteArrayResource resource = new ByteArrayResource(file.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getContent().length)
                .body(resource);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create product")
    @ApiResponse(responseCode = "409", description = "Product id already exists", content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + response.getId())).body(response);
    }

    private ProductExportFormat parseFormat(String format) {
        try {
            return ProductExportFormat.fromValue(format);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    private Pageable resolveExportPageable(Pageable pageable, HttpServletRequest request) {
        boolean hasPage = request.getParameter("page") != null;
        boolean hasSize = request.getParameter("size") != null;

        if (!hasPage && !hasSize) {
            Sort sort = pageable == null ? Sort.unsorted() : pageable.getSort();
            return Pageable.unpaged(sort);
        }

        return pageable;
    }
}
