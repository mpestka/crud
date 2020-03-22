package my.crud.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import my.crud.domain.Product;
import my.crud.rest.dto.ProductDto;
import my.crud.service.ProductService;

@RestController
@ExposesResourceFor(ProductDto.class)
@RequestMapping(
        value = "/products",
        produces = { "application/json" } )
public class ProductController {

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<ProductDto> getAllActive() {
        return productService.getAllActive()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> create(@RequestBody ProductDto productDto) {
        
        Product createdProduct = productService.create(convertFromDto(productDto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
    
    @PutMapping(value = "/{id}", consumes = { "application/json" })
    public ResponseEntity<Void> update(@RequestBody ProductDto productDto, @PathVariable Long id) {
        productDto.setId(id);
        final Product product = convertFromDto(productDto);
        
        if (productService.update(product).isPresent()) {
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productService.softDeleteById(id)) {
            return ResponseEntity.ok().build(); 
        }
        return ResponseEntity.notFound().build();
    }
    
    private ProductDto convertToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    private Product convertFromDto(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
