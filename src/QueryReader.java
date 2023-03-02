import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * this class contain the functions that can read and manipulate queries from the input text file
 */
public class QueryReader {

    /**
     * check the type of the query
     *
     * @param query the given query from the text file
     * @return the type of the query - VE (Variable Elimination) or BAYES (Bayes Ball)
     */
    public static QueryType typeOfQuery(String query) {
        return query.charAt(0) == 'P' ? QueryType.VE : QueryType.BAYES;
    }

    /**
     * getting a bayes ball query and return a list with the variables
     *
     * @param query the given query from the text file
     * @return list with the variables
     */
    public static List<String> bayesBallQuery(String query) {

        String[] half = query.split("\\|");
        String[] first_second = half[0].split("-");

        List<String> output = new ArrayList<>(Arrays.asList(first_second));

        if (half.length > 1) {
            String[] evidence = half[1].split(",");
            for (String s : evidence) {
                StringBuilder ve = new StringBuilder();
                String[] keys = s.split("=");
                ve.append(keys[0]);
                output.add(ve.toString());
            }
        }

        System.out.println("OUTPUT: " + output);

        return output;
    }

    public static List<String> variableEliminationQuery(String query) {
        List<String> output = new ArrayList<>();
        String[] in_query = query.split(" ");
        String in_parentheses = in_query[0].substring(2, in_query[0].length() - 1);
        String[] half = in_parentheses.split("\\|");
        output.add(half[0]);

        StringBuilder evidence = new StringBuilder();
        if(half.length > 1) {
            String[] evidences = half[1].split(",");
            for(int i = 0; i < evidences.length; i++) {
                evidence.append(evidences[i]);
                if(i != evidences.length -1) evidence.append(",");
            }
        }
        if(evidence.toString().length() > 0) {
            output.add(evidence.toString());
        }
        return output;
    }

    public static List<String> variableEliminationQueryHidden(String query) {
        List<String> output = new ArrayList<>();
        String[] half = query.split("\\|");
        String[] split_hidden = half[1].split(" ");
        String[] hidden = split_hidden[1].split("-");
        Collections.addAll(output, hidden);
        return output;
    }
}
