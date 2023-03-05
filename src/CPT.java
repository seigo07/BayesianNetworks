
import java.util.*;

/**
 * The class for building a CPT for BNs
 */
public class CPT {

    /**
     * Building a CPT by given the names, outcomes, and values of the variable
     *
     * @param values   - values of outcomes for each variable
     * @param outcomes - possible outcomes for each variable
     * @param names    - names for each variable
     * @return - .
     */
    public static LinkedHashMap<String, Double> buildCPT(double[] values, List<List<String>> outcomes, List<String> names) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

//        System.out.println("values: " + Arrays.toString(values));
        for (List<String> o : outcomes) {
//            System.out.println("outcome: " + o);
        }
//        System.out.println("names: " + names);


        String[] outputs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            outputs[i] = "";
        }

        int exp = values.length;
        for (int i = 0; i < outcomes.size(); i++) {
            List<String> o = outcomes.get(i);
            exp = exp / o.size();
            int k = 0, sum = 0;
            for (int j = 0; j < values.length; j++) {
                sum++;
                outputs[j] += o.get(k);
                if (i != outcomes.size() - 1) outputs[j] += ",";
                if (sum >= exp) {
                    k++;
                    sum = 0;
                    if (k >= o.size()) {
                        k = 0;
                    }
                }
            }
        }

        List<String> keys = new ArrayList<>();
        for (String output : outputs) {
            String[] splitKey = output.split(",");
            StringBuilder keyLine = new StringBuilder();
            for (int j = 0; j < splitKey.length; j++) {
                String key = names.get(j) + "=" + splitKey[j];
                keyLine.append(key);
                if (j != splitKey.length - 1) keyLine.append(",");
            }
            keys.add(keyLine.toString());
        }

        for (int i = 0; i < values.length; i++) {
            result.put(keys.get(i), values[i]);
        }

        return result;
    }

    /**
     * Integrating factors to one
     *
     * @param cptList the list of the factors to join
     * @return joined factor
     */
    public static LinkedHashMap<String, Double> integrateFactors(List<LinkedHashMap<String, Double>> cptList, FactorCounter factorCounter) {

        LinkedHashMap<String, Double> factor = cptList.get(0);
        List<LinkedHashMap<String, Double>> newCptList = new ArrayList<>();
        for (int i = 1; i < cptList.size(); i++) {
            newCptList.add(cptList.get(i));
        }
        return integrateFactors(newCptList, factor, factorCounter);
    }

    private static LinkedHashMap<String, Double> integrateFactors(List<LinkedHashMap<String, Double>> cptList, LinkedHashMap<String, Double> factor, FactorCounter factorCounter) {

        if (cptList.isEmpty()) return factor;

        cptList.add(factor);
        cptList = sortFactors(cptList);

        factor = cptList.get(0);
        factor = integrateTwoFactors(factor, cptList.get(1), factorCounter);
        factor = Utils.removeDuplicateValuesInKeys(factor);

        List<LinkedHashMap<String, Double>> newCptList = new ArrayList<>();
        for (int i = 2; i < cptList.size(); i++) {
            newCptList.add(cptList.get(i));
        }

        return integrateFactors(newCptList, factor, factorCounter);
    }

    /**
     * join two factors function
     *
     * @param X the first factor
     * @param Y the second factor
     * @return new factor of X and Y combined
     */
    public static LinkedHashMap<String, Double> integrateTwoFactors(LinkedHashMap<String, Double> X, LinkedHashMap<String, Double> Y, FactorCounter factorCounter) {

//        System.out.println("//////////////// JOIN //////////////////////");
//        System.out.println("X:");
//        System.out.println(Utils.hashMapToString(X));
//        System.out.println("Y:");
//        System.out.println(Utils.hashMapToString(Y));
//        System.out.println("////////////////////////////////////////////");

        HashMap<String, List<String>> xOutcomes = getNamesAndOutcomes(X);
        HashMap<String, List<String>> yOutcomes = getNamesAndOutcomes(Y);

        Set<String> xNamesSet = xOutcomes.keySet();
        List<String> xNames = new ArrayList<>(xNamesSet);
        Set<String> yNamesSet = yOutcomes.keySet();
        List<String> yNames = new ArrayList<>(yNamesSet);

        // Getting the names of the variables that the factor will include
        List<String> xyNamesIntersection = Utils.intersection(xNames, yNames);
//        System.out.println("intersection: " + xyNamesIntersection);

        // Integrating a factor into result
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<String, Double> y : Y.entrySet()) {
            LinkedHashMap<String, String> valuesOfLine = Utils.splitKeys(y.getKey());
            List<String> intersectionVariablesValues = new ArrayList<>();

            for (String name : xyNamesIntersection) {
                intersectionVariablesValues.add(name + "=" + valuesOfLine.get(name));
            }
            for (Map.Entry<String, Double> x : X.entrySet()) {
                boolean b = true;

                for (String name : intersectionVariablesValues) {
                    if (!x.getKey().contains(name)) {
                        b = false;
                        break;
                    }
                }
                if (b) {

                    double u = y.getValue();
                    double v = x.getValue();
                    double r = u * v;

                    String[] ySplit = y.getKey().split(",");
                    List<String> ySplitList = new ArrayList<>();
                    Collections.addAll(ySplitList, ySplit);

                    String[] xSplit = x.getKey().split(",");
                    List<String> xSplitList = new ArrayList<>();
                    Collections.addAll(xSplitList, xSplit);

                    List<String> newKeySplit = Utils.union(xSplitList, ySplitList);
                    Collections.sort(newKeySplit);
                    String new_key = Utils.combineWithCommas(newKeySplit);
                    result.put(new_key, r);
                }
            }
        }

//        System.out.println("\nRESULT AFTER JOIN:");
//        System.out.println(Utils.hashMapToString(result));
//        System.out.println();

        factorCounter.mulAdd(result.size());

        return result;
    }

    /**
     * Getting names and outcomes
     *
     * @param cpt
     * @return outcomes
     */
    public static LinkedHashMap<String, List<String>> getNamesAndOutcomes(LinkedHashMap<String, Double> cpt) {

        LinkedHashMap<String, List<String>> outcomes = new LinkedHashMap<>();

        List<String> names = new ArrayList<>();
        for (Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> lineSplit = Utils.splitKeys(line.getKey());
            for (Map.Entry<String, String> inner : lineSplit.entrySet()) {
                names.add(inner.getKey());
            }
            break;
        }

        for (String name : names) {
            outcomes.put(name, new ArrayList<>());
        }

        for (Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> lineSplit = Utils.splitKeys(line.getKey());
            for (Map.Entry<String, String> inner : lineSplit.entrySet()) {
                if (!outcomes.get(inner.getKey()).contains(inner.getValue())) {
                    outcomes.get(inner.getKey()).add(inner.getValue());
                }
            }
        }
        return outcomes;
    }

    /**
     * Getting names of variables
     *
     * @param factor
     * @return names
     */
    public static List<String> getNames(LinkedHashMap<String, Double> factor) {
        List<String> names = new ArrayList<>();
        LinkedHashMap<String, List<String>> namesAndOutcomes = getNamesAndOutcomes(factor);
        for (Map.Entry<String, List<String>> entry : namesAndOutcomes.entrySet()) {
            names.add(entry.getKey());
        }
        return names;
    }

    /**
     * Eliminating variables from the given factor
     *
     * @param factor    the given factor
     * @param variables the variable
     * @return the new factor eliminated from the variables
     */
    public static LinkedHashMap<String, Double> eliminate(LinkedHashMap<String, Double> factor, Variable variables, FactorCounter factorCounter) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        List<String> names = getNames(factor);

        if (names.size() <= 1) return result;

        // Building list with all the outcomes
        List<String> outcomes = variables.getOutcomes();
        List<String> values = new ArrayList<>();
        for (String outcome : outcomes) {
            values.add(variables.getName() + "=" + outcome);
        }

        for (Map.Entry<String, Double> y : factor.entrySet()) {
            for (String value : values) {
                if (y.getKey().contains(value)) {

                    // Building the new key without the value
                    List<String> splitNewKey = new ArrayList<>(Arrays.asList(y.getKey().split(",")));
                    splitNewKey.remove(value);
                    String new_key = Utils.combineWithCommas(splitNewKey);

                    for (Map.Entry<String, Double> x : factor.entrySet()) {

                        boolean b = true;
                        List<String> newKeyValues = Utils.separateByCommas(new_key);
                        for (String newKeyValue : newKeyValues) {
                            if (!x.getKey().contains(newKeyValue)) {
                                b = false;
                                break;
                            }
                        }

                        if (x.getKey().equals(y.getKey())) {
                            b = false;
                        }

                        if (b) {
                            double u = y.getValue();
                            double v = x.getValue();
                            double r = u + v;

                            if (!result.containsKey(new_key)) {
                                factorCounter.sumAdd(1);
                                result.put(new_key, r);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Sorting factors from the smallest to the biggest
     *
     * @param factors
     * @return sorted factors
     */
    public static List<LinkedHashMap<String, Double>> sortFactors(List<LinkedHashMap<String, Double>> factors) {

        ArrayList<LinkedHashMap<String, Double>> sortedFactors = new ArrayList<>();
        for (int i = 0; i < factors.size(); i++) {
            sortedFactors.add(i,factors.get(i));
        }

        // Sorting by bubble sort algorithm
        for (int i = 0; i < sortedFactors.size(); i++) {
            for (int j = 0; j < sortedFactors.size() - 1; j++) {
                if (CPTCompare(sortedFactors.get(j), sortedFactors.get(j + 1))) {
                    // swap factors
                    LinkedHashMap<String, Double> temp = sortedFactors.get(j);
                    sortedFactors.set(j,sortedFactors.get(j+1));
                    sortedFactors.set(j + 1,temp);

                }
            }
        }

        return sortedFactors;
    }

    /**
     * @param X first factor
     * @param Y second factor
     * @return true or false if we want to swap between X and Y
     */
    private static boolean CPTCompare(LinkedHashMap<String, Double> X, LinkedHashMap<String, Double> Y) {
        if (X.size() < Y.size()) {
            return false;
        } else if (X.size() > Y.size()) {
            return true;
        } else {
            // compare by ASCII values
            List<String> xNamesList = getNames(X);
            List<String> yNamesList = getNames(Y);

            int xNamesAscii = 0;
            for (String name : xNamesList) {
                for (int i = 0; i < name.length(); i++) {
                    xNamesAscii += name.charAt(i);
                }
            }
            int yNamesAscii = 0;
            for (String name : yNamesList) {
                for (int i = 0; i < name.length(); i++) {
                    yNamesAscii += name.charAt(i);
                }
            }
            return xNamesAscii >= yNamesAscii;
        }
    }

    /**
     * Deleting the unrequited values by evidence and return the new factor
     *
     * @param evidenceList list of the evidence variable
     * @param valueList    list of the values of the evidence variables
     * @param factor       the factor we eliminate the evidence values
     * @return result
     */
    public static LinkedHashMap<String, Double> updateCPT(List<String> evidenceList, List<String> valueList, LinkedHashMap<String, Double> factor) {

        List<String> variablesNames = CPT.getNames(factor);
        List<String> evidenceVariables = Utils.intersection(variablesNames, evidenceList);

        List<String> evidences = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (int i = 0; i < evidenceList.size(); i++) {
            String name = evidenceList.get(i);
            if (evidenceVariables.contains(name)) {
                evidences.add(evidenceList.get(i));
                values.add(valueList.get(i));
            }
        }

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            boolean b = true;
            for (int i = 0; i < evidences.size(); i++) {
                StringBuilder evidenceValue = new StringBuilder();
                evidenceValue.append(evidences.get(i)).append("=").append(values.get(i));
                b &= entry.getKey().contains(evidenceValue);
            }
            if (b) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }
}
