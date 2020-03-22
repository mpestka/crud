package my.crud.service;

import java.util.List;
import java.util.Optional;

import my.crud.domain.Product;

public interface ProductService {
    /**
     * @return all active (not soft-deleted) products
     */
	List<Product> getAllActive();
	
	/**
	 * Creates new active product ('id' and 'created' fields are ignored and calculated)
	 * @param product given product
	 * @return created product entity
	 */
	Product create(Product product);
	
	/**
	 * Updates given product if exists
	 * @param product to be updated
	 * @return updated product, or an empty optional if product not found
	 */
	Optional<Product> update(Product product);
	
	/**
	 * Deactivates (soft-deletes) product with given id (if active)
	 * @param id
	 * @return <code>true</code> when updated, <code>false</code> otherwise (id not found or already deleted)
	 */
	boolean softDeleteById(Long id);
}
