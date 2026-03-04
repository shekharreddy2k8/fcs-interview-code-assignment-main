package com.fulfilment.application.monolith.warehouses.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link Warehouse} and {@link Location} domain models. */
public class WarehouseModelTest {

  @Test
  void warehouseDefaultConstructorShouldCreateInstanceWithNullFields() {
    Warehouse w = new Warehouse();
    assertNotNull(w);
    assertNull(w.id);
    assertNull(w.businessUnitCode);
    assertNull(w.location);
    assertNull(w.capacity);
    assertNull(w.stock);
    assertNull(w.createdAt);
    assertNull(w.archivedAt);
  }

  @Test
  void warehouseFieldsShouldBeDirectlyAssignable() {
    Warehouse w = new Warehouse();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime archived = LocalDateTime.now().minusDays(2);
    w.id = 1L;
    w.businessUnitCode = "MWH.001";
    w.location = "ZWOLLE-001";
    w.capacity = 100;
    w.stock = 50;
    w.createdAt = now;
    w.archivedAt = archived;

    assertEquals(1L, w.id);
    assertEquals("MWH.001", w.businessUnitCode);
    assertEquals("ZWOLLE-001", w.location);
    assertEquals(100, w.capacity);
    assertEquals(50, w.stock);
    assertEquals(now, w.createdAt);
    assertEquals(archived, w.archivedAt);
  }

  @Test
  void locationConstructorShouldSetAllFields() {
    Location loc = new Location("AMSTERDAM-001", 5, 200);

    assertEquals("AMSTERDAM-001", loc.identification);
    assertEquals(5, loc.maxNumberOfWarehouses);
    assertEquals(200, loc.maxCapacity);
  }
}
