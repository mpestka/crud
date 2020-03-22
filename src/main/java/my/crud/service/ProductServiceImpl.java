
package my.crud.service;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import my.crud.domain.Product;
import my.crud.repository.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private Clock clock;
    private ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(Clock clock, ProductRepository productRepository) {
        this.clock = clock;
        this.productRepository = productRepository;
    }

    public List<Product> getAllActive() {
        return productRepository.findAllActive();
    }
    
    public Product create(Product product) {
        final Product created = product.toBuilder()
                .id(null)
                .deleted(null)
                .created(now())
                .build();
        return productRepository.save(created);
    }
    
    public Optional<Product> update(Product product) {
        Assert.notNull(product.getId(), "Product id required");
        Assert.isNull(product.getDeleted(), "To delete product use delete api");
                
        final Optional<Product> dbProduct = productRepository.findById(product.getId());
        
        if (!dbProduct.isPresent() || dbProduct.get().getDeleted() != null) {
            return Optional.empty();
        }
                
        return Optional.of(productRepository.save(product));
    }
    
    public boolean softDeleteById(Long id) {
        
        final Optional<Product> product = productRepository.findById(id);
        
        if (!product.isPresent() || product.get().getDeleted() != null) {
            return false;
        }
        
        productRepository.softDeleteById(id, now());        
        return true;
    }
    
    private Timestamp now() {
        return Timestamp.from(clock.instant());
    }
}
