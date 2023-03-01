import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for Variable Elimination Algorithm.
 */
public class VariableElimination {

    private HashMap<String, BNVariable> variables;

    /**
     * Blank Constructor:
     */
    public VariableElimination(HashSet<BNVariable> variables) {
        this.variables = new HashMap<>();
        for (BNVariable v : variables) {
            addVariable(v);
        }
    }

    /**
     * Add a variable to the variables.
     *
     * Remove a variable from the variables.
     */
    public void addVariable(BNVariable v) {
        this.variables.put(v.getName(), new BNVariable(v.getName(), v.getOutcomes(), v.getPosition(), v.getParents(), v.getProbTable()));
    }

    /**
     * Remove a variable from the variables.
     *
     * @param v BNVariable.
     */
    public void removeVariables(HashSet<BNVariable> eliminateVariables) {
        for (BNVariable v : eliminateVariables) {
            variables.remove(v.getName(), v);
        }
    }

    /**
     * Set variables for Variable Elimination Algorithm.
     */
    public HashSet<BNVariable> getVariables() {
        return new HashSet<>(variables.values());
    }

    /**
     * Get a variable from the variables by name.
     *
     * @param name Name of the variable to get.
     * @return Variable object of the variable name requested, null if does not exist.
     */
    public BNVariable getVariable(String name) {
        return variables.get(name);
    }

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
//        System.out.println("var = " + var.getName());
//        for (double d : var.getProbTable()) {
//            System.out.println("prob = " + d);
//        }
//        System.out.println("parentVar = " + parentVar.getName());
//        for (double d : parentVar.getProbTable()) {
//            System.out.println("prob = " + d);
//        }

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
    public BNVariable getSumOutVariableTwoParents(BNVariable var, BNVariable parentVar) {
//        System.out.println("parentVar1 = " + parentVar1.getName());
//        for (double d : parentVar1.getProbTable()) {
//            System.out.println("prob = " + d);
//        }
//        System.out.println("parentVar2 = " + parentVar2.getName());
//        for (double d : parentVar2.getProbTable()) {
//            System.out.println("prob = " + d);
//        }

        ArrayList<Double> sumOutProbtable = new ArrayList<>();

        Double trueValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(0);
        Double falseValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(1);
        Double trueValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(2);
        Double falseValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(3);
        Double trueValue3 = parentVar.getProbTable().get(0) * var.getProbTable().get(4);
        Double falseValue3 = parentVar.getProbTable().get(0) * var.getProbTable().get(5);
        Double trueValue4 = parentVar.getProbTable().get(1) * var.getProbTable().get(6);
        Double falseValue4 = parentVar.getProbTable().get(1) * var.getProbTable().get(7);

        sumOutProbtable.add(0,trueValue1 + trueValue2);
        sumOutProbtable.add(1,falseValue1 + falseValue2);
        sumOutProbtable.add(2,trueValue3 + trueValue4);
        sumOutProbtable.add(3,falseValue3 + falseValue4);

        var.removeParents(parentVar.getName());
        BNVariable sumOutVariable = new BNVariable(var.getName(), var.getOutcomes(), var.getPosition(), var.getParents(), sumOutProbtable);

        return sumOutVariable;
    }

}
