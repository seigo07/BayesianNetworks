
/**
 * The class for counting the number of additions and multiplies
 */
public class Counter {

    private int numberOfAdditions;
    private int numberOfMultiplies;

    public Counter() {
        this.numberOfAdditions = 0;
        this.numberOfMultiplies = 0;
    }

    public void sumAdd(int add) {
        this.numberOfAdditions += add;
    }

    public void multiAdd(int mul) {
        this.numberOfMultiplies += mul;
    }

    public int getNumberOfAdditions() {
        return numberOfAdditions;
    }

    public int getNumberOfMultiplies() {
        return numberOfMultiplies;
    }

    @Override
    public String toString() {
        return this.numberOfAdditions + "," + this.numberOfMultiplies;
    }
}
