package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link LegacyStoreManagerGateway}. */
public class LegacyStoreManagerGatewayTest {

  private LegacyStoreManagerGateway gateway;

  @BeforeEach
  void setUp() {
    gateway = new LegacyStoreManagerGateway();
  }

  @Test
  void createStoreOnLegacySystemShouldNotThrow() {
    Store store = new Store("LegacyCreate");
    store.quantityProductsInStock = 50;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
  }

  @Test
  void updateStoreOnLegacySystemShouldNotThrow() {
    Store store = new Store("LegacyUpdate");
    store.quantityProductsInStock = 20;

    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }

  @Test
  void createStoreWithZeroStockShouldNotThrow() {
    Store store = new Store("EmptyStore");
    store.quantityProductsInStock = 0;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
  }

  /**
   * A store name containing '/' makes Files.createTempFile treat the prefix as a path component,
   * raising an IOException (no such directory), which exercises the catch(Exception) block
   * inside writeToFile. The method swallows the exception, so nothing propagates.
   */
  @Test
  void createStoreWithInvalidNameShouldSwallowException() {
    Store store = new Store();
    store.name = "no/such/dir"; // '/' in prefix → IOException inside writeToFile → catch block

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
  }

  @Test
  void updateStoreWithInvalidNameShouldSwallowException() {
    Store store = new Store();
    store.name = "no/such/dir";

    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}
