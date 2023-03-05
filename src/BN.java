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
     * bayes ball algorithm using BFS algorithm
     *
     * @param startNode       the starting variable name position of the BFS algorithm
     * @param destinationNode the target variable which is the algorithm is searching for
     * @param nodesNames      evidence variables in the query
     * @return true if the startNode and the destinationNode are independents
     */
    public boolean bayesBall(String startNode, String destinationNode, List<String> nodesNames) {
        List<Variable> evidencesVariables = new ArrayList<>();
        if (nodesNames != null) {
            for (String name : nodesNames) {
                evidencesVariables.add(this.getVariableByName(name));
            }
        }
        Variable startVariable = getVariableByName(startNode);
        Variable destinationVariable = getVariableByName(destinationNode);

        if (startVariable == null || destinationVariable == null) return true;
        if (startVariable.equals(destinationVariable)) return false;
        LinkedHashMap<Variable, Visited> visited = new LinkedHashMap<>();

        for (Variable variable : this.variables) {
            variable.setShade(evidencesVariables.contains(variable));
            variable.setFromChild(false);
            visited.put(variable, Visited.NO);
        }

        visited.put(startVariable, Visited.YES);
        Queue<Variable> queue = new LinkedList<>();
        queue.add(startVariable);

        while (!queue.isEmpty()) {
            Variable v = queue.poll();
//            System.out.println("poll: " + v + ", neighbors: " + getNeighbors(v));
            for (Variable u : getNeighborVariables(v)) {
//                System.out.println("V: " + v + ", U: " + u);
                // found destination
                if (u.equals(destinationVariable)) {
//                    System.out.println("found " + destinationVariable);
                    return false;
                }

                // if u is parent of v
                if (this.parents.get(v.getName()).contains(u)) {
                    queue.add(u);
                    u.setFromChild(true);
//                    System.out.println(u + " is parent of " + v);
                    // u is child of v
                } else if (visited.get(u) == Visited.NO) {
                    queue.add(u);
                    visited.put(u, Visited.YES);
                    u.setFromChild(false);
//                    System.out.println(u + " is child of " + v);

                    // found evidence variable
                    if (u.isShaded()) {
                        u.setFromChild(true);
                        if (v.isShaded()) {
                            u.setFromChild(false);
                        }
//                        System.out.println(u + " is evidence");
                    }
                }
            }
        }
        return true;
    }

    private List<Variable> getNeighborVariables(Variable variable) {
        List<Variable> neighborVariables = new ArrayList<>(this.children.get(variable.getName()));
        if (variable.isFromChild()) {
            neighborVariables.addAll(this.parents.get(variable.getName()));
        }
        return neighborVariables;
    }

    /**
     * The function for Variable Elimination
     *
     * @param var       the variable which we input on console
     * @param val       the value which we input on console
     * @param evidences the evidence variables
     * @param order     the list of variable names in order for Variable Elimination part 2 task
     * @return the probability of the query
     */
    public List<Double> VE(String var, String val, ArrayList<String[]> evidences, List<String> order) {

        Variable queryVariable = getVariableByName(var);
        String queryValue = val;

        List<String> evidenceValues = new ArrayList<>();
        List<Variable> evidenceVariables = new ArrayList<>();
        if (evidences != null && evidences.size() > 0) {
            for (String[] evidence : evidences) {
                if (evidence != null && evidence.length > 0) {
                    evidenceVariables.add(getVariableByName(evidence[0]));
                    evidenceValues.add(evidence[1]);
                }
            }
        }

//        System.out.println("queryVariable: " + queryVariable + ", queryValue: " + queryValue + ", evidenceVariables: " + evidenceVariables + ", evidenceValues: " + evidenceValues);

        // This counter counts the number of addition and multiplication operations in Variable Elimination
        FactorCounter factorCounter = new FactorCounter();

        // The hashmap of factors for each variable
        LinkedHashMap<String, LinkedHashMap<String, Double>> factors = new LinkedHashMap<>();

        // Adding the names of evidenceVariables to evidenceVariablesNames
        List<String> evidenceVariablesNames = new ArrayList<>();
        for (Variable variable : evidenceVariables) {
            evidenceVariablesNames.add(variable.getName());
        }

        // Adding variables to factors
        for (Variable variable : this.variables) {
            factors.put(variable.getName(), CPT.updateCPT(evidenceVariablesNames, evidenceValues, variable.getCPT()));
        }

        // Checking whether one of the factors includes the necessary value of queryVariable alone
        if (evidenceVariables.isEmpty()) {
            for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                for (Map.Entry<String, Double> line : f.getValue().entrySet()) {
                    if (line.getKey().equals(queryValue)) {
                        List<Double> result = new ArrayList<>();
                        result.add(line.getValue());
                        result.add(0.0);
                        result.add(0.0);
//                        System.out.println("FINAL VALUE IS " + line.getValue());
                        return result;
                    }
                }
            }
        }

//        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//        System.out.println("FACTORS ADDED:");
        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
//            System.out.println(f.getKey() + ":");
//            System.out.println(Utils.hashMapToString(f.getValue()));
        }
//        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

//        System.out.println();
//        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//        System.out.println("PRINT CURRENT FACTORS:");
        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
//            System.out.println("NAME: " + f.getKey());
//            System.out.println(Utils.hashMapToString(f.getValue()));
        }
//        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

        // removing the factors with size of one or less
        LinkedHashMap<String, Integer> sizes = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
            sizes.put(factor.getKey(), factor.getValue().size());
        }

        for (Map.Entry<String, Integer> factor : sizes.entrySet()) {
            if (factor.getValue() <= 1) {
                factors.remove(factor.getKey());
            }
        }

        // eliminate again if still the factor contains other different outcomes of
//        System.out.println("-------------------------------BEFORE END PRINT FACTORS----------------------------------");
        for (Map.Entry<String, LinkedHashMap<String, Double>> e : factors.entrySet()) {
//            System.out.println("factor for " + e.getKey() + ", is:");
//            System.out.println(Utils.hashMapToString(e.getValue()));
        }
//        System.out.println("-----------------------------------------------------------------------------------------");

        LinkedHashMap<String, Double> lastFactor = new LinkedHashMap<>();

        // If the factors include more than one factor
        if (factors.size() > 1) {
            List<LinkedHashMap<String, Double>> factorsLeft = new ArrayList<>();
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
                factorsLeft.add(factor.getValue());
            }
            lastFactor = CPT.integrateFactors(factorsLeft, factorCounter);
            // Getting the one left factor
        } else {
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
                lastFactor = new LinkedHashMap<>(factor.getValue());
//                System.out.println("LAST FACTOR:");
//                System.out.println(Utils.hashMapToString(lastFactor));
                break;
            }
        }

        // Set variable elimination order for Part 2 if order is passed.
        // If not, set an automatic order for Part 3.
        List<String> variableNames = order.isEmpty() ? CPT.getNames(lastFactor) : order;
        if (variableNames.size() > 1) {
            variableNames.remove(queryVariable.getName());
//            System.out.println("initial lastFactor: " + lastFactor);
            for (String name : variableNames) {
//                System.out.println("name: " + name);
                lastFactor = CPT.eliminate(lastFactor, getVariableByName(name), factorCounter);
//                System.out.println("lastFactor: " + lastFactor);
            }
        }

        // Normalizing the lastFactor
        lastFactor = normalize(lastFactor, factorCounter);
//        System.out.println("lastFactor: (after normalize)");
//        System.out.println("factorCounter: " + factorCounter);
//        System.out.println(Utils.hashMapToString(lastFactor));

        double probability = 0.0;
        for (Map.Entry<String, Double> factor : lastFactor.entrySet()) {
            if (factor.getKey().contains(queryValue)) {
                probability = factor.getValue();
                break;
            }
        }

//        System.out.println("FINAL VALUE IS " + val);

        List<Double> result = new ArrayList<>();
        // The probability for given variable
        result.add(probability);
        // The number of additions
        result.add((double) factorCounter.getSumCount());
        // The number of multiples
        result.add((double) factorCounter.getMulCount());

        return result;
    }

    /**
     * Normalizing given factor
     *
     * @param factor
     * @return normalized given factor
     */
    public LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> factor, FactorCounter factorCounter) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        factor = Utils.removeDuplicateValuesInKeys(factor);

//        System.out.println("=======================================================================================");
//        System.out.println("Factor to Normalize:");
//        System.out.println(Utils.hashMapToString(factor));
//        System.out.println("=======================================================================================");

        LinkedHashMap<String, List<String>> outcomes = CPT.getNamesAndOutcomes(factor);
        String variableName = "";
        for (Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
            variableName = entry.getKey();
        }
        List<String> variableOutcomes = outcomes.get(variableName);
        List<String> variableOutcomesKeys = new ArrayList<>();
        for (String outcome : variableOutcomes) {
            String value = variableName + "=" + outcome;
            variableOutcomesKeys.add(value);
        }
        List<Double> values = new ArrayList<>();

        for (String outcome : variableOutcomesKeys) {
            values.add(factor.get(outcome));
        }

        factorCounter.sumAdd(values.size() - 1);

        double exp = 0.0;
        for (Double value : values) {
            exp += value;
        }
        exp = 1 / exp;
        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            result.put(entry.getKey(), entry.getValue() * exp);
        }
        return result;
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
