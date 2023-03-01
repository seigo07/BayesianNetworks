import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for BN object.
 */
public class BN {

    private final HashMap<String, BNVariable> networkNodes; // Mapping of variable names to their corresponding variable objects.


    /**
     * Blank Constructor:
     */
    public BN() {
        networkNodes = new HashMap<>(); // Initialise the BN structure.
    } // BayesianNetwork().

    /**
     * Add a variable to the network.
     *
     * @param name Name of the variable.
     * @param outcomes Outcomes of the variable (binary for this practical).
     * @param position Position property string for the AISpace tool.
     */
    public void addVariable(String name, ArrayList<String> outcomes, String position) {
        this.networkNodes.put(name, new BNVariable(name, outcomes, position));
    } // addVariable().

    /**
     * Add a variable to the network.
     *
     * @param name Name of the variable.
     * @param outcomes Outcomes of the variable.
     * @param position Position property string used to place the variable in the AISpace tool canvas.
     * @param parents Parents of this variable.
     * @param probTable (Conditional) Probability Table of this variable.
     */
    public void addVariable(String name, ArrayList<String> outcomes, String position, ArrayList<String> parents,
                            ArrayList<Double> probTable) {

        this.networkNodes.put(name, new BNVariable());

    } // addVariable().

    /**
     * Get a variable from the network.
     *
     * @param name Name of the variable to get.
     * @return Variable object of the variable name requested, null if does not exist.
     */
    public BNVariable getVariable(String name) {
        return networkNodes.get(name);
    } // getVariable().

    /**
     * Get the set of variable names for this Bayesian network.
     *
     * @return Hash set of variable names for this BN.
     */
    public HashSet<String> getVariableNames() {
        return new HashSet<>(networkNodes.keySet());
    } // getVariableNames().

    /**
     * Get the set of variables for this Bayesian network.
     *
     * @return Hash set of variables for this BN.
     */
    public HashSet<BNVariable> getVariables() {
        return new HashSet<>(networkNodes.values());
    } // getVariables().

    /**
     * @return eliminate variables.
     */
    public HashSet<BNVariable> getEliminateVariables(String name) {
        HashSet<BNVariable> variables = new HashSet<>();
        BNVariable sameVar = getVariable(name);
        variables.add(sameVar);
        for (BNVariable v : getVariables()) {
            if (v.getParents().contains(name)) {
                BNVariable var = getVariable(v.getName());
                variables.add(var);
            }
        }
        for (BNVariable v : variables) {
            if (v != null) {
                System.out.println("eliminate variable = "+ v.getName());
            }
        }
        return variables;
    }

    /**
     * @return sum-out variables for one parent.
     */
    public BNVariable getSumOutVariable(BNVariable var, BNVariable parentVar) {

        ArrayList<Double> sumOutProbtable = new ArrayList<>();

        Double trueValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(0);
        Double falseValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(1);
        Double trueValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(2);
        Double falseValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(3);

        sumOutProbtable.add(0,trueValue1 + trueValue2);
        sumOutProbtable.add(1,falseValue1 + falseValue2);

        BNVariable sumOutVariable = new BNVariable(var.getName(), var.getOutcomes(), var.getPosition(), new ArrayList<>(), sumOutProbtable);

        return sumOutVariable;
    }

    /**
     * @return sum-out variables for two parent.
     */
    public BNVariable getSumOutVariable(BNVariable var, BNVariable parentVar1, BNVariable parentVar2) {
        System.out.println("parentVar1 = " + parentVar1.getName());
        for (double d : parentVar1.getProbTable()) {
            System.out.println("prob = " + d);
        }
        System.out.println("parentVar2 = " + parentVar2.getName());
        for (double d : parentVar2.getProbTable()) {
            System.out.println("prob = " + d);
        }

        ArrayList<Double> sumOutProbtable = new ArrayList<>();

        Double trueValue1 = parentVar1.getProbTable().get(0) * parentVar2.getProbTable().get(0) * var.getProbTable().get(0);
        Double falseValue1 = parentVar1.getProbTable().get(0) * parentVar2.getProbTable().get(0) * var.getProbTable().get(1);
        Double trueValue2 = parentVar1.getProbTable().get(0) * parentVar2.getProbTable().get(1) * var.getProbTable().get(0);
        Double falseValue2 = parentVar1.getProbTable().get(0) * parentVar2.getProbTable().get(1) * var.getProbTable().get(1);
        Double trueValue3 = parentVar1.getProbTable().get(1) * parentVar2.getProbTable().get(2) * var.getProbTable().get(0);
        Double falseValue3 = parentVar1.getProbTable().get(1) * parentVar2.getProbTable().get(2) * var.getProbTable().get(1);
        Double trueValue4 = parentVar1.getProbTable().get(1) * parentVar2.getProbTable().get(3) * var.getProbTable().get(0);
        Double falseValue4 = parentVar1.getProbTable().get(1) * parentVar2.getProbTable().get(3) * var.getProbTable().get(1);

        sumOutProbtable.add(0,trueValue1 + trueValue2);
        sumOutProbtable.add(1,falseValue1 + falseValue2);
        sumOutProbtable.add(0,trueValue3 + trueValue4);
        sumOutProbtable.add(1,falseValue3 + falseValue4);

        BNVariable sumOutVariable = new BNVariable(var.getName(), var.getOutcomes(), var.getPosition(), new ArrayList<>(Arrays.asList(parentVar2.getName())), sumOutProbtable);

        return sumOutVariable;
    }
}
