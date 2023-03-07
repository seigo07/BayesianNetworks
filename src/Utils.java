import java.util.*;

/**
 * The helper class
 */

public class Utils {

    /**
     * Returning a string of a given hashMap (for printing)
     *
     * @param hashmap
     * @return hashmap to string
     */
    public static <K, V> String hashMapToString(LinkedHashMap<K, V> hashmap) {
        if (hashmap.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        hashmap.forEach((key, value) -> {
            output.append(key);
            output.append(" : ");
            output.append(value);
            output.append("\n");
        });
        return output.toString();
    }

    /**
     * Splitting keys into variables and outcomes
     * e.g. "A=T,B=F" to variables ("A", "B", "C") and the outcomes ("T", "F")
     *
     * @param keys
     * @return hashmap for variables and outcomes
     */
    public static LinkedHashMap<String, String> splitKeys(String keys) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        String[] splitKeys = keys.split(",");
        for (String key : splitKeys) {
            String[] splitKey = key.split("=");
            result.put(splitKey[0], splitKey[1]);
        }
        return result;
    }

    /**
     * @param X   list of elements
     * @param Y   list of elements
     * @param <T> some value that X and Y are fill with
     * @return union of X and Y
     */
    public static <T> List<T> union(List<T> X, List<T> Y) {
        Set<T> result = new HashSet<>();
        result.addAll(X);
        result.addAll(Y);
        return new ArrayList<>(result);
    }

    /**
     * @param X   list of elements
     * @param Y   list of elements
     * @param <T> some value that X and Y are fill with
     * @return intersection of X and Y
     */
    public static <T> List<T> intersection(List<T> X, List<T> Y) {
        List<T> result = new ArrayList<>();
        if (X.isEmpty() && Y.isEmpty()) return result;
        else if (X.isEmpty()) return Y;
        else if (Y.isEmpty()) return X;
        else for (T x : X) if (Y.contains(x)) result.add(x);
        return result;
    }

    /**
     * @param strings
     * @return list of strings seperated by commas
     */
    public static String combineWithCommas(List<String> strings) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            result.append(strings.get(i));
            if (i != strings.size() - 1) result.append(",");
        }
        return result.toString();
    }

    /**
     * @param string
     * @return list of strings split by commas
     */
    public static List<String> separateByCommas(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    /**
     * Removing duplicate values in keys
     *
     * @param factor
     * @return output factor removed duplicate values
     */
    public static LinkedHashMap<String, Double> removeDuplicateValuesInKeys(LinkedHashMap<String, Double> factor) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        if (factor.size() == 1 && CPT.getNames(factor).size() == 1) {
            return factor;
        }

        LinkedHashMap<String, List<String>> outcomes = CPT.getNamesAndOutcomes(factor);

        if (outcomes.size() == 0) {
            return result;
        }

        if (outcomes.size() == 1) {
            for (Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
                if (entry.getValue().size() == 1) {
                    return result;
                }
            }
        }

        // list with the un welcome values - that we want to delete
        List<String> unWelcomeValues = new ArrayList<>();

        // for each outcome in outcomes if outcome of a values has only one values - we want to delete it
        for (Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
            if (entry.getValue().size() == 1) {
                String value = entry.getKey() + "=" + entry.getValue().get(0);
                unWelcomeValues.add(value);
            }
        }

        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            StringBuilder newKey = new StringBuilder();
            List<String> newKeySplit = separateByCommas(entry.getKey());

            for (String key : newKeySplit) {

                if (!unWelcomeValues.contains(key)) {
                    newKey.append(key).append(",");
                }
            }
            result.put(newKey.substring(0, newKey.length() - 1), entry.getValue());
        }

        return result;
    }

}
