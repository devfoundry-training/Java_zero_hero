package com.teaching.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates a race condition, how to fix it with synchronized, and the
 * cleaner alternative using AtomicInteger. Ends with a volatile correctness note.
 *
 * Run several times - the race condition result changes on each run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.concurrency.RaceConditionRunner"
 */
public class RaceConditionRunner {

    private static final int THREADS    = 10;
    private static final int INCREMENTS = 100_000;
    private static final int EXPECTED   = THREADS * INCREMENTS;  // 1_000_000

    public static void main(String[] args) throws InterruptedException {
        raceConditionDemo();
        synchronizedDemo();
        atomicIntegerDemo();
        volatileDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Unsafe counter - count++ is NOT atomic
    //    count++ compiles to three bytecode instructions:
    //      GETFIELD  (read count from memory into a register)
    //      IADD      (increment the register)
    //      PUTFIELD  (write the register back to memory)
    //    Two threads can both read the same value, both increment, both write back -
    //    only one increment is recorded. With 10 threads x 100k this is reliable to trigger.
    // -------------------------------------------------------------------------

    static void raceConditionDemo() throws InterruptedException {
        System.out.println("=== Race Condition (unsafe) - expected " + EXPECTED + " ===");

        UnsafeCounter counter = new UnsafeCounter();
        Thread[] threads = buildThreads(() -> {
            for (int j = 0; j < INCREMENTS; j++) counter.increment();
        });

        startAndJoin(threads);
        System.out.println("Result : " + counter.count
            + (counter.count < EXPECTED ? "  <- data was lost (race condition)" : "  (lucky run, re-run to see race)"));
    }

    // -------------------------------------------------------------------------
    // 2. Synchronized counter - intrinsic lock serialises access
    //    Only one thread can hold the monitor at a time.
    //    Other threads block at the method entry until the lock is released.
    //    Guaranteed correct - but serialises all increments (throughput cost).
    // -------------------------------------------------------------------------

    static void synchronizedDemo() throws InterruptedException {
        System.out.println("\n=== Synchronized Fix ===");

        SynchronizedCounter counter = new SynchronizedCounter();
        Thread[] threads = buildThreads(() -> {
            for (int j = 0; j < INCREMENTS; j++) counter.increment();
        });

        startAndJoin(threads);
        boolean correct = counter.getCount() == EXPECTED;
        System.out.println("Result : " + counter.getCount() + (correct ? "  [OK]" : "  [X]"));
    }

    // -------------------------------------------------------------------------
    // 3. AtomicInteger - lock-free, hardware-level CAS (compare-and-swap)
    //    Preferred over synchronized for simple counter/flag operations.
    //    incrementAndGet() is a single atomic operation - no monitor, no blocking.
    // -------------------------------------------------------------------------

    static void atomicIntegerDemo() throws InterruptedException {
        System.out.println("\n=== AtomicInteger (preferred for counters) ===");

        AtomicInteger counter = new AtomicInteger(0);
        Thread[] threads = buildThreads(() -> {
            for (int j = 0; j < INCREMENTS; j++) counter.incrementAndGet();
        });

        startAndJoin(threads);
        boolean correct = counter.get() == EXPECTED;
        System.out.println("Result : " + counter.get() + (correct ? "  [OK]" : "  [X]"));
    }

    // -------------------------------------------------------------------------
    // 4. volatile - visibility guarantee, NOT atomicity
    //    volatile ensures every thread reads the freshest value from main memory
    //    rather than a stale cached copy. It does NOT prevent interleaved read-modify-write.
    //    Correct use: a flag written by one thread, read by others - no compound operation.
    // -------------------------------------------------------------------------

    static void volatileDemo() throws InterruptedException {
        System.out.println("\n=== volatile (visibility flag) ===");

        VolatileFlag flag = new VolatileFlag();

        Thread worker = new Thread(() -> {
            int spins = 0;
            // Without volatile, the JIT may cache flag.running in a CPU register and
            // never re-read memory - the loop would never see the update and spin forever.
            while (flag.running) {
                spins++;
            }
            System.out.println("  worker stopped after " + spins + " spins");
        });

        worker.start();
        Thread.sleep(5);     // let the worker spin for a bit
        flag.running = false; // write is immediately visible to worker because of volatile
        worker.join(1_000);

        // volatile int x; x++ is STILL a race:  read(x) -> increment -> write(x)
        // For that, use AtomicInteger. volatile alone is not enough for compound ops.
        System.out.println("  volatile: correct for single-writer flag, NOT for counter");
    }

    // -------------------------------------------------------------------------
    // Supporting classes - inner static so everything stays in one file
    // -------------------------------------------------------------------------

    static class UnsafeCounter {
        int count = 0;
        void increment() { count++; }   // not thread-safe
    }

    static class SynchronizedCounter {
        private int count = 0;

        // synchronized method: uses `this` as the monitor (intrinsic lock)
        synchronized void increment() { count++; }
        synchronized int  getCount()  { return count; }

        // Equivalent granular form using a synchronized block:
        // void increment() { synchronized (this) { count++; } }
    }

    static class VolatileFlag {
        // volatile: write by one thread is immediately visible to all other threads.
        // Without it, the JIT/CPU can keep a stale copy in a register or cache line.
        volatile boolean running = true;
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------

    static Thread[] buildThreads(Runnable task) {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) threads[i] = new Thread(task);
        return threads;
    }

    static void startAndJoin(Thread[] threads) throws InterruptedException {
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }
}
