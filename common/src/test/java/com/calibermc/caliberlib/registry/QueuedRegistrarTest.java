package com.calibermc.caliberlib.registry;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueuedRegistrarTest {

	private final List<ResourceLocation> sunk = new ArrayList<>();
	private final QueuedRegistrar<Object> registrar = new QueuedRegistrar<>("test", (id, value) -> sunk.add(id));

	@Test
	void nothingRegistersBeforeFinish() {
		registrar.register("a", Object::new);
		assertEquals(List.of(), sunk);
	}

	@Test
	void entriesRegisterInOrderOnFinish() {
		registrar.register("a", Object::new);
		registrar.register("b", Object::new);
		registrar.finish();
		assertEquals(List.of(new ResourceLocation("test", "a"), new ResourceLocation("test", "b")), sunk);
	}

	@Test
	void getBeforeRegistrationNamesTheId() {
		RegistryEntry<Object> entry = registrar.register("a", Object::new);
		IllegalStateException e = assertThrows(IllegalStateException.class, entry::get);
		assertEquals("Not registered yet: test:a", e.getMessage());
	}

	@Test
	void aLaterSupplierCanReadAnEarlierEntry() {
		RegistryEntry<Object> base = registrar.register("base", Object::new);
		RegistryEntry<Object> stairs = registrar.register("stairs", () -> List.of(base.get()));
		registrar.finish();
		assertSame(base.get(), ((List<?>) stairs.get()).get(0));
	}

	@Test
	void eachSupplierRunsOnce() {
		int[] calls = {0};
		RegistryEntry<Object> entry = registrar.register("a", () -> {
			calls[0]++;
			return new Object();
		});
		registrar.finish();
		Object first = entry.get();
		registrar.finish();
		assertSame(first, entry.get());
		assertEquals(1, calls[0]);
	}

	@Test
	void afterFinishAnEntryRegistersAtOnce() {
		registrar.finish();
		RegistryEntry<Object> late = registrar.register("late", Object::new);
		assertEquals(List.of(new ResourceLocation("test", "late")), sunk);
		late.get();
	}

	@Test
	void aNameRegistersOnlyOnce() {
		registrar.register("a", Object::new);
		assertThrows(IllegalStateException.class, () -> registrar.register("a", Object::new));
	}

	@Test
	void entriesKeepTheirOrderAndIds() {
		registrar.register("a", Object::new);
		registrar.register("b", Object::new);
		assertEquals(List.of("test:a", "test:b"), registrar.entries().stream().map(e -> e.getId().toString()).toList());
	}
}
