package com.teaching.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates the Executor framework: thread pools, Callable, Future, and shutdown.
 *
 * Why Executors instead of raw Threads?
 * - Thread creation is expensive. Pools reuse threads across tasks.
 * - Decouples task submission from threading policy.
 * - Provides task queuing, result retrieval (Future), and lifecycle management.
 *
 * Run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.concurrency.ExecutorRunner"
 */
public class ExecutorRunner {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        fixedPoolDemo();
        callableAndFutureDemo();
        invokeAllDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Fixed thread pool - bounded concurrency
    // -------------------------------------------------------------------------

    static void fixedPoolDemo() throws InterruptedException {
        System.out.println("=== Fixed Thread Pool (3 threads, 6 tasks) ===");

        // Exactly 3 threads. Tasks 4-6 queue until a thread becomes available.
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 6; i++) {
            final int id = i;
            pool.execute(() -> {   // execute(): submit a Runnable - fire and forget
                System.out.printf("  task %d started  on %s%n", id, Thread.currentThread().getName());
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.printf("  task %d finished on %s%n", id, Thread.currentThread().getName());
            });
        }

        // shutdown(): reject new submissions, but let queued and running tasks complete.
        // shutdownNow(): interrupt running tasks and return the pending queue - use with care.
        pool.shutdown();
        boolean clean = pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Pool terminated cleanly: " + clean);
    }

    // -------------------------------------------------------------------------
    // 2. Callable + Future - getting a return value from a thread
    //    Callable<T> is like Runnable but returns T and can throw checked exceptions.
    //    Future<T> is the handle to a result that isn't ready yet.
    // -------------------------------------------------------------------------

    static void callableAndFutureDemo() throws InterruptedException, ExecutionException {
        System.out.println("\n=== Callable + Future ===");

        ExecutorService exec = Executors.newSingleThreadExecutor();

        // submit() returns immediately with a Future - the task runs in the background
        Future<Integer> future = exec.submit(() -> {
            System.out.println("  computing on: " + Thread.currentThread().getName());
            Thread.sleep(150);
            return 6 * 7;
        });

        System.out.println("  task submitted; main thread doing other work...");
        System.out.println("  isDone before get(): " + future.isDone());

        // get() BLOCKS until the result is available (or throws if the task threw)
        Integer result = future.get();
        System.out.println("  result: " + result);
        System.out.println("  isDone after  get(): " + future.isDone());

        exec.shutdown();
    }

    // -------------------------------------------------------------------------
    // 3. invokeAll - submit a batch of Callables; wait for all to complete
    // -------------------------------------------------------------------------

    static void invokeAllDemo() throws InterruptedException, ExecutionException {
        System.out.println("\n=== invokeAll (batch of Callables) ===");

        ExecutorService exec = Executors.newFixedThreadPool(4);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            tasks.add(() -> {
                Thread.sleep(50L * id);   // tasks finish at different times
                return "result-" + id + " from " + Thread.currentThread().getName();
            });
        }

        // invokeAll blocks until ALL tasks complete; returns futures in submission order
        List<Future<String>> futures = exec.invokeAll(tasks);

        System.out.println("All tasks finished:");
        for (Future<String> f : futures) {
            System.out.println("  " + f.get());   // get() doesn't block - all are done
        }

        exec.shutdown();
    }
}
