package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link Product} entity (constructors and field access). */
public class ProductTest {

  @Test
  void defaultConstructorShouldCreateInstanceWithNullFields() {
    Product product = new Product();
    assertNotNull(product);
    assertNull(product.id);
    assertNull(product.name);
    assertNull(product.description);
    assertNull(product.price);
    assertEquals(0, product.stock);
  }

  @Test
  void nameConstructorShouldSetName() {
    Product product = new Product("KALLAX");
    assertEquals("KALLAX", product.name);
  }

  @Test
  void fieldsShouldBeDirectlyAssignable() {
    Product product = new Product();
    product.name = "BESTÅ";
    product.description = "Shelf unit";
    product.price = new BigDecimal("49.99");
    product.stock = 10;

    assertEquals("BESTÅ", product.name);
    assertEquals("Shelf unit", product.description);
    assertEquals(new BigDecimal("49.99"), product.price);
    assertEquals(10, product.stock);
  }
}
