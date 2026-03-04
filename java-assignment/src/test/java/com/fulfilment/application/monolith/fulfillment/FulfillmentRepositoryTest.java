package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FulfillmentRepository}.
 *
 * <p>Panache repository methods (find, list, count) are stubbed using
 * {@code doReturn().when(spy)} to avoid triggering the real Panache bytecode
 * that throws outside the CDI container.
 */
@SuppressWarnings("unchecked")
public class FulfillmentRepositoryTest {

  private FulfillmentRepository repo;
  private EntityManager mockEm;

  @BeforeEach
  void setUp() throws Exception {
    mockEm = mock(EntityManager.class);
    // spy — doReturn() stubs avoid invoking the real Panache internals
    repo = spy(new FulfillmentRepository());

    Field emField = FulfillmentRepository.class.getDeclaredField("entityManager");
    emField.setAccessible(true);
    emField.set(repo, mockEm);
  }

  // -----------------------------------------------------------------------
  // persist — delegates to injected EntityManager directly
  // -----------------------------------------------------------------------

  @Test
  void persistShouldDelegateToEntityManager() {
    FulfillmentAssignment a = new FulfillmentAssignment("MWH.001", 1L, 1L);
    doNothing().when(mockEm).persist(a);

    repo.persist(a);

    verify(mockEm).persist(a);
  }

  // -----------------------------------------------------------------------
  // findByStoreId — calls list("storeId", storeId)
  // -----------------------------------------------------------------------

  @Test
  void findByStoreIdShouldReturnMatchingAssignments() {
    FulfillmentAssignment a = new FulfillmentAssignment("MWH.001", 1L, 5L);
    // Use doReturn to avoid triggering real Panache list()
    doReturn(List.of(a)).when(repo).list("storeId", 5L);

    List<FulfillmentAssignment> result = repo.findByStoreId(5L);

    assertEquals(1, result.size());
    assertEquals(5L, result.get(0).storeId);
  }

  // -----------------------------------------------------------------------
  // findByWarehouseCode — calls list("warehouseCode", warehouseCode)
  // -----------------------------------------------------------------------

  @Test
  void findByWarehouseCodeShouldReturnMatchingAssignments() {
    FulfillmentAssignment a = new FulfillmentAssignment("MWH.007", 2L, 3L);
    doReturn(List.of(a)).when(repo).list("warehouseCode", "MWH.007");

    List<FulfillmentAssignment> result = repo.findByWarehouseCode("MWH.007");

    assertEquals(1, result.size());
    assertEquals("MWH.007", result.get(0).warehouseCode);
  }

  // -----------------------------------------------------------------------
  // countDistinctWarehousesForProductAndStore — uses find(...).stream()
  // -----------------------------------------------------------------------

  @Test
  void countDistinctWarehousesForProductAndStoreShouldCountCorrectly() {
    FulfillmentAssignment a1 = new FulfillmentAssignment("MWH.001", 1L, 1L);
    FulfillmentAssignment a2 = new FulfillmentAssignment("MWH.002", 1L, 1L);
    FulfillmentAssignment a3 = new FulfillmentAssignment("MWH.001", 1L, 1L); // duplicate warehouse

    PanacheQuery<FulfillmentAssignment> mockQuery = mock(PanacheQuery.class);
    doReturn(Stream.of(a1, a2, a3)).when(mockQuery).stream();
    doReturn(mockQuery).when(repo).find("productId = ?1 and storeId = ?2", 1L, 1L);

    long count = repo.countDistinctWarehousesForProductAndStore(1L, 1L);

    assertEquals(2, count); // MWH.001 and MWH.002 — duplicate collapsed
  }

  // -----------------------------------------------------------------------
  // countDistinctWarehousesForStore — uses find("storeId", storeId).stream()
  // -----------------------------------------------------------------------

  @Test
  void countDistinctWarehousesForStoreShouldCountCorrectly() {
    FulfillmentAssignment a1 = new FulfillmentAssignment("MWH.001", 1L, 2L);
    FulfillmentAssignment a2 = new FulfillmentAssignment("MWH.002", 2L, 2L);
    FulfillmentAssignment a3 = new FulfillmentAssignment("MWH.001", 3L, 2L); // same warehouse, different product

    PanacheQuery<FulfillmentAssignment> mockQuery = mock(PanacheQuery.class);
    doReturn(Stream.of(a1, a2, a3)).when(mockQuery).stream();
    doReturn(mockQuery).when(repo).find("storeId", 2L);

    long count = repo.countDistinctWarehousesForStore(2L);

    assertEquals(2, count); // MWH.001 and MWH.002
  }

  // -----------------------------------------------------------------------
  // countDistinctProductsInWarehouse — uses find("warehouseCode", ...).stream()
  // -----------------------------------------------------------------------

  @Test
  void countDistinctProductsInWarehouseShouldCountCorrectly() {
    FulfillmentAssignment a1 = new FulfillmentAssignment("MWH.003", 1L, 1L);
    FulfillmentAssignment a2 = new FulfillmentAssignment("MWH.003", 2L, 1L);
    FulfillmentAssignment a3 = new FulfillmentAssignment("MWH.003", 1L, 2L); // same product, different store

    PanacheQuery<FulfillmentAssignment> mockQuery = mock(PanacheQuery.class);
    doReturn(Stream.of(a1, a2, a3)).when(mockQuery).stream();
    doReturn(mockQuery).when(repo).find("warehouseCode", "MWH.003");

    long count = repo.countDistinctProductsInWarehouse("MWH.003");

    assertEquals(2, count); // products 1 and 2
  }

  // -----------------------------------------------------------------------
  // exists — delegates to count(...)
  // -----------------------------------------------------------------------

  @Test
  void existsShouldReturnTrueWhenCountIsPositive() {
    doReturn(1L).when(repo).count(
        "warehouseCode = ?1 and productId = ?2 and storeId = ?3",
        "MWH.001", 1L, 1L);

    assertTrue(repo.exists("MWH.001", 1L, 1L));
  }

  @Test
  void existsShouldReturnFalseWhenCountIsZero() {
    doReturn(0L).when(repo).count(
        "warehouseCode = ?1 and productId = ?2 and storeId = ?3",
        "MWH.999", 99L, 99L);

    assertFalse(repo.exists("MWH.999", 99L, 99L));
  }
}
