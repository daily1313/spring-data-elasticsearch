package com.example.springbootelasticsearch.controller;

import com.example.springbootelasticsearch.domain.Product;
import com.example.springbootelasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RequestMapping("/api/products")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody final Product product) {
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkSave(@RequestBody final List<Product> products) {
        productService.bulkSave(products);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable final String id) {
        Optional<Product> product = productService.findById(id);

        if(!product.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        Iterable<Product> products = productService.findAll();
        List<Product> productList = new ArrayList<>();
        products.forEach(productList::add);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/name")
    public ResponseEntity<?> getProductsByName(@RequestParam(value = "name") final String name) {
        List<Product> products = productService.findByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/inStock")
    public ResponseEntity<?> getProductsByInStock(@RequestParam(value = "inStock") final boolean inStock) {
        List<Product> products = productService.findByInStock(inStock);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/query/range/search")
    public ResponseEntity<?> getProductsByPriceBetween(@RequestParam(value = "minPrice") final BigDecimal minPrice,
                                                       @RequestParam(value = "maxPrice") final BigDecimal maxPrice) {
        List<Product> products = productService.findByPriceBetween(minPrice, maxPrice);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/query/wildCard/search")
    public ResponseEntity<?> getProductNamesByWildCardQuery(@RequestParam(value = "name") final String name) {
        List<String> productsName = productService.findProductNamesByWildCardQuery(name);
        return new ResponseEntity<>(productsName, HttpStatus.OK);
    }

    @GetMapping("/query/fuzzy/search")
    public ResponseEntity<?> getProductsByFuzzySearch(@RequestParam(value = "name") final String name) {
        List<Product> products = productService.findByFuzzySearch(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/query/multiMatch/search")
    public ResponseEntity<?> getProductsByMultiMatchQuery(@RequestParam(value = "name") final String name) {
        List<Product> products = productService.findByMultiMatchQuery(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/query/bool/search")
    public ResponseEntity<?> getProductsByBoolQuery(@RequestParam(value = "category") final String category,
                                                    @RequestParam(value = "price") final BigDecimal price,
                                                    @RequestParam(value = "inStock") final Boolean inStock) {
        List<Product> products = productService.findByBoolQuery(category, price, inStock);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/aggregations/metrics/averagePrice")
    public ResponseEntity<?> getAveragePricePerCategory() {
        Map<String, Double> averagePricePerCategory = productService.calculateAveragePricePerCategory();
        return new ResponseEntity<>(averagePricePerCategory, HttpStatus.OK);
    }

    @GetMapping("/aggregations/metrics/docCount")
    public ResponseEntity<?> getProductCountPerCategory() {
        Map<String, Long> productCountPerCategory = productService.countProductsPerCategory();
        return new ResponseEntity<>(productCountPerCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") final String id) {
        Optional<Product> product = productService.findById(id);

        if(!product.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        productService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
