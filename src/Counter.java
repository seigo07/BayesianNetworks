
/**
 * The class for counting the number of additions and multiples
 */
public class Counter {

    private int sumCount;
    private int multiCount;

    public Counter() {
        this.sumCount = 0;
        this.multiCount = 0;
    }

    public void sumAdd(int add) {
        this.sumCount += add;
    }

    public void multiAdd(int mul) {
        this.multiCount += mul;
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getMultiCount() {
        return multiCount;
    }

    @Override
    public String toString() {
        return this.sumCount + "," + this.multiCount;
    }
}
