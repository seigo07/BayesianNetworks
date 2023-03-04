import java.util.*;

/**
 * The class for BNs
 */
public class BNs {

    private final List<Variable> variables;

    private final LinkedHashMap<String, List<Variable>> parents;

    private final LinkedHashMap<String, List<Variable>> children;

    private static final List<Variable> emptyList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param variables
     */
    public BNs(List<Variable> variables) {
        this.variables = new ArrayList<>();
        this.variables.addAll(variables);
        this.parents = new LinkedHashMap<>();
        this.children = new LinkedHashMap<>();
        initializeParentsAndChildren();
    }

    /**
     * Initialization parents and children
     */
    private void initializeParentsAndChildren() {

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
                this.parents.put(variable.getName(), emptyList);
            }
            if (!this.children.containsKey(variable.getName())) {
                this.children.put(variable.getName(), emptyList);
            }
        }
    }

    /**
     * @return - number of variables in the network - |V|
     */
    public int size() {
        return this.variables.size();
    }

    /**
     * getting a variable by his given name
     *
     * @param name the name of the variable
     * @return the variable
     */
    public Variable getVariableByName(String name) {
        for (int i = 0; i < this.size(); i++) {
            Variable variable = this.variables.get(i);
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    /**
     * bayes ball algorithm using BFS algorithm
     * return true if and only if the start_node and the destination_node are independents
     * else, return false
     *
     * @param start_node            starting variable name position of the BFS algorithm
     * @param destination_node      the variable the algorithm is searching for
     * @param evidences_nodes_names evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    public boolean bayes_ball(String start_node, String destination_node, List<String> evidences_nodes_names) {
        List<Variable> evidences_nodes = new ArrayList<>();
        if (evidences_nodes_names != null) {
            for (String name : evidences_nodes_names) {
                evidences_nodes.add(this.getVariableByName(name));
            }
        }
        return bayes_ball(getVariableByName(start_node), getVariableByName(destination_node), evidences_nodes);
    }

    /**
     * @param start_node       starting variable position of the BFS algorithm
     * @param destination_node the variable the algorithm is searching for
     * @param evidences_nodes  evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    private boolean bayes_ball(Variable start_node, Variable destination_node, List<Variable> evidences_nodes) {

        if (start_node == null || destination_node == null) return true;
        if (start_node.equals(destination_node)) return false;
        LinkedHashMap<Variable, Visited> visited = new LinkedHashMap<>();

        for (Variable variable : this.variables) {
            variable.setShade(evidences_nodes.contains(variable));
            variable.setFromChild(false);
            visited.put(variable, Visited.NO);
        }

        visited.put(start_node, Visited.YES);
        Queue<Variable> queue = new LinkedList<>();
        queue.add(start_node);

        while (!queue.isEmpty()) {
            Variable v = queue.poll();
//            System.out.println("poll: " + v + ", neighbors: " + getNeighbors(v));
            for (Variable u : getNeighbors(v)) {

//                System.out.println("V: " + v + ", U: " + u);

                // found destination
                if (u.equals(destination_node)) {
//                    System.out.println("found " + destination_node);
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


    private List<Variable> getNeighbors(Variable variable) {
        List<Variable> neighbors = new ArrayList<>(this.children.get(variable.getName()));
        if (variable.isFromChild()) {
            neighbors.addAll(this.parents.get(variable.getName()));
        }
        return neighbors;
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
    public List<Double> variableElimination(String var, String val, ArrayList<String[]> evidences, List<String> order) {

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
            factors.put(variable.getName(), updateLocalCpt(evidenceVariablesNames, evidenceValues, variable.getCPT()));
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
//            System.out.println(UtilFunctions.hashMapToString(f.getValue()));
        }
//        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

//        System.out.println();
//        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//        System.out.println("PRINT CURRENT FACTORS:");
        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
//            System.out.println("NAME: " + f.getKey());
//            System.out.println(UtilFunctions.hashMapToString(f.getValue()));
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
//            System.out.println(UtilFunctions.hashMapToString(e.getValue()));
        }
//        System.out.println("-----------------------------------------------------------------------------------------");

        LinkedHashMap<String, Double> lastFactor = new LinkedHashMap<>();

        // If the factors include more than one factor
        if (factors.size() > 1) {
            List<LinkedHashMap<String, Double>> factorsLeft = new ArrayList<>();
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor: factors.entrySet()) {
                factorsLeft.add(factor.getValue());
            }
            lastFactor = CPTBuilder.joinFactors(factorsLeft, factorCounter);
        // Getting the one left factor
        } else {
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor: factors.entrySet()) {
                lastFactor = new LinkedHashMap<>(factor.getValue());
//                System.out.println("LAST FACTOR:");
//                System.out.println(UtilFunctions.hashMapToString(last_factor));
                break;
            }
        }

        // if the last factor contains values for more than one variable - eliminate again with those variables
//        List<Variable> orderedVariables = new ArrayList<>();
//        for (String name : order) {
//            orderedVariables.add(getVariableByName(name));
//        }
//        for (Variable variable : orderedVariables) {

        // Set variable elimination order for Part 2 if order is passed.
        // If not, set an automatic order for Part 3.
        List<String> names_in_last_factor = order.isEmpty() ? CPTBuilder.getNames(lastFactor) : order;
        if (names_in_last_factor.size() > 1) {
            names_in_last_factor.remove(queryVariable.getName());
            for (String name : names_in_last_factor) {
                lastFactor = CPTBuilder.eliminate(lastFactor, getVariableByName(name), factorCounter);
            }
        }

        // normalize the final_factor
        lastFactor = normalize(lastFactor, factorCounter);
//        System.out.println("final_factor: (after normalize)");
//        System.out.println("factorCounter: " + factorCounter);
//        System.out.println(UtilFunctions.hashMapToString(last_factor));

        double value = 0.0;
        for (Map.Entry<String, Double> entry : lastFactor.entrySet()) {
            if (entry.getKey().contains(queryValue)) {
                value = entry.getValue();
                break;
            }
        }

//        System.out.println("FINAL VALUE IS " + value);

        // result list
        List<Double> result = new ArrayList<>();

        // final value
        result.add(value);

        // number of Additions
        result.add((double) factorCounter.getSumCount());

        // number of multiples
        result.add((double) factorCounter.getMulCount());

        return result;
    }

    /**
     * this function get a hidden variable with all the evidence variables and their values we get in the variable elimination function
     * and return the new factor for the hidden variable
     * (deleting the unrequited values by evidence)
     * for example if we have the evidence A=T, and the hidden variable B factor contains B values and A values we delete all the A=F values from the factor
     *
     * @param evidence list of the evidence variable
     * @param values   list of the values of the evidence variables
     * @param factor   the factor we eliminate the evidence values
     * @return the new factor of hidden
     */
    public static LinkedHashMap<String, Double> updateLocalCpt(List<String> evidence, List<String> values, LinkedHashMap<String, Double> factor) {

        List<String> variables_name_in_factor = CPTBuilder.getNames(factor);
        List<String> evidence_variable_in_factor = UtilFunctions.intersection(variables_name_in_factor, evidence);

        List<String> relevant_evidence = new ArrayList<>();
        List<String> relevant_values = new ArrayList<>();

        for (int i = 0; i < evidence.size(); i++) {
            String name = evidence.get(i);
            if (evidence_variable_in_factor.contains(name)) {
                relevant_evidence.add(evidence.get(i));
                relevant_values.add(values.get(i));
            }
        }

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            boolean b = true;
            for (int i = 0; i < relevant_evidence.size(); i++) {
                StringBuilder evidence_value = new StringBuilder();
                evidence_value.append(relevant_evidence.get(i)).append("=").append(relevant_values.get(i));
                b &= entry.getKey().contains(evidence_value);
            }
            if (b) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * this function get a factor (assuming with one variable) and return it normalized
     * for example if the input factor is:
     * B=T 0.00059224259
     * B=F 0.00149185665
     * the output factor will be:
     * B=T 0.284171971
     * B=F 0.715828028
     * By Calculating:
     * exp = 1 / 0.00059224259 + 0.00149185665 = 479.8236
     * 0.00059224259 * 479.8236 = 0.2841719716
     * 0.00149185665 * 479.8236 = 0.7158280285
     *
     * @param factor input factor
     * @return normalized given factor
     */
    public LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> factor, FactorCounter factorCounter) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        factor = UtilFunctions.fixingDuplicatesValuesInKeys(factor);

//        System.out.println("=======================================================================================");
//        System.out.println("Factor to Normalize:");
//        System.out.println(UtilFunctions.hashMapToString(factor));
//        System.out.println("=======================================================================================");

        LinkedHashMap<String, List<String>> outcomes = CPTBuilder.getNamesAndOutcomes(factor);
        String variable_name = "";
        for (Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
            variable_name = entry.getKey();
        }
        List<String> variable_outcomes = outcomes.get(variable_name);
        List<String> variable_outcomes_keys = new ArrayList<>();
        for (String outcome : variable_outcomes) {
            String value = variable_name + "=" + outcome;
            variable_outcomes_keys.add(value);
        }
        List<Double> values = new ArrayList<>();

        for (String outcome : variable_outcomes_keys) {
            values.add(factor.get(outcome));
        }

        // number of values we added less one is the number of addition operations
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
     * to string function
     *
     * @return string represents the network, print each CPT of the variables
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("TO STRING NETWORK:\n");
        for (Variable variable : this.variables) {
            result.append(variable.getName()).append(":\n").append(UtilFunctions.hashMapToString(variable.getCPT()));
        }
        return result.toString();
    }

    public void print_childes_parents() {
//        System.out.println("childes are:");
//        System.out.println(UtilFunctions.hashMapToString(this.childes));
//        System.out.println("parents are:");
//        System.out.println(UtilFunctions.hashMapToString(this.parents));
    }
}
