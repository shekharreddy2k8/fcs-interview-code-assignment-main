package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain unit tests for {@link ProductResource} using a Mockito-mocked {@link ProductRepository}.
 */
public class ProductResourceTest {

  private ProductResource resource;
  private ProductRepository mockRepo;

  @BeforeEach
  void setUp() throws Exception {
    mockRepo = mock(ProductRepository.class);
    resource = new ProductResource();
    inject(resource, "productRepository", mockRepo);
  }

  // -----------------------------------------------------------------------
  // GET /product
  // -----------------------------------------------------------------------

  @Test
  void getShouldReturnAllProducts() {
    var products = List.of(new Product("KALLAX"), new Product("BESTÅ"));
    when(mockRepo.listAll(any(Sort.class))).thenReturn(products);

    List<Product> result = resource.get();

    assertEquals(2, result.size());
  }

  // -----------------------------------------------------------------------
  // GET /product/{id}
  // -----------------------------------------------------------------------

  @Test
  void getSingleShouldReturnProductWhenFound() {
    Product p = new Product("TONSTAD");
    p.id = 1L;
    when(mockRepo.findById(1L)).thenReturn(p);

    Product result = resource.getSingle(1L);

    assertEquals("TONSTAD", result.name);
  }

  @Test
  void getSingleShouldReturn404WhenNotFound() {
    when(mockRepo.findById(99L)).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.getSingle(99L));

    assertEquals(404, ex.getResponse().getStatus());
  }

  // -----------------------------------------------------------------------
  // POST /product
  // -----------------------------------------------------------------------

  @Test
  void createShouldReturn201WhenIdIsNull() {
    Product p = new Product("KALLAX");
    doNothing().when(mockRepo).persist(p);

    Response response = resource.create(p);

    assertEquals(201, response.getStatus());
    verify(mockRepo).persist(p);
  }

  @Test
  void createShouldReturn422WhenIdIsAlreadySet() {
    Product p = new Product("KALLAX");
    p.id = 5L;

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.create(p));

    assertEquals(422, ex.getResponse().getStatus());
  }

  // -----------------------------------------------------------------------
  // PUT /product/{id}
  // -----------------------------------------------------------------------

  @Test
  void updateShouldReturnUpdatedProduct() {
    Product existing = new Product("OLD");
    existing.id = 1L;
    when(mockRepo.findById(1L)).thenReturn(existing);
    doNothing().when(mockRepo).persist(existing);

    Product update = new Product("NEW");
    update.description = "updated desc";
    update.price = new BigDecimal("9.99");
    update.stock = 5;

    Product result = resource.update(1L, update);

    assertEquals("NEW", result.name);
    assertEquals("updated desc", result.description);
    assertEquals(new BigDecimal("9.99"), result.price);
    assertEquals(5, result.stock);
  }

  @Test
  void updateShouldReturn422WhenNameIsNull() {
    Product update = new Product();
    update.name = null;

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.update(1L, update));

    assertEquals(422, ex.getResponse().getStatus());
  }

  @Test
  void updateShouldReturn404WhenProductNotFound() {
    when(mockRepo.findById(99L)).thenReturn(null);

    Product update = new Product("name");

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.update(99L, update));

    assertEquals(404, ex.getResponse().getStatus());
  }

  // -----------------------------------------------------------------------
  // DELETE /product/{id}
  // -----------------------------------------------------------------------

  @Test
  void deleteShouldReturn204WhenProductFound() {
    Product p = new Product("BESTÅ");
    p.id = 2L;
    when(mockRepo.findById(2L)).thenReturn(p);
    doNothing().when(mockRepo).delete(p);

    Response response = resource.delete(2L);

    assertEquals(204, response.getStatus());
    verify(mockRepo).delete(p);
  }

  @Test
  void deleteShouldReturn404WhenProductNotFound() {
    when(mockRepo.findById(77L)).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
        () -> resource.delete(77L));

    assertEquals(404, ex.getResponse().getStatus());
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
