import org.w3c.dom.Document;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Main class.
 */
class A2main {

    public static void main(String[] args) {

        // Validate file.
        String filePath = args[1];
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error: Invalid file path given for <NID>.\n");
            System.exit(-1);
        }

        // Constructing BN instance
        Document doc = FileManager.readXML(filePath);
        List<Variable> variables = new ArrayList<>(FileManager.buildVariables(doc));
        BN bn = new BN(variables);
        Scanner sc = new Scanner(System.in);

        switch (args[0]) {
            case "P1": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];

                List<Double> results = VariableElimination.VE(variable, value, new ArrayList<>(), new ArrayList<>(), bn);
                double result = results.get(0);
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
                List<Double> results = VariableElimination.VE(variable, value, new ArrayList<>(), order, bn);
                double result = results.get(0);
                printResult(result);
            }
            break;

            case "P3": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                ArrayList<String[]> evidence = getEvidence(sc);
                // execute query of p(variable=value|evidence) with an order
                List<Double> results = VariableElimination.VE(variable, value, evidence, new ArrayList<>(), bn);
                double result = results.get(0);
                printResult(result);
            }
            break;

            case "P4": {

                // Validate a given BN should be DAG
                HashMap<String, Integer> variablesWithNumber = TopologicalSort.getVariablesWithNumber(variables);
                TopologicalSort bna = new TopologicalSort(variablesWithNumber.size());

                // BNA
                if (variablesWithNumber.size() == 4) {
                    bna.addEdge(variablesWithNumber.get("A"), variablesWithNumber.get("B"));
                    bna.addEdge(variablesWithNumber.get("B"), variablesWithNumber.get("C"));
                    bna.addEdge(variablesWithNumber.get("C"), variablesWithNumber.get("D"));
                // BNB
                } else if (variablesWithNumber.size() == 6) {
                    bna.addEdge(variablesWithNumber.get("J"), variablesWithNumber.get("K"));
                    bna.addEdge(variablesWithNumber.get("K"), variablesWithNumber.get("M"));
                    bna.addEdge(variablesWithNumber.get("L"), variablesWithNumber.get("M"));
                    bna.addEdge(variablesWithNumber.get("M"), variablesWithNumber.get("N"));
                    bna.addEdge(variablesWithNumber.get("M"), variablesWithNumber.get("O"));
                // BNC
                } else if (variablesWithNumber.size() == 7) {
                    bna.addEdge(variablesWithNumber.get("P"), variablesWithNumber.get("Q"));
                    bna.addEdge(variablesWithNumber.get("R"), variablesWithNumber.get("S"));
                    bna.addEdge(variablesWithNumber.get("Q"), variablesWithNumber.get("S"));
                    bna.addEdge(variablesWithNumber.get("S"), variablesWithNumber.get("U"));
                    bna.addEdge(variablesWithNumber.get("R"), variablesWithNumber.get("V"));
                    bna.addEdge(variablesWithNumber.get("Q"), variablesWithNumber.get("V"));
                    bna.addEdge(variablesWithNumber.get("S"), variablesWithNumber.get("Z"));
                    bna.addEdge(variablesWithNumber.get("V"), variablesWithNumber.get("Z"));
                }

                List<Integer> sortedVariables = bna.topologicalSort();
                List<String> sortedOrder = bna.getSortedOrder(sortedVariables, variablesWithNumber);
//                System.out.println("sortedOrder: "+sortedOrder);

                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                List<Double> results = VariableElimination.VE(variable, value, new ArrayList<>(), new ArrayList<>(), bn);
                double result = results.get(0);
                printResult(result);
            }
            break;

            default: {
                // Invalid args[0]
                System.out.println("Invalid args[0]");
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