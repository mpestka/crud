package my.crud;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import my.crud.rest.dto.ProductDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:integration-tests.properties")
class ApplicationIT {
    
    private final String HOST = "http://localhost:";
    private final String URI_PRODUCTS = "/products";

    @LocalServerPort
    private int port;
    
    @Value("${server.servlet.context-path}")
    private String basePath;
    
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    
    private String getUri(String path) {
        return HOST + port + basePath + path;
    }
    
    /**
     * This is single integration test checking real life scenario in single use case
     */
    @Test
    void shouldCreateGetUpdateAndDeleteProductsInSingeUseCaseScenario() {
        final Long maxPrice = Long.MAX_VALUE;
        final String name1 = "product 1";
        final String name2 = "product 2";
        final String name3 = "product 3";
        final String name2Updated = "product 2 name updated";
        
        // === Create ===
        createProduct("product 1", 1L);
        createProduct("product 2", 2L);
        createProduct("product 3", maxPrice);

        // === Get ===
        final List<ProductDto> createdList = getAll();
        assertAll("Get created products",
            () -> assertEquals(3, createdList.size(), "Should have added exactly 3 products by now"),
            () -> assertThat(createdList).extracting("name").containsExactlyInAnyOrder(name1, name2, name3),
            () -> assertThat(createdList).extracting("price").containsExactlyInAnyOrder(1L, 2L, maxPrice) 
        );
        
        // === Update ===
        ProductDto updateProduct = createdList.stream()
                                                .filter(el -> name2.equals(el.getName()))
                                                .findFirst()
                                                .get();
        updateProduct.setName(name2Updated);
        updateProduct.setPrice(maxPrice - 1);
        HttpStatus statusCode = update(updateProduct);
        assertEquals(HttpStatus.OK, statusCode, "Update response status must be OK for existing product"); 
        
        // === Get ===
        final List<ProductDto> updatedList = getAll();
        assertAll("Get updated products",
            () -> assertEquals(3, updatedList.size(), "Should have added exactly 3 products"),
            () -> assertThat(updatedList).extracting("name").containsExactlyInAnyOrder(name1, name2Updated, name3),
            () -> assertThat(updatedList).extracting("price").containsExactlyInAnyOrder(1L, maxPrice, maxPrice - 1) 
        );
        
        // === Delete ===:
        statusCode = delete(updatedList.get(0).getId());
        assertEquals(HttpStatus.OK, statusCode, "Delete response status must be OK for existing product");
        
        // === Get ===
        final List<ProductDto> deletedList = getAll();
        assertAll("Get deleted products",
            () -> assertEquals(2, deletedList.size(), "Should have added exactly 2 products"),
            () -> assertThat(deletedList).containsExactlyInAnyOrder(updatedList.get(1), updatedList.get(2))
        );
        
        // Not found on updating / deleting already deleted product:
        assertAll("not existing products",            
            () -> assertEquals(
                    HttpStatus.NOT_FOUND, update(updatedList.get(0)), "Should be alredy deleted"),
            () -> assertEquals(
                    HttpStatus.NOT_FOUND, delete(updatedList.get(0).getId()), "Should be alredy deleted")
        );
    }
    
    @Test
    void shouldReturnBadRequestForNullProperties() {
        ResponseEntity<ProductDto> response = postProduct(new ProductDto());
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "expected bad request for null properties");
    }
    
    @Test
    void shouldReturnBadRequestWhenTryingToUpdateProductWithoutId() {
        HttpStatus status = update(new ProductDto());
        
        assertEquals(HttpStatus.BAD_REQUEST, status, "expected bad request for update with no id");
    }
    
    private ResponseEntity<ProductDto> postProduct(ProductDto product) {
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                getUri(URI_PRODUCTS), 
                HttpMethod.POST, 
                new HttpEntity<>(product, headers), 
                ProductDto.class);
        return response;
    }
    
    private void createProduct(String name, Long price) {
        ProductDto product = new ProductDto();
        product.setName(name);
        product.setPrice(price);

        ResponseEntity<ProductDto> response = postProduct(product);
        
        assertAll("create product",
            () -> assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response status must be CREATED"),
            () -> {
                String actualLocation = response.getHeaders().get(HttpHeaders.LOCATION).get(0);
                assertTrue(
                        actualLocation.startsWith(getUri(URI_PRODUCTS) + '/'), "Location must start with base uri");  
            }
        ); 
    }
    
    private List<ProductDto> getAll() {
        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                        getUri(URI_PRODUCTS),
                        HttpMethod.GET, 
                        null, 
                        new ParameterizedTypeReference<List<ProductDto>>() {
                    });

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Products list response status must be OK");   
        
        return response.getBody();       
    }
    
    private HttpStatus update(ProductDto product) {
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                getUri(URI_PRODUCTS) + '/' + product.getId(), 
                HttpMethod.PUT, 
                new HttpEntity<>(product, headers), 
                ProductDto.class);
        
        return response.getStatusCode();
    }

    private HttpStatus delete(Long id) {
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                getUri(URI_PRODUCTS) + '/' + id, 
                HttpMethod.DELETE, 
                null, 
                ProductDto.class);
        
        return response.getStatusCode();
    }
}
