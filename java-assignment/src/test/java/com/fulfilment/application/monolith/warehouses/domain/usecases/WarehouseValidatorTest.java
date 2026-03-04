package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

/** Unit tests that directly exercise every branch of {@link WarehouseValidator}. */
public class WarehouseValidatorTest {

	// -----------------------------------------------------------------------
	// null / blank combinations that trigger the first guard block
	// -----------------------------------------------------------------------

	@Test
	void shouldRejectNullWarehouse() {
		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(null));
	}

	@Test
	void shouldRejectWhenBusinessUnitCodeIsNull() {
		Warehouse w = new Warehouse();
		w.location = "ZWOLLE-001";
		w.capacity = 10;
		w.stock = 5;
		// businessUnitCode is null by default

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenBusinessUnitCodeIsBlank() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "   ";
		w.location = "ZWOLLE-001";
		w.capacity = 10;
		w.stock = 5;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenLocationIsNull() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "MWH.001";
		// location is null by default
		w.capacity = 10;
		w.stock = 5;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenLocationIsBlank() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "MWH.001";
		w.location = "";
		w.capacity = 10;
		w.stock = 5;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenCapacityIsNull() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "MWH.001";
		w.location = "ZWOLLE-001";
		// capacity is null by default
		w.stock = 5;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenStockIsNull() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "MWH.001";
		w.location = "ZWOLLE-001";
		w.capacity = 10;
		// stock is null by default

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	// -----------------------------------------------------------------------
	// Invalid capacity / stock values (second guard block)
	// -----------------------------------------------------------------------

	@Test
	void shouldRejectWhenCapacityIsZero() {
		Warehouse w = validWarehouse();
		w.capacity = 0;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenCapacityIsNegative() {
		Warehouse w = validWarehouse();
		w.capacity = -5;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	@Test
	void shouldRejectWhenStockIsNegative() {
		Warehouse w = validWarehouse();
		w.stock = -1;

		assertThrows(IllegalArgumentException.class,
				() -> WarehouseValidator.validateMandatoryFields(w));
	}

	// -----------------------------------------------------------------------
	// Happy path
	// -----------------------------------------------------------------------

	@Test
	void shouldAcceptValidWarehouse() {
		assertDoesNotThrow(() -> WarehouseValidator.validateMandatoryFields(validWarehouse()));
	}

	@Test
	void shouldAcceptWarehouseWithZeroStock() {
		Warehouse w = validWarehouse();
		w.stock = 0; // zero stock is perfectly valid

		assertDoesNotThrow(() -> WarehouseValidator.validateMandatoryFields(w));
	}

	// -----------------------------------------------------------------------
	// Helper
	// -----------------------------------------------------------------------

	private static Warehouse validWarehouse() {
		Warehouse w = new Warehouse();
		w.businessUnitCode = "MWH.001";
		w.location = "ZWOLLE-001";
		w.capacity = 20;
		w.stock = 5;
		return w;
	}
}
