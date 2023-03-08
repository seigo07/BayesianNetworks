import java.util.*;

/**
 * The class for the Variable Elimination Algorithm
 */
public class VariableElimination {

    /**
     * The function for Variable Elimination
     *
     * @param var       the variable which we input on console
     * @param val       the value which we input on console
     * @param evidences the evidence variables
     * @param order     the list of variable names in order for Variable Elimination part 2 task
     * @param bn        instance of BN class
     * @return the probability of the query
     */
    public static List<Double> VE(String var, String val, ArrayList<String[]> evidences, List<String> order, BN bn) {

        Variable queryVariable = bn.getVariableByName(var);
        String queryValue = val;

        List<Variable> orderedVariables = new ArrayList<>();
        if (orderedVariables != null) {
            for (String s : order) {
                orderedVariables.add(bn.getVariableByName(s));
            }
        }

//        System.out.println("queryVariable: " + queryVariable + ", queryValue: " + queryValue + ", evidences: " + evidences + ", order: " + order);

        List<String> evidenceValues = new ArrayList<>();
        List<Variable> evidenceVariables = new ArrayList<>();
        if (evidences != null && evidences.size() > 0) {
            for (String[] evidence : evidences) {
                if (evidence != null && evidence.length > 0) {
                    evidenceVariables.add(bn.getVariableByName(evidence[0]));
                    evidenceValues.add(evidence[1]);
                }
            }
        }

        // The counter for the number of addition and multiplication
        Counter counter = new Counter();

        // The factors for variables
        LinkedHashMap<String, LinkedHashMap<String, Double>> factors = new LinkedHashMap<>();

        // Adding the names of evidenceVariables to evidenceVariablesNames
        List<String> evidenceVariablesNames = new ArrayList<>();
        for (Variable variable : evidenceVariables) {
            evidenceVariablesNames.add(variable.getName());
        }

        // Adding variables to factors
        for (Variable variable : bn.getVariables()) {
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
                        return result;
                    }
                }
            }
        }

//        System.out.println("Initial Factors:");
//        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
//            System.out.println(f.getKey() + ":");
//            System.out.println(Utils.hashMapToString(f.getValue()));
//        }

        // Join factors for each ordered variable
        if (!orderedVariables.isEmpty()) {
            for (Variable h : orderedVariables) {

                // CPTs which includes the orderedVariables
                List<LinkedHashMap<String, Double>> cpts = new ArrayList<>();
                List<String> variableNames = new ArrayList<>();

                for (Map.Entry<String, LinkedHashMap<String, Double>> entry : factors.entrySet()) {
                    if (CPT.getNames(entry.getValue()).contains(h.getName())) {
                        cpts.add(entry.getValue());
                        variableNames.add(entry.getKey());
                    }
                }

                String lastName = "empty";
                if (variableNames.size() > 1) {
                    for (String name : variableNames) {
                        factors.remove(name);
                        lastName = name;
                    }
                } else if (variableNames.size() == 1) {
                    lastName = variableNames.get(0);
                }

                if (!cpts.isEmpty()) {
                    if (cpts.size() > 0) {

//                        System.out.println("Factor which joins with " + h.getName() + ":\n");
//                        for (LinkedHashMap<String, Double> cpt : cpts) {
//                            System.out.println(Utils.hashMapToString(cpt));
//                        }

                        // join cpt_to_join (all the factors that mentioning h) to one factor
                        LinkedHashMap<String, Double> newFactor = CPT.integrateFactors(cpts, counter);

//                        System.out.println("Before eliminate " + h.getName() + "\n");
//                        System.out.println("counter: " + counter);
//                        System.out.println(Utils.hashMapToString(newFactor));

                        boolean factorToAdd = true;

                        // eliminate factor
                        if (CPT.getNames(newFactor).size() > 0) {
                            newFactor = CPT.eliminate(newFactor, h, counter);
                        } else if (CPT.getNames(newFactor).size() == 1) {
                            factorToAdd = false;
                        }

//                        System.out.println("After eliminate " + h.getName() + "\n");
//                        System.out.println("counter: " + counter);
//                        System.out.println(Utils.hashMapToString(newFactor));
                        if (factorToAdd) factors.put(lastName, newFactor);
                    }
                }
            }
        }

//        System.out.println("Remaining Factors: ");
        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
//            System.out.println("Name: " + f.getKey());
//            System.out.println(Utils.hashMapToString(f.getValue()));
        }

        // Removing the factors if the size <= 1
        LinkedHashMap<String, Integer> sizes = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
            sizes.put(factor.getKey(), factor.getValue().size());
        }
        for (Map.Entry<String, Integer> factor : sizes.entrySet()) {
            if (factor.getValue() <= 1) {
                factors.remove(factor.getKey());
            }
        }

//        for (Map.Entry<String, LinkedHashMap<String, Double>> e : factors.entrySet()) {
//            System.out.println(Utils.hashMapToString(e.getValue()));
//        }

        LinkedHashMap<String, Double> lastFactor = new LinkedHashMap<>();

        // In the case of including more than one factor
        if (factors.size() > 1) {
            List<LinkedHashMap<String, Double>> factorsLeft = new ArrayList<>();
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
                factorsLeft.add(factor.getValue());
            }
            lastFactor = CPT.integrateFactors(factorsLeft, counter);
        // Getting the one left factor
        } else {
            for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
                lastFactor = new LinkedHashMap<>(factor.getValue());
//                System.out.println("last factor:");
//                System.out.println(Utils.hashMapToString(lastFactor));
                break;
            }
        }

        List<String> variableNames = CPT.getNames(lastFactor);
        if (variableNames.size() > 1) {
            variableNames.remove(queryVariable.getName());
//            System.out.println("Updated factors based on the evidence: ");
//            System.out.println(Utils.hashMapToString(lastFactor));
            for (String name : variableNames) {
//                System.out.println("Name: " + name);
                lastFactor = CPT.eliminate(lastFactor, bn.getVariableByName(name), counter);
//                System.out.println("Factors after eliminate " + name + ": ");
//                System.out.println(Utils.hashMapToString(lastFactor));
//                Double cptValue = 0.0;
//                List<String> cptCalculation = new ArrayList<>();
//                for (Map.Entry<String, Double> f : lastFactor.entrySet()) {
//                    cptValue += f.getValue();
//                    cptCalculation.add(Double.toString(f.getValue()));
//                }
//                String cptFormula = String.join(" + ", cptCalculation);
//                String cptFormula = String.join(" + \n", cptCalculation);
//                System.out.println("Sum of CPT = " + cptFormula + " = " + Math.round(cptValue));
//                System.out.println("Sum of CPT = " + cptFormula + " = " + Math.round(cptValue) + "\n");
            }
        }

        // Normalizing the lastFactor
        lastFactor = normalize(lastFactor, counter);
//        System.out.println("Probability: (after normalize)");
//        System.out.println("counter: " + counter);
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
        result.add((double) counter.getNumberOfAdditions());
//        System.out.println("The number of additions: ");
//        System.out.println(counter.getNumberOfAdditions());
        // The number of multiples
        result.add((double) counter.getNumberOfMultiplies());
//        System.out.println("The number of multiples: ");
//        System.out.println(counter.getNumberOfMultiplies());

        return result;
    }

    /**
     * Normalizing given factor
     *
     * @param factor
     * @return normalized given factor
     */
    public static LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> factor, Counter counter) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        factor = Utils.removeDuplicateValuesInKeys(factor);

//        System.out.println("Normalizable factor :");
//        System.out.println(Utils.hashMapToString(factor));

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

        counter.sumAdd(values.size() - 1);

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

}
