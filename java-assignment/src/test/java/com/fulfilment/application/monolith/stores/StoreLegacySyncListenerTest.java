package com.fulfilment.application.monolith.stores;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link StoreLegacySyncListener}. */
public class StoreLegacySyncListenerTest {

  private StoreLegacySyncListener listener;
  private LegacyStoreManagerGateway mockGateway;

  @BeforeEach
  void setUp() throws Exception {
    mockGateway = mock(LegacyStoreManagerGateway.class);
    listener = new StoreLegacySyncListener();

    Field field = StoreLegacySyncListener.class.getDeclaredField("legacyStoreManagerGateway");
    field.setAccessible(true);
    field.set(listener, mockGateway);
  }

  @Test
  void shouldCallCreateOnLegacySystemWhenActionIsCreate() {
    Store store = new Store("NewStore");
    StoreSyncEvent event = StoreSyncEvent.create(store, StoreSyncEvent.Action.CREATE);

    listener.onStoreChanged(event);

    verify(mockGateway).createStoreOnLegacySystem(store);
    verifyNoMoreInteractions(mockGateway);
  }

  @Test
  void shouldCallUpdateOnLegacySystemWhenActionIsUpdate() {
    Store store = new Store("ExistingStore");
    StoreSyncEvent event = StoreSyncEvent.create(store, StoreSyncEvent.Action.UPDATE);

    listener.onStoreChanged(event);

    verify(mockGateway).updateStoreOnLegacySystem(store);
    verifyNoMoreInteractions(mockGateway);
  }
}
