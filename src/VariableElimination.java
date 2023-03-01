import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for Variable Elimination Algorithm.
 */
public class VariableElimination {

    private ArrayList<BNVariable> variables;

    /**
     * Blank Constructor:
     */
    public VariableElimination(ArrayList<BNVariable> variables) {
        this.variables = new ArrayList<>(variables);
    }

    /**
     * Add a variable to the variables.
     *
     * Remove a variable from the variables.
     */
    public void addVariable(BNVariable v) {
        this.variables.add(new BNVariable(v.getName(), v.getOutcomes(), v.getPosition(), v.getParents(), v.getProbTable()));
    }

    /**
     * Remove a variable from the variables.
     *
     * @param eliminateVariables eliminate variables.
     */
    public void removeVariables(ArrayList<BNVariable> eliminateVariables) {
        for (BNVariable v : eliminateVariables) {
            variables.remove(v);
        }
    }

    /**
     * Set variables for Variable Elimination Algorithm.
     */
    public ArrayList<BNVariable> getVariables() {
        return variables;
    }

    /**
     * Get a variable from the variables by name.
     *
     * @param name Name of the variable to get.
     * @return Variable object of the variable name requested, null if does not exist.
     */
    public ArrayList<BNVariable> getVariableByName(String name) {
        ArrayList<BNVariable> variables = new ArrayList<>();
        for (BNVariable v : getVariables()) {
//            System.out.println("v = "+ v.getName());
            if (v.getName().equals(name)) {
                variables.add(v);
//                System.out.println("name = "+ v.getName());
            }
        }
        return variables;
    }

    /**
     * @return eliminate variables.
     */
    public ArrayList<BNVariable> getEliminateVariables(String name) {
        ArrayList<BNVariable> variables = new ArrayList<>();
        ArrayList<BNVariable> sameVars = getVariableByName(name);
        variables.addAll(sameVars);
        for (BNVariable v : getVariables()) {
            if (v.getParents().contains(name)) {
                ArrayList<BNVariable> vars = getVariableByName(v.getName());
                variables.addAll(vars);
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
    public BNVariable getSumOutVariable(BNVariable var, BNVariable parentVar, boolean isEliminateVar) {
//        System.out.println("var = " + var.getName());
//        for (double d : var.getProbTable()) {
//            System.out.println("prob = " + d);
//        }
//        System.out.println("parentVar = " + parentVar.getName());
//        for (double d : parentVar.getProbTable()) {
//            System.out.println("prob = " + d);
//        }

        if (isEliminateVar) {
            return parentVar;
        }

        ArrayList<Double> sumOutProbtable = new ArrayList<>();

        Double trueValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(0);
        Double falseValue1 = parentVar.getProbTable().get(0) * var.getProbTable().get(1);
        Double trueValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(2);
        Double falseValue2 = parentVar.getProbTable().get(1) * var.getProbTable().get(3);

        sumOutProbtable.add(0,trueValue1 + trueValue2);
        sumOutProbtable.add(1,falseValue1 + falseValue2);

        BNVariable sumOutVariable = new BNVariable(var.getName(), var.getOutcomes(), var.getPosition(), new ArrayList<>(), sumOutProbtable);
//        System.out.println("sumOutVariable = " + sumOutVariable.getName() + " " + sumOutVariable.getProbTable());

        return sumOutVariable;
    }

    /**
     * @return sum-out variables for two parent.
     */
    public BNVariable getSumOutVariableTwoParents(BNVariable var, BNVariable parentVar, boolean isEliminateVar) {
//        System.out.println("var = " + var.getName());
//        for (double d : var.getProbTable()) {
//            System.out.println("prob = " + d);
//        }
//        System.out.println("parentVar = " + parentVar.getName());
//        for (double d : parentVar.getProbTable()) {
//            System.out.println("prob = " + d);
//        }

        if (isEliminateVar) {
        }

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
