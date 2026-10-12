import java.util.ArrayList;
import java.util.List;

/**
 * A library member (starter).
 *
 * TODO Core 2 and 3: the fields id, name, email, type and joined, validation
 * (type must be standard, student or staff), getters, the list of book ids on
 * loan and toString().
 */
public class Member {

    private final int id;
    private final List<Integer> loans = new ArrayList<>();

    public Member(int id, String name, String email, String type, String joined) {
        // TODO validate, then keep every field
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getLoans() {
        return List.copyOf(loans);
    }

    /** "#ID NAME (TYPE), N loans" ("1 loan" for one) */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("not implemented");
    }
}
