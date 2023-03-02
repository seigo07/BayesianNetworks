import java.util.*;

/**
 * this class represents a Variable in a
 */
public class Variable {
    private final String name;
    private List<Variable> parents;
    private final List<String> outcomes;
    private LinkedHashMap<String, Double> cpt;
    private boolean shaded;
    private boolean fromChild;
    public boolean uninitialized;

    /**
     * constructor to create a variables without initialize parents and values
     *
     * @param name     the name of the variable (for instance "A", "E", "VariableA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     */
    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        this.cpt = new LinkedHashMap<>();
        this.shaded = false;
        this.uninitialized = false;
        this.fromChild = false;
    }

    /**
     * initialize parents after creating the variable
     *
     * @param values  outcomes values
     * @param parents variable parents
     */
    public void initialize_parents(double[] values, Variable[] parents) {

        this.parents = new ArrayList<>(Arrays.asList(parents));

        // do not have parents
        if (this.parents.size() == 0) {

            for (int i = 0; i < this.outcomes.size(); i++) {
                this.cpt.put(this.name + '=' + this.outcomes.get(i), values[i]);
            }

            // have parents
        } else {

            List<List<String>> all_outcomes = new ArrayList<>();
            List<String> all_names = new ArrayList<>();

            for (Variable p : this.parents) {
                all_outcomes.add(p.outcomes);
                all_names.add(p.name);
            }
            all_outcomes.add(this.outcomes);
            all_names.add(this.name);
            this.cpt = CPTBuilder.buildCPTLinkedHashMap(values, all_outcomes, all_names);

        }
        this.uninitialized = true;

    }

    /**
     * @return - parents list
     */
    public List<Variable> getParents() {
        return this.parents;
    }

    /**
     * @return - true if variables has parents, else return false
     */
    public boolean hasParents() {
        return this.parents.size() > 0;
    }

    public String getName() {
        return this.name;
    }

    /**
     * set shaded - using for the bayes-ball algorithm
     *
     * @param shaded - true or false
     */
    public void setShade(boolean shaded) {
        this.shaded = shaded;
    }

    /**
     * @return - shaded status
     */
    public boolean isShaded() {
        return this.shaded;
    }

    /**
     * @return the origin cpt of this variable
     */
    public LinkedHashMap<String, Double> getCPT() {
        return this.cpt;
    }

    /**
     * @return variable outcomes
     */
    public List<String> getOutcomes() {
        return this.outcomes;
    }

    /**
     * @param parent_check the variable we go throw parents and check if he is one of them
     * @return true of and only if parent_check is a parent or grandparent of this variable
     */
    public boolean isGrandParent(Variable parent_check) {
        return isGrandParent(parent_check, this);
    }

    private boolean isGrandParent(Variable parent_check, Variable current) {
        if (current.getName().equals(parent_check.getName())) return true;
        if (!current.hasParents()) return false;
        for (Variable parent : current.parents) {
            if (isGrandParent(parent_check, parent)) return true;
        }
        return false;
    }

    public boolean isFromChild() {
        return this.fromChild;
    }

    public void setFromChild(boolean fromParents) {
        this.fromChild = fromParents;
    }

    /**
     * to string method
     *
     * @return - string represents the variable
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
