import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for reading a Bayesian Networks XML files.
 */
public class FileManager {

    /**
     * Method to read a Bayesian network from an XML file, with the format used by the AISpace tool.
     *
     * @param BNFile File to read Bayesian network from.
     * @return BN object representing the BN from the file.
     */
    public static BN readBNFromFile(File BNFile) {

        BN bn = new BN();

        try {

            // Open and parse the XML file defining the BN.
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document BNFileDoc = documentBuilder.parse(BNFile);
            BNFileDoc.getDocumentElement().normalize();

            // Get the set of nodes in the XML file corresponding to the variables and the probability definitions.
            NodeList BNFileVariables = BNFileDoc.getElementsByTagName("VARIABLE");
            NodeList BNFileDefinitions = BNFileDoc.getElementsByTagName("DEFINITION");

            // Loop over the variables in the XML file to create variables in our BN object.
            for (int currVarIndex = 0; currVarIndex < BNFileVariables.getLength(); currVarIndex++) {

                Node currVarNode = BNFileVariables.item(currVarIndex);

                if (currVarNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element currVarElement = (Element) currVarNode;

                    // Get variable name.
                    String varName = currVarElement.getElementsByTagName("NAME").item(0).getTextContent();

                    // Get variable outcomes.
                    ArrayList<String> varOutcomes = new ArrayList<>();
                    NodeList outcomes = currVarElement.getElementsByTagName("OUTCOME");
                    for (int currOutcomeIndex = 0; currOutcomeIndex < outcomes.getLength(); currOutcomeIndex++) {
                        varOutcomes.add(outcomes.item(currOutcomeIndex).getTextContent());
                    }

                    // Get variable position property.
                    String varPosition = currVarElement.getElementsByTagName("PROPERTY").item(0).getTextContent();

                    // Create BNVariable object and add to the BN.
                    bn.addVariable(varName, varOutcomes, varPosition);

                }

            }

            // Loop over definitions in the XML file to get the parents of each variable and associated probabilities.
            for (int currDefIndex = 0; currDefIndex < BNFileDefinitions.getLength(); currDefIndex++) {

                Node currDefNode = BNFileDefinitions.item(currDefIndex);

                if (currDefNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element currDefElement = (Element) currDefNode;

                    // Get variable this definition is for.
                    String variableForStr = currDefElement.getElementsByTagName("FOR").item(0).getTextContent();
                    BNVariable BNVariableFor = bn.getVariable(variableForStr);

                    // Get probability table and add probability table to the variable.
                    String probTableString = currDefElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    ArrayList<String> probStrings = new ArrayList<>(Arrays.asList(probTableString.split(" ")));
                    ArrayList<Double> probTable = new ArrayList<>();
                    for (String p : probStrings) {
                        probTable.add(Double.valueOf(p));
                    }
                    BNVariableFor.setProbTable(probTable);

                    // Get variables this variable is affected by and add variableGivens as parents to this variableFor.
                    NodeList variableGivensList = currDefElement.getElementsByTagName("GIVEN");

                    for (int currVarGivenIndex = 0; currVarGivenIndex < variableGivensList.getLength(); currVarGivenIndex++) {

                        // Get variable associated with current GIVEN value.
                        String currVarGivenStr = variableGivensList.item(currVarGivenIndex).getTextContent();

                        // Add variables as parent to BNVariableFor.
                        BNVariableFor.addParent(currVarGivenStr);

                    }

                }

            }

        } catch (Exception e) { // Error when reading BN from the given XML file.

            System.out.println("readBNFromFile() Exception - Could not parse Bayesian network from file: " + BNFile.getName());
            System.exit(-1);

        }

        // Output progress to the user.
//        System.out.println("Successfully Read Bayesian Network From File: '" + BNFile.getName() + "'.");
//        System.out.println("-------------------------------------------------------------------------------");

        return bn;
    }
}