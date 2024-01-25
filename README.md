## Elasticsearch + Springboot crud api with Elastic Query 

### Configuration For Connecting Springboot with Elasticsearch

#### 1. AbstractElasticsearchConfiguration.java
```aidl
public abstract class AbstractElasticsearchConfiguration extends ElasticsearchConfigurationSupport {

    @Bean
    public abstract RestHighLevelClient elasticsearchClient();

    @Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
    public ElasticsearchOperations elasticsearchOperations(final ElasticsearchConverter elasticsearchConverter,
                                                           final RestHighLevelClient elasticsearchClient) {

        ElasticsearchRestTemplate template = new ElasticsearchRestTemplate(elasticsearchClient, elasticsearchConverter);
        template.setRefreshPolicy(refreshPolicy());

        return template;
    }
}
```
#### 2. ElasticsearchConfiguration.java
```aidl
@Configuration
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

    private static final String COLON = ":";

    @Value("${spring.data.elasticsearch.host}")
    private String host;

    @Value("${spring.data.elasticsearch.port}")
    private int port;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(host + COLON + port)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
```

### Elasticsearch Settings
```aidl
docker run --name es712 -p 9201:9200 -p 9301:9300 \
         -e "discovery.type=single-node" \ 
         -e "cluster.name=${cluster_name}" \ 
         -e "node.name=${node_name}" docker.elastic.co/elasticsearch/elasticsearch:7.12.1
```

### How to check all data in a Product Index

#### Request 
<img width="600" alt="스크린샷 2024-01-25 오후 10 11 35" src="https://github.com/daily1313/spring-data-elasticsearch/assets/88074556/9ad32e76-1b26-4df9-b06d-24c58e344407">

#### Response
```
{
    "took": 9,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 10,
            "relation": "eq"
        },
        "max_score": 1.0,
        "hits": [
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "1",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "1",
                    "name": "물품1",
                    "category": "category1",
                    "price": 50000.0,
                    "inStock": true
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "2",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "2",
                    "name": "물품2",
                    "category": "category2",
                    "price": 20000.0,
                    "inStock": false
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "3",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "3",
                    "name": "물품3",
                    "category": "category3",
                    "price": 30000.0,
                    "inStock": true
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "4",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "4",
                    "name": "물품4",
                    "category": "category4",
                    "price": 10000.0,
                    "inStock": false
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "5",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "5",
                    "name": "물품5",
                    "category": "category5",
                    "price": 40000.0,
                    "inStock": true
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "6",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "6",
                    "name": "물품6",
                    "category": "category6",
                    "price": 25000.0,
                    "inStock": false
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "7",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "7",
                    "name": "물품7",
                    "category": "category7",
                    "price": 35000.0,
                    "inStock": true
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "8",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "8",
                    "name": "물품8",
                    "category": "category8",
                    "price": 60000.0,
                    "inStock": false
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "9",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "9",
                    "name": "물품9",
                    "category": "category9",
                    "price": 45000.0,
                    "inStock": true
                }
            },
            {
                "_index": "products",
                "_type": "_doc",
                "_id": "10",
                "_score": 1.0,
                "_source": {
                    "_class": "com.example.springbootelasticsearch.domain.Product",
                    "id": "10",
                    "name": "물품10",
                    "category": "category10",
                    "price": 15000.0,
                    "inStock": false
                }
            }
        ]
    }
}
```

### API List
<img width="280" alt="스크린샷 2024-01-25 오후 9 37 58" src="https://github.com/daily1313/spring-data-elasticsearch/assets/88074556/cbc444e4-df17-43d3-815d-d13e5c4599fc">

### Elasticsearch UI 
<img width="600" alt="스크린샷 2024-01-25 오후 9 37 37" src="https://github.com/daily1313/spring-data-elasticsearch/assets/88074556/0d273045-07c2-4926-8840-b0f6ae72c2c9">