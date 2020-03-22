package my.crud.domain;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class ProductTest {

    @Test
    void sholdVerifyEqualsAndHashCode() {
        // we assume equals on ID field here which might not be the best option,
        // but since we have no any natural key lets stick with it...
        // Anyway this test it only demonstration for now...
        EqualsVerifier.forClass(Product.class)
            .suppress(Warning.SURROGATE_KEY)
            .verify();
    }

}
