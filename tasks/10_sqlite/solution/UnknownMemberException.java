/** There is no member with that id. */
public class UnknownMemberException extends LibraryException {

    public UnknownMemberException(int memberId) {
        super("no member with id " + memberId);
    }
}
