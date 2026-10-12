import java.util.concurrent.locks.ReentrantLock;

/**
 * The copies of one book, shared by every thread.
 *
 * The race: borrowing is "check, then act":
 *     if (available > 0) {    // 1. check
 *         available--;        // 2. act
 *     }
 * Two threads can both pass step 1 before either does step 2, and the library
 * lends a copy it does not have. Even available-- on its own is read, subtract,
 * write: two threads can read the same value and one update is lost.
 *
 * The fix: a lock around the whole check-and-act, so only one thread at a time
 * can be inside it.
 */
public class Shelf {

    private final int copies;
    private int available;
    private int loansRecorded;
    private final ReentrantLock lock = new ReentrantLock();

    public Shelf(int copies) {
        this.copies = copies;
        this.available = copies;
    }

    public int getCopies() {
        return copies;
    }

    public int getAvailable() {
        lock.lock();
        try {
            return available;
        } finally {
            lock.unlock();
        }
    }

    public int getLoansRecorded() {
        lock.lock();
        try {
            return loansRecorded;
        } finally {
            lock.unlock();
        }
    }

    // ---- The fix: check and act together, under one lock ----

    public boolean borrow() {
        lock.lock();            // one thread at a time from here...
        try {
            if (available > 0) {
                available--;
                loansRecorded++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();      // ...to here. finally: the lock is released even if something throws
        }
    }

    public void giveBack() {
        lock.lock();
        try {
            available++;
        } finally {
            lock.unlock();
        }
    }

    // ---- The race (Stretch 1 only): the same code with no lock ----

    public boolean borrowUnsafe() {
        if (available > 0) {
            Thread.yield();     // give other threads a chance to run, as a busy machine would
            available--;
            int count = loansRecorded;
            Thread.yield();
            loansRecorded = count + 1;
            return true;
        }
        return false;
    }

    public void giveBackUnsafe() {
        int value = available;
        Thread.yield();
        available = value + 1;
    }
}
