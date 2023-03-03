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

        // Read and generate BN instance from given xml.
        BN bn = FileManager.readBNFromFile(file);
        for (BNVariable v : bn.getVariables()) {
//            System.out.println("Name:" + v.getName() + "\n");
//            System.out.println("ParentNames:" + v.getParentNames() + "\n");
//            System.out.println("Outcomes:" + v.getOutcomes() + "\n");
//            System.out.println("ProbTable:" + v.getProbTable() + "\n");
        }

        Scanner sc = new Scanner(System.in);
        // getting the document of the xml file
        Document doc = XMLReader.readXMLFile(filePath);
        // build the variables for the bayesian network from given document
        List<Variable> variables = new ArrayList<>(XMLReader.build_variables(doc));
        // building the bayesian network
        Network net = new Network(variables);
        // output text for output file
        StringBuilder output = new StringBuilder();

        switch (args[0]) {
            case "P1": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
//                System.out.println("variable:" + variable);
//                System.out.println("value:" + value);

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

//                String String variable = "N:T"; = query[1];
//                String variable = "N";
//                String value = "T";
//                String[] order = {"J","L","K"};

                // execute query of p(variable=value) with given order of elimination
                VariableElimination ve = new VariableElimination(bn.getVariablesArrayList());
                for (String name : order) {
                    // Get eliminate variables from order
                    ArrayList<BNVariable> eliminateVariables = ve.getEliminateVariables(name);
                    VariableElimination newVe = new VariableElimination(ve.getVariables());
                    newVe.removeVariables(eliminateVariables);
                    for (BNVariable v : eliminateVariables) {
                        if (v.hasParents()) {
                            boolean isEliminateVar = name.equals(v.getName());
//                            if (!name.equals(v.getName())) {
                                ArrayList<BNVariable> sumOutVars = new ArrayList<>();
                                // In the case of two parents
                                if (v.getParents().size() == 2) {
                                    for (BNVariable parentVar : ve.getVariableByName(v.getParents().get(v.getParents().indexOf(name)))) {
                                        sumOutVars.add(ve.getSumOutVariableTwoParents(v, parentVar, isEliminateVar));
                                    }
                                // In the case of one parents
                                } else if (v.getParents().size() == 1) {
                                    for (BNVariable parentVar : ve.getVariableByName(v.getParents().get(0))) {
                                        sumOutVars.add(ve.getSumOutVariable(v, parentVar, isEliminateVar));
                                    }
                                }
                                for (BNVariable sumOutVar : sumOutVars) {
                                    newVe.addVariable(sumOutVar);
                                }
//                            }
                        }
                    }
                    ve = newVe;
//                    for (BNVariable v : ve.getVariables()) {
//                        System.out.println("variable = " + v.getName() + " parents = " + v.getParentNames());
//                        for (double d : v.getProbTable()) {
//                            System.out.println("prob = " + d);
//                        }
//                    }
                }
                ArrayList<BNVariable> results = new ArrayList<>();
                for (BNVariable v : ve.getVariables()) {
//                    System.out.println("variable = " + v.getName() + " parents = " + v.getParentNames());
//                    for (double d : v.getProbTable()) {
//                        System.out.println("prob = " + d);
//                    }
                    results.add(v);
                }
                if (results.size() == 1) {
                    double result = value.equals("T") ? results.get(0).getProbTable().get(0) : results.get(1).getProbTable().get(1);
                    printResult(result);
                }
            }
            break;

            case "P3": {
                // construct the network based on the specification in args[1]
                String[] query = getQueriedNode(sc);
                String variable = query[0];
                String value = query[1];
                ArrayList<String[]> evidence = getEvidence(sc);
                // execute query of p(variable=value|evidence) with an order
                List<Double> ve_result = net.variable_elimination(variable, value, evidence);
                double result = ve_result.get(0);
                printResult(result);

                // need to save output to output txt file...
                output.append(UtilFunctions.roundFiveDecimalPlaces(ve_result.get(0)));
                output.append(",");
                output.append((long)Math.floor(ve_result.get(1)));
                output.append(",");
                output.append((long)Math.floor(ve_result.get(2)));
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