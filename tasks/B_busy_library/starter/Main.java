/**
 * Task B - Busy library (starter).
 *
 * Shelf.java is a stub. Fill in each part marked TODO, then run the check
 * several times (see tasks/README.md): a race that is only sometimes wrong is
 * still wrong.
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=B_busy_library/starter
 */
public class Main {

    static final int MEMBERS_1 = 8;
    static final int COPIES_1 = 3;
    static final int MEMBERS_2 = 4;
    static final int COPIES_2 = 4;
    static final int CYCLES = 5000;

    public static void main(String[] args) {
        System.out.println("Busy library");
        // TODO Core 2: MEMBERS_1 threads wait at a CountDownLatch (or CyclicBarrier),
        // then each tries once to borrow from a Shelf of COPIES_1; join them all
        System.out.println("Core 2: scenario 1 not implemented");
        // TODO Core 3: MEMBERS_2 members, each CYCLES times borrow then give back,
        // on Executors.newFixedThreadPool(MEMBERS_2)
        System.out.println("Core 3: scenario 2 not implemented");
        // TODO Core 4: the summary lines and "Result: OK" or "Result: WRONG"
    }
}
