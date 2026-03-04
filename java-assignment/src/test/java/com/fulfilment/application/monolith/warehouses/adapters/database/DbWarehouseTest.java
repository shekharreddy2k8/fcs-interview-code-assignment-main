package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DbWarehouse#toWarehouse()}. */
public class DbWarehouseTest {

  @Test
  void toWarehouseShouldMapAllFields() {
    DbWarehouse db = new DbWarehouse();
    db.id = 7L;
    db.businessUnitCode = "MWH.007";
    db.location = "AMSTERDAM-001";
    db.capacity = 100;
    db.stock = 30;
    LocalDateTime now = LocalDateTime.now();
    db.createdAt = now;
    db.archivedAt = null;

    Warehouse w = db.toWarehouse();

    assertNotNull(w);
    assertEquals(7L, w.id);
    assertEquals("MWH.007", w.businessUnitCode);
    assertEquals("AMSTERDAM-001", w.location);
    assertEquals(100, w.capacity);
    assertEquals(30, w.stock);
    assertEquals(now, w.createdAt);
    assertNull(w.archivedAt);
  }

  @Test
  void toWarehouseShouldMapArchivedAtWhenSet() {
    DbWarehouse db = new DbWarehouse();
    db.id = 1L;
    db.businessUnitCode = "MWH.001";
    db.location = "ZWOLLE-001";
    db.capacity = 20;
    db.stock = 5;
    LocalDateTime archived = LocalDateTime.of(2025, 1, 15, 10, 0);
    db.archivedAt = archived;

    Warehouse w = db.toWarehouse();

    assertEquals(archived, w.archivedAt);
  }

  @Test
  void defaultConstructorShouldProduceNullFields() {
    DbWarehouse db = new DbWarehouse();

    assertNull(db.id);
    assertNull(db.businessUnitCode);
    assertNull(db.location);
    assertNull(db.capacity);
    assertNull(db.stock);
    assertNull(db.createdAt);
    assertNull(db.archivedAt);
  }
}
