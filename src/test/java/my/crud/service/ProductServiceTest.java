package my.crud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import my.crud.domain.Product;
import my.crud.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    ProductService productService;
    
    Clock clock = Clock.fixed(Instant.MIN, ZoneId.systemDefault());

    @Mock
    ProductRepository productRepository;
    
    @Captor 
    ArgumentCaptor<Product> productCaptor;
    
    @BeforeEach
    public void before() {
        productService = new ProductServiceImpl(clock, productRepository);
    }
    
    @Test
    public void shouldCallFindAllProductsFromRepository() {
        final List<Product> expected = Lists.list(null, null);
        Mockito.when(productRepository.findAllActive()).thenReturn(expected);
        
        final List<Product> actual = productService.getAllActive();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldCallRepositorySaveWithNullIdAndCurrentCreationDate() {
        final Timestamp expectedClockTimestamp = Timestamp.from(clock.instant());
        
        final Product given = Product.builder()
                .id(Long.MIN_VALUE)
                .name("name")
                .price(Long.MAX_VALUE)
                .created(Timestamp.from(Instant.now()))
                .build();
        
        final Product expected = given.toBuilder()
                .id(null)
                .created(expectedClockTimestamp)
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(expected);
        
        final Product actual = productService.create(given);
        
        assertEquals(expected, actual);
        
        verify(productRepository).save(productCaptor.capture());
        assertNull(productCaptor.getValue().getId(), "Product must have <null> id on create when passed to save.");
        assertEquals(expectedClockTimestamp, productCaptor.getValue().getCreated(),
                "CreationDate Timestamp must be generated from current clock");
    }

    @Test
    public void shouldUpdateExistingProduct() {
        final Product given = Product.builder().id(1L).build();
        
        when(productRepository.findById(given.getId())).thenReturn(Optional.of(given));
        when(productRepository.save(given)).thenReturn(given);
        
        final Optional<Product> actual = productService.update(given);
        
        assertTrue(actual.isPresent());
        assertEquals(given, actual.get());
    }
    
    @Test
    public void shouldNotUpdateMissingProduct() {
        final Product given = Product.builder().id(1L).build();
        
        when(productRepository.findById(given.getId())).thenReturn(Optional.empty());
        
        final Optional<Product> actual = productService.update(given);
        
        assertFalse(actual.isPresent());
        verify(productRepository, never()).save(any());
    }

    @Test
    public void shouldCallRepositoryDelete() {
        final Product given = Product.builder().id(1L).build();
        when(productRepository.findById(given.getId())).thenReturn(Optional.of(given));
        
        productService.softDeleteById(given.getId());
        
        verify(productRepository).softDeleteById(given.getId(), Timestamp.from(clock.instant()));
    }
}
