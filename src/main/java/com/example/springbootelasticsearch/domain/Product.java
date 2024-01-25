package com.example.springbootelasticsearch.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Getter
@Document(indexName = "products")
public class Product {

    @Id
    private String id;

    private String name;

    @Field(type = FieldType.Keyword)
    private String category;

    private BigDecimal price;

    private Boolean inStock;
}
