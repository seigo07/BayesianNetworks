public class FactorCounter {

    private int sumCount;
    private int mulCount;

    public FactorCounter() {
        this.sumCount = 0;
        this.mulCount = 0;
    }

    public void sumAdd(int add) {
        this.sumCount += add;
    }

    public void mulAdd(int mul) {
        this.mulCount += mul;
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getMulCount() {
        return mulCount;
    }

    @Override
    public String toString() {
        return this.sumCount + "," + this.mulCount;
    }
}
