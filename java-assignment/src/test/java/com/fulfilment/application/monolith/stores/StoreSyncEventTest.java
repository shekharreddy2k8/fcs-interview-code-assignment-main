package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Unit tests for the {@link StoreSyncEvent} value object and its {@link StoreSyncEvent.Action} enum. */
public class StoreSyncEventTest {

  @Test
  void createShouldReturnEventWithCreateAction() {
    Store store = new Store("TestStore");
    StoreSyncEvent event = StoreSyncEvent.create(store, StoreSyncEvent.Action.CREATE);

    assertNotNull(event);
    assertSame(store, event.store());
    assertEquals(StoreSyncEvent.Action.CREATE, event.action());
  }

  @Test
  void createShouldReturnEventWithUpdateAction() {
    Store store = new Store("UpdatedStore");
    StoreSyncEvent event = StoreSyncEvent.create(store, StoreSyncEvent.Action.UPDATE);

    assertNotNull(event);
    assertSame(store, event.store());
    assertEquals(StoreSyncEvent.Action.UPDATE, event.action());
  }

  @Test
  void actionEnumShouldHaveTwoValues() {
    StoreSyncEvent.Action[] actions = StoreSyncEvent.Action.values();
    assertEquals(2, actions.length);
  }

  @Test
  void actionValueOfShouldResolveCreate() {
    assertEquals(StoreSyncEvent.Action.CREATE, StoreSyncEvent.Action.valueOf("CREATE"));
  }

  @Test
  void actionValueOfShouldResolveUpdate() {
    assertEquals(StoreSyncEvent.Action.UPDATE, StoreSyncEvent.Action.valueOf("UPDATE"));
  }
}
