import java.util.*;

/**
 * The class for variable
 */
public class Variable {
    private final String name;
    private List<Variable> parents;
    private final List<String> outcomes;
    private LinkedHashMap<String, Double> cpt;
    private boolean fromChild;
    public boolean uninitialized;

    /**
     * Constructor
     *
     * @param name     the name of the variable
     * @param outcomes [T, F]
     */
    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        this.cpt = new LinkedHashMap<>();
        this.uninitialized = false;
        this.fromChild = false;
    }

    /**
     * Initialization of parents after creating the variable
     *
     * @param values  the values of outcomes
     * @param parents the variables of parents
     */
    public void initParents(double[] values, Variable[] parents) {

        this.parents = new ArrayList<>(Arrays.asList(parents));

        // No parents
        if (this.parents.size() == 0) {

            for (int i = 0; i < this.outcomes.size(); i++) {
                this.cpt.put(this.name + '=' + this.outcomes.get(i), values[i]);
            }

        // Having parents
        } else {

            List<List<String>> outcomes = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for (Variable p : this.parents) {
                outcomes.add(p.outcomes);
                names.add(p.name);
            }
            outcomes.add(this.outcomes);
            names.add(this.name);
            this.cpt = CPT.constructCPT(values, outcomes, names);

        }
        this.uninitialized = true;

    }

    /**
     * @return - parents
     */
    public List<Variable> getParents() {
        return this.parents;
    }

    /**
     * @return - the name of variable
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the origin cpt of this variable
     */
    public LinkedHashMap<String, Double> getCPT() {
        return this.cpt;
    }

    /**
     * @return the outcomes of variable
     */
    public List<String> getOutcomes() {
        return this.outcomes;
    }

    /**
     * @return whether the variable is from child or not
     */
    public boolean isFromChild() {
        return this.fromChild;
    }

    /**
     * Converting to string
     *
     * @return - string of the variable
     */
    @Override
    public String toString() {
        return "" + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(this.name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
