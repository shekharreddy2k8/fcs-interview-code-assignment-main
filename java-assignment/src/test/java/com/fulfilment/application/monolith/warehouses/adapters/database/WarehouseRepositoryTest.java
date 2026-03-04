package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link WarehouseRepository}.
 * Panache query/persist methods are mocked via a spy.
 */
public class WarehouseRepositoryTest {

  private WarehouseRepository repo;

  @BeforeEach
  void setUp() {
    repo = spy(new WarehouseRepository());
  }

  // -----------------------------------------------------------------------
  // getAll
  // -----------------------------------------------------------------------

  @Test
  @SuppressWarnings("unchecked")
  void getAllShouldMapDbWarehousesToDomainWarehouses() {
    DbWarehouse db = dbWarehouse(1L, "MWH.001", "ZWOLLE-001", 100, 10, null);
    doReturn(List.of(db)).when(repo).listAll();

    List<Warehouse> result = repo.getAll();

    assertEquals(1, result.size());
    assertEquals("MWH.001", result.get(0).businessUnitCode);
  }

  // -----------------------------------------------------------------------
  // create
  // -----------------------------------------------------------------------

  @Test
  void createShouldPersistNewDbWarehouse() {
    Warehouse w = warehouse("MWH.002", "AMSTERDAM-001", 50, 20, null);
    doNothing().when(repo).persist(any(DbWarehouse.class));

    repo.create(w);

    verify(repo).persist(any(DbWarehouse.class));
  }

  // -----------------------------------------------------------------------
  // update
  // -----------------------------------------------------------------------

  @Test
  @SuppressWarnings("unchecked")
  void updateShouldModifyExistingActiveDbWarehouse() {
    DbWarehouse existing = dbWarehouse(1L, "MWH.003", "ZWOLLE-001", 30, 5, null);
    PanacheQuery<DbWarehouse> mockQuery = mock(PanacheQuery.class);
    when(mockQuery.firstResult()).thenReturn(existing);
    doReturn(mockQuery).when(repo).find(anyString(), eq("MWH.003"));

    Warehouse updated = warehouse("MWH.003", "AMSTERDAM-001", 50, 10, null);
    repo.update(updated);

    assertEquals("AMSTERDAM-001", existing.location);
    assertEquals(50, existing.capacity);
    assertEquals(10, existing.stock);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateShouldDoNothingWhenWarehouseNotFound() {
    PanacheQuery<DbWarehouse> mockQuery = mock(PanacheQuery.class);
    when(mockQuery.firstResult()).thenReturn(null);
    doReturn(mockQuery).when(repo).find(anyString(), eq("NONEXISTENT"));

    // Should not throw
    repo.update(warehouse("NONEXISTENT", "X", 10, 5, null));
  }

  // -----------------------------------------------------------------------
  // remove
  // -----------------------------------------------------------------------

  @Test
  void removeShouldDeleteByBusinessUnitCode() {
    doReturn(0L).when(repo).delete(anyString(), eq("MWH.004"));

    Warehouse w = warehouse("MWH.004", "ZWOLLE-001", 20, 5, null);
    repo.remove(w);

    verify(repo).delete(anyString(), eq("MWH.004"));
  }

  // -----------------------------------------------------------------------
  // findById(String)
  // -----------------------------------------------------------------------

  @Test
  void findByIdShouldReturnNullForNonNumericId() {
    Warehouse result = repo.findById("NOT_A_NUMBER");
    assertNull(result);
  }

  @Test
  void findByIdShouldReturnNullWhenDbRowNotFound() {
    doReturn(null).when(repo).findById(42L);

    Warehouse result = repo.findById("42");

    assertNull(result);
  }

  @Test
  void findByIdShouldReturnNullWhenDbWarehouseIsArchived() {
    DbWarehouse archived = dbWarehouse(5L, "MWH.005", "ZWOLLE-001", 20, 5,
        LocalDateTime.now().minusDays(1));
    doReturn(archived).when(repo).findById(5L);

    Warehouse result = repo.findById("5");

    assertNull(result);
  }

  @Test
  void findByIdShouldReturnWarehouseWhenFoundAndActive() {
    DbWarehouse active = dbWarehouse(6L, "MWH.006", "TILBURG-001", 40, 15, null);
    doReturn(active).when(repo).findById(6L);

    Warehouse result = repo.findById("6");

    assertNotNull(result);
    assertEquals("MWH.006", result.businessUnitCode);
  }

  // -----------------------------------------------------------------------
  // findByBusinessUnitCode
  // -----------------------------------------------------------------------

  @Test
  @SuppressWarnings("unchecked")
  void findByBusinessUnitCodeShouldReturnWarehouseWhenFound() {
    DbWarehouse db = dbWarehouse(7L, "MWH.007", "AMSTERDAM-001", 70, 30, null);
    PanacheQuery<DbWarehouse> mockQuery = mock(PanacheQuery.class);
    when(mockQuery.firstResult()).thenReturn(db);
    doReturn(mockQuery).when(repo).find(anyString(), eq("MWH.007"));

    Warehouse result = repo.findByBusinessUnitCode("MWH.007");

    assertNotNull(result);
    assertEquals("MWH.007", result.businessUnitCode);
  }

  @Test
  @SuppressWarnings("unchecked")
  void findByBusinessUnitCodeShouldReturnNullWhenNotFound() {
    PanacheQuery<DbWarehouse> mockQuery = mock(PanacheQuery.class);
    when(mockQuery.firstResult()).thenReturn(null);
    doReturn(mockQuery).when(repo).find(anyString(), eq("MISSING"));

    Warehouse result = repo.findByBusinessUnitCode("MISSING");

    assertNull(result);
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  private static DbWarehouse dbWarehouse(Long id, String buCode, String location,
      int capacity, int stock, LocalDateTime archivedAt) {
    DbWarehouse db = new DbWarehouse();
    db.id = id;
    db.businessUnitCode = buCode;
    db.location = location;
    db.capacity = capacity;
    db.stock = stock;
    db.archivedAt = archivedAt;
    return db;
  }

  private static Warehouse warehouse(String buCode, String location,
      int capacity, int stock, LocalDateTime archivedAt) {
    Warehouse w = new Warehouse();
    w.businessUnitCode = buCode;
    w.location = location;
    w.capacity = capacity;
    w.stock = stock;
    w.archivedAt = archivedAt;
    return w;
  }
}
