import org.w3c.dom.Document;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Main class.
 */
public class A2main {

    private static final String INVALID_ARGS_ERROR = "Usage: java A2main <Pn> <NID>"; // Usage Message.

    public static void main(String[] args) {

        // Validate args.
        if (args.length != 2) {
            System.out.println(INVALID_ARGS_ERROR);
            System.exit(-1);
        }

        // Validate file.
        String filePath = args[1];
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error: Invalid file path given for <NID>.\n" + INVALID_ARGS_ERROR);
            System.exit(-1);
        }

        Scanner sc = new Scanner(System.in);
        // Reading the document of the xml file
        Document doc = XMLReader.readXML(filePath);
        // Generating variables for BNs from a given document
        List<Variable> variables = new ArrayList<>(XMLReader.buildVariables(doc));
        // Instantiating BNs
        BN net = new BN(variables);
        // Output text for output file
        StringBuilder output = new StringBuilder();

        switch (args[0]) {
            case "P1": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];

                List<Double> ve_result = net.variableElimination(variable, value, new ArrayList<>(), new ArrayList<>());
                double result = ve_result.get(0);
                printResult(result);
            }
            break;

            case "P2": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                String[] inputOrder = getOrder(sc);
                List<String> order = new ArrayList<>(Arrays.asList(inputOrder));

                // execute query of p(variable=value|evidence) with an order
                List<Double> results = net.variableElimination(variable, value, new ArrayList<>(), order);
                double result = results.get(0);
                printResult(result);

                // need to save output to output txt file...
//                output.append(UtilFunctions.roundFiveDecimalPlaces(results.get(0)));
//                output.append(",");
//                output.append((long)Math.floor(results.get(1)));
//                output.append(",");
//                output.append((long)Math.floor(results.get(2)));
//                System.out.println("output:\n" + output);
            }
            break;

            case "P3": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                ArrayList<String[]> evidence = getEvidence(sc);
                // execute query of p(variable=value|evidence) with an order
                List<Double> results = net.variableElimination(variable, value, evidence, new ArrayList<>());
                double result = results.get(0);
                printResult(result);

                // need to save output to output txt file...
//                output.append(UtilFunctions.roundFiveDecimalPlaces(results.get(0)));
//                output.append(",");
//                output.append((long)Math.floor(results.get(1)));
//                output.append(",");
//                output.append((long)Math.floor(results.get(2)));
//                System.out.println("output:\n" + output);
            }
            break;

            case "P4": {
                // construct the network based on the specification in args[1]

            }

            default: {
                // Invalid args[0]
                System.out.println(INVALID_ARGS_ERROR);
                System.exit(-1);
            }
            break;
        }
        sc.close();
    }

    // method to obtain the evidence from the user
    private static ArrayList<String[]> getEvidence(Scanner sc) {

        System.out.println("Evidence:");
        ArrayList<String[]> evidence = new ArrayList<String[]>();
        String[] line = sc.nextLine().split(" ");

        for (String st : line) {
            String[] ev = st.split(":");
            evidence.add(ev);
        }
        return evidence;
    }

    // method to obtain the order from the user
    private static String[] getOrder(Scanner sc) {

        System.out.println("Order:");
        String[] val = sc.nextLine().split(",");
        return val;
    }

    // method to obtain the queried node from the user
    private static String[] getQueriedNode(Scanner sc) {

        System.out.println("Query:");
        String[] val = sc.nextLine().split(":");

        return val;

    }

    // method to format and print the result
    private static void printResult(double result) {

        DecimalFormat dd = new DecimalFormat("#0.00000");
        System.out.println(dd.format(result));
    }

}