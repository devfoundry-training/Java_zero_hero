package com.teaching.concurrency;

/**
 * Demonstrates Thread creation patterns, start() vs run(), join(), sleep(),
 * and basic thread metadata.
 *
 * Run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.concurrency.ThreadRunner"
 *
 * Output order is non-deterministic - threads are scheduled by the OS, not by Java.
 * Re-run a few times to observe different orderings.
 */
public class ThreadRunner {

    public static void main(String[] args) throws InterruptedException {
        threadVsRunnableDemo();
        startVsRunDemo();
        joinAndSleepDemo();
        threadInfoDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Three ways to define a thread's task
    // -------------------------------------------------------------------------

    static void threadVsRunnableDemo() throws InterruptedException {
        System.out.println("=== Thread vs Runnable ===");

        // Option 1: extend Thread - couples the task to the threading mechanism.
        // Inflexible: Java has single inheritance, so you burn your one superclass.
        Thread byExtension = new Thread() {
            @Override
            public void run() {
                System.out.println("  by extension  : " + Thread.currentThread().getName());
            }
        };

        // Option 2: implement Runnable, pass to Thread constructor.
        // Separates WHAT the task does from HOW it is run - preferred over extension.
        Runnable task = () -> System.out.println("  by Runnable   : " + Thread.currentThread().getName());
        Thread byRunnable = new Thread(task);

        // Option 3: inline lambda + named thread - most common modern style.
        // Named threads appear in stack traces and thread dumps, making debugging far easier.
        Thread byLambda = new Thread(
            () -> System.out.println("  by lambda     : " + Thread.currentThread().getName()),
            "worker-1"
        );

        byExtension.start();
        byRunnable.start();
        byLambda.start();

        // join() - wait for each thread to terminate before continuing
        byExtension.join();
        byRunnable.join();
        byLambda.join();
        System.out.println("  all done");
    }

    // -------------------------------------------------------------------------
    // 2. start() creates a new thread. run() does NOT.
    //    This is the single most common beginner mistake with threads.
    // -------------------------------------------------------------------------

    static void startVsRunDemo() throws InterruptedException {
        System.out.println("\n=== start() vs run() ===");

        // run() is just a regular method call - executes synchronously on THIS thread
        Thread t1 = new Thread(() ->
            System.out.println("  run()   executed on: " + Thread.currentThread().getName()));
        System.out.print("  Calling run()  -> ");
        t1.run();    // still on "main" - no concurrency, no new thread

        // start() creates a new OS thread; that thread calls run() concurrently
        Thread t2 = new Thread(() ->
            System.out.println("  start() executed on: " + Thread.currentThread().getName()));
        System.out.print("  Calling start() -> ");
        t2.start();  // new thread, name is "Thread-N"
        t2.join();
    }

    // -------------------------------------------------------------------------
    // 3. join() - block until another thread finishes
    //    sleep() - pause the current thread, releasing the CPU
    // -------------------------------------------------------------------------

    static void joinAndSleepDemo() throws InterruptedException {
        System.out.println("\n=== join() and sleep() ===");

        Thread worker = new Thread(() -> {
            try {
                System.out.println("  worker: started, sleeping 200ms");
                Thread.sleep(200);   // releases CPU; does NOT hold any locks
                System.out.println("  worker: done");
            } catch (InterruptedException e) {
                // InterruptedException means thread.interrupt() was called on this thread.
                // Restore the flag - never silently swallow an interrupt.
                Thread.currentThread().interrupt();
                System.out.println("  worker: interrupted");
            }
        });

        worker.start();
        System.out.println("  main: worker started, calling join()");
        worker.join();    // main blocks here until worker reaches TERMINATED state
        System.out.println("  main: join() returned - worker finished");
    }

    // -------------------------------------------------------------------------
    // 4. Thread metadata
    // -------------------------------------------------------------------------

    static void threadInfoDemo() {
        System.out.println("\n=== Thread Metadata ===");
        Thread t = Thread.currentThread();
        System.out.println("Name     : " + t.getName());
        System.out.println("ID       : " + t.threadId());
        System.out.println("Priority : " + t.getPriority()
            + " (range 1-10, default=" + Thread.NORM_PRIORITY + ")");
        System.out.println("Daemon   : " + t.isDaemon());
        // Daemon threads: JVM exits when ALL non-daemon threads finish -
        // even if daemon threads are still running. GC is a daemon thread.
        // Set before start(): new Thread(...); t.setDaemon(true); t.start();
        System.out.println("State    : " + t.getState()); // RUNNABLE - it is executing right now
    }
}
