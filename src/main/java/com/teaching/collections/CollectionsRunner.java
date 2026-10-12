package com.teaching.collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Demonstrates the Java Collections Framework: List, Set, Map, Queue/Deque,
 * and the Collections utility class.
 *
 * Run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.collections.CollectionsRunner"
 *
 * Key concept - collections store references (not values) for objects:
 *   List<int> is illegal; use List<Integer> (autoboxing handles int <-> Integer).
 *
 * Interface vs implementation:
 *   Declare variables using the interface type (List, Set, Map) so you can
 *   swap the backing implementation without changing the rest of the code.
 *   List<String> names = new ArrayList<>();   <- good
 *   ArrayList<String> names = new ArrayList<>();  <- avoid (locks you in)
 */
public class CollectionsRunner {

    public static void main(String[] args) {
        listDemo();
        setDemo();
        mapDemo();
        queueAndDequeDemo();
        collectionsUtilityDemo();
    }

    // -------------------------------------------------------------------------
    // 1. List - ordered, allows duplicates, index-accessible
    // -------------------------------------------------------------------------

    static void listDemo() {
        System.out.println("=== List Demo ===");

        // ArrayList: O(1) random access, O(n) insert/remove in the middle
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        names.add("Bob");   // duplicates are allowed

        System.out.println("ArrayList: " + names);
        System.out.println("  get(1)       : " + names.get(1));
        System.out.println("  size()       : " + names.size());
        System.out.println("  contains(Bob): " + names.contains("Bob"));
        System.out.println("  indexOf(Bob) : " + names.indexOf("Bob"));  // first occurrence

        names.remove("Bob");   // removes first occurrence by value
        System.out.println("  after remove(\"Bob\"): " + names);

        // Index-based remove:
        names.remove(0);   // removes "Alice" (index 0)
        System.out.println("  after remove(0)   : " + names);

        // Iteration with a classic for-loop
        System.out.println("  Indexed for loop  : ");
        List<Integer> numbers = new ArrayList<>(List.of(10, 20, 30, 40));
        for (int i = 0; i < numbers.size(); i++) {
            System.out.print("    [" + i + "]=" + numbers.get(i) + " ");
        }
        System.out.println();

        // Iteration with Iterator - safe for removing during traversal
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            int n = it.next();
            if (n % 20 == 0) it.remove();   // safe; direct list.remove() inside a for-each throws
        }
        System.out.println("  After removing multiples of 20 via Iterator: " + numbers);

        // List.of() - immutable (factory method, Java 9+)
        List<String> immutable = List.of("X", "Y", "Z");
        System.out.println("  List.of() (immutable): " + immutable);
        try {
            immutable.add("W");   // UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("  add() on List.of() -> UnsupportedOperationException (immutable)");
        }
    }

    // -------------------------------------------------------------------------
    // 2. Set - unordered (usually), no duplicates
    // -------------------------------------------------------------------------

    static void setDemo() {
        System.out.println("\n=== Set Demo ===");

        // HashSet: O(1) add/contains/remove on average; no guaranteed order
        Set<String> hashSet = new HashSet<>(List.of("Banana", "Apple", "Cherry", "Apple"));
        System.out.println("HashSet (no order guaranteed): " + hashSet);
        System.out.println("  Duplicate 'Apple' silently ignored: size=" + hashSet.size());

        // LinkedHashSet: preserves insertion order; slightly slower than HashSet
        Set<String> linked = new LinkedHashSet<>(List.of("Banana", "Apple", "Cherry", "Apple"));
        System.out.println("LinkedHashSet (insertion order): " + linked);

        // TreeSet: always sorted (natural order or Comparator); O(log n) operations
        Set<String> tree = new TreeSet<>(List.of("Banana", "Apple", "Cherry"));
        System.out.println("TreeSet (sorted order)        : " + tree);

        // Set operations (mathematical)
        Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
        Set<Integer> b = new HashSet<>(List.of(3, 4, 5, 6));

        Set<Integer> union = new HashSet<>(a);
        union.addAll(b);                  // A union B
        System.out.println("  Union        " + a + " union " + b + " = " + new TreeSet<>(union));

        Set<Integer> intersection = new HashSet<>(a);
        intersection.retainAll(b);        // A intersect B
        System.out.println("  Intersection " + a + " intersect " + b + " = " + intersection);

        Set<Integer> difference = new HashSet<>(a);
        difference.removeAll(b);          // A \ B
        System.out.println("  Difference   " + a + " \\ " + b + " = " + difference);
    }

    // -------------------------------------------------------------------------
    // 3. Map - key -> value pairs; keys unique, values may repeat
    // -------------------------------------------------------------------------

    static void mapDemo() {
        System.out.println("\n=== Map Demo ===");

        // HashMap: O(1) get/put on average; no order
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 90);
        scores.put("Bob",   75);
        scores.put("Carol", 88);
        scores.put("Alice", 95);   // put on existing key -> replaces the old value
        System.out.println("HashMap: " + scores);

        // Retrieval
        System.out.println("  get(Alice)                : " + scores.get("Alice"));
        System.out.println("  get(Unknown)              : " + scores.get("Unknown"));  // null
        System.out.println("  getOrDefault(Unknown, -1) : " + scores.getOrDefault("Unknown", -1));
        System.out.println("  containsKey(Bob)          : " + scores.containsKey("Bob"));

        // putIfAbsent - only inserts if key is absent (useful for initialising counts)
        scores.putIfAbsent("Dave", 80);   // Dave is absent -> inserted
        scores.putIfAbsent("Alice", 0);   // Alice already exists -> no change
        System.out.println("  after putIfAbsent(Dave,80) + putIfAbsent(Alice,0): " + scores);

        // Iterate over entries (entrySet)
        System.out.println("  entrySet iteration (sorted by key for readability):");
        new TreeMap<>(scores).forEach(
            (name, score) -> System.out.println("    " + name + " -> " + score)
        );

        // LinkedHashMap preserves insertion order
        Map<String, Integer> ordered = new LinkedHashMap<>();
        ordered.put("first",  1);
        ordered.put("second", 2);
        ordered.put("third",  3);
        System.out.println("  LinkedHashMap (insertion order): " + ordered);

        // TreeMap - always sorted by key
        Map<String, Integer> sorted = new TreeMap<>(scores);
        System.out.println("  TreeMap (sorted by key): " + sorted);
    }

    // -------------------------------------------------------------------------
    // 4. Queue and Deque - FIFO and LIFO structures
    // -------------------------------------------------------------------------

    static void queueAndDequeDemo() {
        System.out.println("\n=== Queue and Deque Demo ===");

        // ArrayDeque as a Queue (FIFO - First In, First Out)
        // Prefer offer/poll/peek over add/remove/element for null-safe behaviour:
        //   offer() returns false on failure (add() throws)
        //   poll()  returns null on empty  (remove() throws)
        //   peek()  returns null on empty  (element() throws)
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("first");
        queue.offer("second");
        queue.offer("third");
        System.out.println("Queue (FIFO): " + queue);
        System.out.println("  peek()  (head, not removed): " + queue.peek());
        System.out.println("  poll()  -> " + queue.poll() + "  queue now: " + queue);
        System.out.println("  poll()  -> " + queue.poll() + "  queue now: " + queue);

        // ArrayDeque as a Stack (LIFO - Last In, First Out)
        // java.util.Stack is a legacy class (extends Vector - synchronized, slower).
        // Use ArrayDeque instead.
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        System.out.println("\nStack (LIFO) using ArrayDeque: " + stack);
        System.out.println("  peek() (top, not removed): " + stack.peek());
        System.out.println("  pop()  -> " + stack.pop() + "  stack now: " + stack);
        System.out.println("  pop()  -> " + stack.pop() + "  stack now: " + stack);
    }

    // -------------------------------------------------------------------------
    // 5. Collections utility class - algorithms on collections
    // -------------------------------------------------------------------------

    static void collectionsUtilityDemo() {
        System.out.println("\n=== Collections Utility Demo ===");

        List<Integer> nums = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));
        System.out.println("Original              : " + nums);

        Collections.sort(nums);
        System.out.println("sort()                : " + nums);

        Collections.reverse(nums);
        System.out.println("reverse()             : " + nums);

        System.out.println("min()                 : " + Collections.min(nums));
        System.out.println("max()                 : " + Collections.max(nums));
        System.out.println("frequency(1)          : " + Collections.frequency(nums, 1));

        Collections.sort(nums);
        int idx = Collections.binarySearch(nums, 5);   // list must be sorted first
        System.out.println("binarySearch(5)       : index " + idx);

        // Unmodifiable wrapper - defensive copy pattern:
        // expose a read-only view of an internal mutable list
        List<Integer> readOnly = Collections.unmodifiableList(nums);
        System.out.println("unmodifiableList view : " + readOnly);
        try {
            readOnly.add(99);   // UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("  add() on unmodifiableList -> UnsupportedOperationException");
        }
    }
}
