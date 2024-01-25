package com.example.springbootelasticsearch.service;

import com.example.springbootelasticsearch.domain.Product;
import com.example.springbootelasticsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private static final String PRODUCT_INDEX = "products";

    private final ProductRepository productRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void save(final Product product) {
        try {
            productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bulkSave(final List<Product> products) {
        List<IndexQuery> queries = products.stream()
                .map(product -> new IndexQueryBuilder()
                        .withId(product.getId())
                        .withObject(product)
                        .build())
                .collect(Collectors.toList());

        elasticsearchRestTemplate.bulkIndex(queries, Product.class);
    }

    public Optional<Product> findById(final String id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public void deleteById(final String id) {
        productRepository.deleteById(id);
    }

    public List<Product> findByBoolQuery(final String category,
                                                 final BigDecimal minPrice,
                                                 final Boolean inStock) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("category", category))
                .should(QueryBuilders.rangeQuery("price").lt(minPrice))
                .should(QueryBuilders.matchQuery("inStock", inStock));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<Product> findByInStock(final boolean inStock) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("inStock", inStock);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQueryBuilder)
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<Product> findByPriceBetween(final BigDecimal minPrice, final BigDecimal maxPrice) {
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price")
                .gte(minPrice).lte(maxPrice);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(rangeQueryBuilder)
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<Product> findByName(final String name) {

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", name);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQueryBuilder)
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<String> findProductNamesByWildCardQuery(final String name) {
        String lowercaseNameKeyword = name.toLowerCase();

        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("name", lowercaseNameKeyword + "*");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(wildcardQueryBuilder)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(searchHit -> searchHit.getContent().getName())
                .collect(Collectors.toList());
    }

    public List<Product> findByFuzzySearch(final String name) {
        MatchQueryBuilder fuzzyQueryBuilder = QueryBuilders
                .matchQuery("name", name)
                .fuzziness(Fuzziness.AUTO);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", name)
                        .fuzziness(Fuzziness.ONE)
                        .prefixLength(3))
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public  List<Product> findByMultiMatchQuery(final String name) {
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery(name, "category", "name");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQueryBuilder)
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public Map<String, Double> calculateAveragePricePerCategory() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("by_category").field("category")
                        .subAggregation(AggregationBuilders.avg("avg_price").field("price")))
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        Terms byCategoryAggregation = searchHits.getAggregations().get("by_category");
        Map<String, Double> result = new HashMap<>();

        for(Terms.Bucket bucket: byCategoryAggregation.getBuckets()) {
            Avg avgPriceAggregation = bucket.getAggregations().get("avg_price");
            double avgPrice = avgPriceAggregation.getValue();
            result.put(bucket.getKeyAsString(), avgPrice);
        }

        return result;
    }

    public Map<String, Long> countProductsPerCategory() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("by_category").field("category"))
                .build();

        SearchHits<Product> searchHits = elasticsearchRestTemplate.search(searchQuery, Product.class);

        Terms byCategoryAggregation = searchHits.getAggregations().get("by_category");
        Map<String, Long> result = new HashMap<>();

        for(Terms.Bucket bucket: byCategoryAggregation.getBuckets()) {
            long docCount = bucket.getDocCount();
            result.put(bucket.getKeyAsString(), docCount);
        }

        return result;
    }
}
