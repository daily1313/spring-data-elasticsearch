package com.example.springbootelasticsearch.repository;

import com.example.springbootelasticsearch.domain.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
