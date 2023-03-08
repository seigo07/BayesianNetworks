import java.util.*;

/**
 * The class for BN
 */
public class BN {

    private final List<Variable> variables;

    private final LinkedHashMap<String, List<Variable>> parents;

    private final LinkedHashMap<String, List<Variable>> children;

    private static final List<Variable> empties = new ArrayList<>();

    /**
     * Constructor
     *
     * @param variables
     */
    public BN(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        this.parents = new LinkedHashMap<>();
        this.children = new LinkedHashMap<>();
        initParentsAndChildren();
    }

    /**
     * Initialization parents and children
     */
    private void initParentsAndChildren() {

        for (Variable variable : this.variables) {

            List<Variable> variableParents = variable.getParents();

            if (variableParents != null) {

                // Adding parents to current variable
                this.parents.put(variable.getName(), variableParents);

                // Adding children for each parent of current variable
                for (Variable parent : variableParents) {

                    // Adding current variable to children if current parent already has child
                    if (this.children.containsKey(parent.getName())) {
                        this.children.get(parent.getName()).add(variable);
                        // Adding current variable to the parent if current parent doesn't have children
                    } else {
                        List<Variable> variablelist = new ArrayList<>();
                        variablelist.add(variable);
                        this.children.put(parent.getName(), variablelist);
                    }
                }
            }

        }

        // Fixing variables hashmaps
        for (Variable variable : this.variables) {
            if (!this.parents.containsKey(variable.getName())) {
                this.parents.put(variable.getName(), empties);
            }
            if (!this.children.containsKey(variable.getName())) {
                this.children.put(variable.getName(), empties);
            }
        }
    }

    /**
     * @return - variables
     */
    public List<Variable> getVariables() {
        return this.variables;
    }

    /**
     * @return - parents
     */
    public LinkedHashMap<String, List<Variable>> getParents() {
        return this.parents;
    }


    /**
     * @return - The number of variables
     */
    public int getVariablesSize() {
        return this.variables.size();
    }

    /**
     * Returning a variable by its name
     *
     * @param name
     * @return variable
     */
    public Variable getVariableByName(String name) {
        for (int i = 0; i < this.getVariablesSize(); i++) {
            Variable variable = this.variables.get(i);
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    /**
     * Converting to string
     *
     * @return string represents the BNs, print each CPT of the variables
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("TO STRING BNs:\n");
        for (Variable variable : this.variables) {
            result.append(variable.getName()).append(":\n").append(Utils.hashMapToString(variable.getCPT()));
        }
        return result.toString();
    }
}
