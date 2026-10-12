import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Task B - Busy library (solution, Core + Stretch).
 *
 * Bonus session: concurrency. Many members borrow at once from one Shelf.
 * Only the summary numbers are printed: which member gets a copy changes from
 * run to run, so it never appears in the output.
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=B_busy_library/solution
 *   mvn -q compile exec:java@task -Dtask=B_busy_library/solution -Dexec.args="--stretch"
 */
public class Main {

    static final int MEMBERS_1 = 8;
    static final int COPIES_1 = 3;
    static final int MEMBERS_2 = 4;
    static final int COPIES_2 = 4;
    static final int CYCLES = 5000;

    /** What scenario 1 found. */
    record Scenario1(int borrowed, int refused, int available) {
    }

    /** What scenario 2 found. */
    record Scenario2(int loansRecorded, int available) {
    }

    /**
     * MEMBERS_1 threads each try once to borrow from COPIES_1 copies.
     * A latch holds every thread until all are ready, so they really do
     * arrive at the same moment.
     */
    static Scenario1 scenario1(Predicate<Shelf> borrow) throws InterruptedException {
        Shelf shelf = new Shelf(COPIES_1);
        CountDownLatch ready = new CountDownLatch(MEMBERS_1);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger borrowed = new AtomicInteger();    // counted from many threads, so atomic
        AtomicInteger refused = new AtomicInteger();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < MEMBERS_1; i++) {
            Thread member = new Thread(() -> {
                ready.countDown();
                try {
                    go.await();                         // everyone waits here...
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (borrow.test(shelf)) {               // ...then all try at once
                    borrowed.incrementAndGet();
                } else {
                    refused.incrementAndGet();
                }
            });
            threads.add(member);
            member.start();
        }
        ready.await();          // every thread is started and waiting
        go.countDown();         // open the gate
        for (Thread member : threads) {
            member.join();      // wait for every thread before reading the results
        }
        return new Scenario1(borrowed.get(), refused.get(), shelf.getAvailable());
    }

    /**
     * MEMBERS_2 members each borrow and give back CYCLES times, on a thread pool
     * of MEMBERS_2 threads (Stretch 2: the pool starts and reuses the threads).
     */
    static Scenario2 scenario2(Predicate<Shelf> borrow, Consumer<Shelf> giveBack) throws InterruptedException {
        Shelf shelf = new Shelf(COPIES_2);
        ExecutorService pool = Executors.newFixedThreadPool(MEMBERS_2);
        for (int i = 0; i < MEMBERS_2; i++) {
            pool.submit(() -> {
                for (int cycle = 0; cycle < CYCLES; cycle++) {
                    if (borrow.test(shelf)) {
                        giveBack.accept(shelf);
                    }
                }
            });
        }
        pool.shutdown();                                // no new work; let the submitted work finish
        if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
            throw new IllegalStateException("scenario 2 did not finish within a minute");
        }
        return new Scenario2(shelf.getLoansRecorded(), shelf.getAvailable());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Busy library");
        System.out.println("Scenario 1: " + MEMBERS_1 + " members, 1 book with " + COPIES_1
                + " copies, everyone borrows at once");
        Scenario1 first = scenario1(Shelf::borrow);
        System.out.println("borrowed: " + first.borrowed());
        System.out.println("refused: " + first.refused());
        System.out.println("available at end: " + first.available());

        System.out.println("Scenario 2: " + MEMBERS_2 + " members, " + COPIES_2 + " copies, " + CYCLES
                + " borrow-and-return cycles each");
        Scenario2 second = scenario2(Shelf::borrow, Shelf::giveBack);
        System.out.println("loans recorded: " + second.loansRecorded());
        System.out.println("available at end: " + second.available());

        boolean ok = first.borrowed() == COPIES_1 && first.available() == 0
                && second.loansRecorded() == MEMBERS_2 * CYCLES && second.available() == COPIES_2;
        System.out.println("Result: " + (ok ? "OK" : "WRONG"));

        if (args.length > 0 && args[0].equals("--stretch")) {
            // Not checked: the numbers change from run to run, which is the point
            System.out.println("Stretch: the same scenarios with no lock");
            Scenario1 unsafeFirst = scenario1(Shelf::borrowUnsafe);
            System.out.println("scenario 1: borrowed " + unsafeFirst.borrowed() + " of " + COPIES_1
                    + " copies, available at end " + unsafeFirst.available());
            Scenario2 unsafeSecond = scenario2(Shelf::borrowUnsafe, Shelf::giveBackUnsafe);
            System.out.println("scenario 2: loans recorded " + unsafeSecond.loansRecorded() + " of "
                    + MEMBERS_2 * CYCLES + ", available at end " + unsafeSecond.available());
        }
    }
}
