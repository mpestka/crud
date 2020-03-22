package my.crud.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import my.crud.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.deleted is null")
    List<Product> findAllActive();
    
    @Modifying
    @Query("UPDATE Product p SET p.deleted = :deleted WHERE p.id = :id and deleted is null")
    int softDeleteById(@Param("id") Long id, @Param("deleted") Timestamp deleted);
}
