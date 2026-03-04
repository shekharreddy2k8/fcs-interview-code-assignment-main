package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Unit tests for the {@link FulfillmentAssignment} entity. */
public class FulfillmentAssignmentTest {

  @Test
  void defaultConstructorShouldCreateInstanceWithNullFields() {
    FulfillmentAssignment a = new FulfillmentAssignment();
    assertNotNull(a);
    assertNull(a.id);
    assertNull(a.warehouseCode);
    assertNull(a.productId);
    assertNull(a.storeId);
  }

  @Test
  void parameterizedConstructorShouldSetAllFields() {
    FulfillmentAssignment a = new FulfillmentAssignment("MWH.001", 5L, 3L);

    assertEquals("MWH.001", a.warehouseCode);
    assertEquals(5L, a.productId);
    assertEquals(3L, a.storeId);
    assertNull(a.id); // generated, not set until persisted
  }

  @Test
  void fieldsShouldBeDirectlyAssignable() {
    FulfillmentAssignment a = new FulfillmentAssignment();
    a.id = 100L;
    a.warehouseCode = "MWH.002";
    a.productId = 7L;
    a.storeId = 2L;

    assertEquals(100L, a.id);
    assertEquals("MWH.002", a.warehouseCode);
    assertEquals(7L, a.productId);
    assertEquals(2L, a.storeId);
  }
}
