package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;
import interpreter.model.type.RefType;

import java.util.List;
import java.util.Map;
import java.util.Set;

class GarbageCollectorTest {
    private final List<IValue> symTable = List.of(
            new RefValue(3, new IntType()), new RefValue(2, new RefType(new IntType())), new IntValue(5));
    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20), 2, new RefValue(1, new IntType()),
            3, new IntValue(30), 4, new IntValue(7));

    @Test
    void symTableAddressesAreOnlyThoseOfReferences() {
        assertEquals(Set.of(2, 3), GarbageCollector.getAddrFromSymTable(symTable));
    }

    @Test
    void unsafeCollectorAlsoRemovesALocationReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.unsafeGarbageCollector(roots, heap);
        assertEquals(Set.of(2, 3), kept.keySet());
    }

    @Test
    void keepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(Set.of(1, 2, 3), kept.keySet());
        assertEquals(new IntValue(20), kept.get(1));
    }

    @Test
    void reachableAddressesFollowsAChainOfReferences() {
        Map<Integer, IValue> chain = Map.of(
                1, new RefValue(2, new RefType(new RefType(new IntType()))),
                2, new RefValue(3, new RefType(new IntType())),
                3, new RefValue(4, new IntType()),
                4, new IntValue(7),
                5, new IntValue(9));
        assertEquals(Set.of(1, 2, 3, 4), GarbageCollector.reachableAddresses(Set.of(1), chain));
    }

    @Test
    void nullReferencesAndCyclesDoNotBreakTheCollector() {
        Map<Integer, IValue> cycle = Map.of(
                1, new RefValue(2, new IntType()),
                2, new RefValue(1, new IntType()));
        assertEquals(Set.of(0, 1, 2), GarbageCollector.reachableAddresses(Set.of(0, 1), cycle));
        assertEquals(Set.of(1, 2), GarbageCollector.safeGarbageCollector(Set.of(0, 1), cycle).keySet());
    }

    @Test
    void emptySymTableMeansEverythingIsGarbage() {
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(), heap).isEmpty());
    }
}