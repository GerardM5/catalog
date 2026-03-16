package com.example.catalog.dto;

import java.util.List;

public class ProductPageResponse {

    private List<ProductResponse> content;
    private PageMetadataResponse pagination;

    public List<ProductResponse> getContent() {
        return content;
    }

    public void setContent(List<ProductResponse> content) {
        this.content = content;
    }

    public PageMetadataResponse getPagination() {
        return pagination;
    }

    public void setPagination(PageMetadataResponse pagination) {
        this.pagination = pagination;
    }
}
