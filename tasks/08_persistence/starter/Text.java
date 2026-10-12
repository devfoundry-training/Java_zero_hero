/** Small text helpers shared by the whole program. */
public final class Text {

    private Text() {
        // static members only: no objects of this class
    }

    /** "1 loan" or "3 loans": the number and the right noun. */
    public static String plural(int count, String singular, String many) {
        return count + " " + (count == 1 ? singular : many);
    }

    /** Whole pence to pounds: 125 is "£1.25". Integer division and remainder only, never a double. */
    public static String money(int pence) {
        return String.format("£%d.%02d", pence / 100, pence % 100);
    }
}
