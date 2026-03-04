package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Unit tests for the {@link Store} entity (constructors and field access). */
public class StoreTest {

  @Test
  void defaultConstructorShouldCreateInstanceWithNullName() {
    Store store = new Store();
    assertNotNull(store);
    assertNull(store.name);
  }

  @Test
  void nameConstructorShouldSetName() {
    Store store = new Store("MyStore");
    assertEquals("MyStore", store.name);
  }

  @Test
  void fieldsShouldBeDirectlyAssignable() {
    Store store = new Store();
    store.name = "TestStore";
    store.quantityProductsInStock = 42;

    assertEquals("TestStore", store.name);
    assertEquals(42, store.quantityProductsInStock);
  }
}
