package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain unit tests for {@link FulfillmentResource} using Mockito mocks.
 * No Quarkus container needed.
 */
public class FulfillmentResourceTest {

  private FulfillmentResource resource;
  private FulfillmentRepository mockRepo;
  private AssignFulfillmentUseCase mockUseCase;

  @BeforeEach
  void setUp() throws Exception {
    mockRepo = mock(FulfillmentRepository.class);
    mockUseCase = mock(AssignFulfillmentUseCase.class);

    resource = new FulfillmentResource();
    inject(resource, "repository", mockRepo);
    inject(resource, "assignUseCase", mockUseCase);
  }

  // -----------------------------------------------------------------------
  // POST /fulfillment  – assign
  // -----------------------------------------------------------------------

  @Test
  void assignShouldReturn201OnSuccess() {
    var assignment = new FulfillmentAssignment("MWH.001", 1L, 1L);
    when(mockUseCase.assign("MWH.001", 1L, 1L)).thenReturn(assignment);

    var request = new FulfillmentResource.FulfillmentRequest("MWH.001", 1L, 1L);
    Response response = resource.assign(request);

    assertEquals(201, response.getStatus());
    assertEquals(assignment, response.getEntity());
  }

  @Test
  void assignShouldReturn400WhenUseCaseThrowsIllegalArgument() {
    when(mockUseCase.assign(anyString(), anyLong(), anyLong()))
        .thenThrow(new IllegalArgumentException("bad input"));

    var request = new FulfillmentResource.FulfillmentRequest("MWH.001", 1L, 1L);
    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.assign(request));

    assertEquals(400, ex.getResponse().getStatus());
  }

  // -----------------------------------------------------------------------
  // GET /fulfillment  – listAll
  // -----------------------------------------------------------------------

  @Test
  void listAllShouldReturnAllAssignments() {
    var assignments = List.of(
        new FulfillmentAssignment("MWH.001", 1L, 1L),
        new FulfillmentAssignment("MWH.002", 2L, 1L));
    when(mockRepo.listAll()).thenReturn(assignments);

    List<FulfillmentAssignment> result = resource.listAll();

    assertEquals(2, result.size());
  }

  // -----------------------------------------------------------------------
  // GET /fulfillment/store/{storeId}
  // -----------------------------------------------------------------------

  @Test
  void byStoreShouldReturnAssignmentsForStore() {
    var assignments = List.of(new FulfillmentAssignment("MWH.001", 1L, 5L));
    when(mockRepo.findByStoreId(5L)).thenReturn(assignments);

    List<FulfillmentAssignment> result = resource.byStore(5L);

    assertEquals(1, result.size());
    assertEquals(5L, result.get(0).storeId);
  }

  // -----------------------------------------------------------------------
  // GET /fulfillment/warehouse/{warehouseCode}
  // -----------------------------------------------------------------------

  @Test
  void byWarehouseShouldReturnAssignmentsForWarehouse() {
    var assignments = List.of(new FulfillmentAssignment("MWH.007", 3L, 2L));
    when(mockRepo.findByWarehouseCode("MWH.007")).thenReturn(assignments);

    List<FulfillmentAssignment> result = resource.byWarehouse("MWH.007");

    assertEquals(1, result.size());
    assertEquals("MWH.007", result.get(0).warehouseCode);
  }

  // -----------------------------------------------------------------------
  // DELETE /fulfillment/{id}
  // -----------------------------------------------------------------------

  @Test
  void removeShouldReturn204WhenAssignmentExists() {
    var assignment = new FulfillmentAssignment("MWH.001", 1L, 1L);
    assignment.id = 42L;
    when(mockRepo.findById(42L)).thenReturn(assignment);

    Response response = resource.remove(42L);

    assertEquals(204, response.getStatus());
    verify(mockRepo).delete(assignment);
  }

  @Test
  void removeShouldReturn404WhenAssignmentNotFound() {
    when(mockRepo.findById(anyLong())).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.remove(99L));

    assertEquals(404, ex.getResponse().getStatus());
  }

  // -----------------------------------------------------------------------
  // FulfillmentRequest record
  // -----------------------------------------------------------------------

  @Test
  void fulfillmentRequestShouldExposeAccessors() {
    var req = new FulfillmentResource.FulfillmentRequest("MWH.001", 1L, 2L);

    assertEquals("MWH.001", req.warehouseCode());
    assertEquals(1L, req.productId());
    assertEquals(2L, req.storeId());
  }

  // -----------------------------------------------------------------------
  // Helper
  // -----------------------------------------------------------------------

  private static void inject(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
