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

        // Read and generate BN instance from given xml.
        BN bn = FileManager.readBNFromFile(file);
        for (BNVariable v : bn.getVariables()) {
            System.out.println("Name:" + v.getName() + "\n");
            System.out.println("ParentNames:" + v.getParentNames() + "\n");
            System.out.println("Outcomes:" + v.getOutcomes() + "\n");
            System.out.println("ProbTable:" + v.getProbTable() + "\n");
        }

        Scanner sc = new Scanner(System.in);

        switch (args[0]) {
            case "P1": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                System.out.println("variable:" + variable);
                System.out.println("value:" + value);

                BNVariable inputVar = bn.getVariable(variable);
                // Finish if inputVar has no parents.
                if (!inputVar.hasParents()) {
                    double result = value.equals("T") ? inputVar.getProbTable().get(0) : inputVar.getProbTable().get(1);
                    printResult(result);
                }

                ArrayList<Double> probtable = new ArrayList<>();

                for (BNVariable v : bn.getVariables()) {
                    // Skip top node
                    if (!v.hasParents()) {
                        for (Double p : v.getProbTable()) {
                            probtable.add(p);
                        }
                        continue;
                    }
                    Double value1 = probtable.get(0) * v.getProbTable().get(0);
                    Double value2 = probtable.get(0) * v.getProbTable().get(1);
                    Double value3 = probtable.get(1) * v.getProbTable().get(2);
                    Double value4 = probtable.get(1) * v.getProbTable().get(3);

                    probtable.clear();

                    probtable.add(0,value1 + value3);
                    probtable.add(1,value2 + value4);

                    if (v.getName().equals(inputVar.getName())) {
                        break;
                    }
                }
                // Finish if inputVar has no parents.
                double result = value.equals("T") ? probtable.get(0) : probtable.get(1);
                printResult(result);
            }
            break;

            case "P2": {

                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                String[] order = getOrder(sc);

                // execute query of p(variable=value) with given order of elimination
                VariableElimination ve = new VariableElimination(bn.getVariables());
                for (String name : order) {
                    // Get eliminate variables from order
                    HashSet<BNVariable> eliminateVariables = ve.getEliminateVariables(name);
                    for (BNVariable v : eliminateVariables) {
                        if (v.hasParents()) {
                            BNVariable sumOutVar = new BNVariable();
                            // In the case of two parents
                            if (v.getParents().size() == 2) {
                                sumOutVar = ve.getSumOutVariableTwoParents(v, ve.getVariable(v.getParents().get(1)));
                            // In the case of one parents
                            } else if (v.getParents().size() == 1) {
                                sumOutVar = ve.getSumOutVariable(v, ve.getVariable(v.getParents().get(0)));
                            }
                            ve.removeVariables(eliminateVariables);
                            ve.addVariable(sumOutVar);
                        }
                    }
                    for (BNVariable v : ve.getVariables()) {
                        System.out.println("variable = " + v.getName() + " parents = " + v.getParentNames());
                        for (double d : v.getProbTable()) {
                            System.out.println("prob = " + d);
                        }
                    }
                }
                double result = 0.05;
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
                double result = 0.570501;
                printResult(result);
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