package my.crud.rest.dto;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ProductDto {
    private Long id;
    private String name;
    private Long price;
    private Timestamp created;
}
