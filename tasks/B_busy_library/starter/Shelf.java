/**
 * The copies of one book, shared by every thread (starter).
 *
 * TODO Core 1: borrow() returns true and records a loan when a copy is
 * available, false otherwise; giveBack() returns a copy. Both must hold one
 * lock (synchronized or a ReentrantLock) around the whole check-and-act.
 */
public class Shelf {

    private final int copies;

    public Shelf(int copies) {
        this.copies = copies;
    }

    public int getCopies() {
        return copies;
    }

    public int getAvailable() {
        throw new UnsupportedOperationException("not implemented");
    }

    public int getLoansRecorded() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean borrow() {
        throw new UnsupportedOperationException("not implemented");
    }

    public void giveBack() {
        throw new UnsupportedOperationException("not implemented");
    }
}
