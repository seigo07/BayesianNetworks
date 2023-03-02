import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * this class contains static functions that can handle xml file reading and xml document reading for building bayesian network
 */
public class XMLReader {

    /**
     * this function return a xml document by a given xml file name
     *
     * @param file_name xml file
     * @return document
     */
    public static Document readXMLFile(String file_name) {
        // xml file
        File inputFile = new File(file_name);

        // create factory for reading xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // create builder for reading xml
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // create document form xml file (parse with builder)
        Document doc = null;
        try {
            if (builder == null) {
                throw new IOException();
            }
            doc = builder.parse(inputFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * build a list of variables by a given xml document
     *
     * @param doc given xml document
     * @return list fo variables
     */
    public static List<Variable> build_variables(Document doc) {

        // final names of each variable
        List<String> names = new ArrayList<>();

        // final outcomes of each variable
        List<List<String>> outcomes = new ArrayList<>();

        // final parents of each variable
        HashMap<String, List<Variable>> parents = new HashMap<>();

        // final values of each variable
        HashMap<String, List<Double>> values = new HashMap<>();

        // reading all the variables
        NodeList variableList = doc.getDocumentElement().getElementsByTagName("VARIABLE");

        for (int i = 0; i < variableList.getLength(); i++) {
            Node variableNode = variableList.item(i);
            String name = "";
            List<String> outcome = new ArrayList<>();
            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList inner = variableNode.getChildNodes();
                for (int j = 0; j < inner.getLength(); j++) {
                    Node innerNode = inner.item(j);

                    // getting name
                    if (innerNode.getNodeName().equals("NAME")) {
                        name = innerNode.getTextContent();

                        // getting outcomes
                    } else if (innerNode.getNodeName().equals("OUTCOME")) {
                        NodeList node_outcomes = innerNode.getChildNodes();
                        for (int k = 0; k < node_outcomes.getLength(); k++) {
                            outcome.add(node_outcomes.item(k).getTextContent());
                        }
                    }
                }
            }
            names.add(name);
            outcomes.add(outcome);
        }

        // hashmap of our current variables indexes by name
        HashMap<String, Variable> variableHashMap = new HashMap<>();
        for (int i = 0; i < variableList.getLength(); i++) {
            variableHashMap.put(names.get(i), new Variable(names.get(i), outcomes.get(i)));
        }

        // reading all the variables
        NodeList definitionList = doc.getDocumentElement().getElementsByTagName("DEFINITION");

        for (int i = 0; i < definitionList.getLength(); i++) {
            Node definitionNode = definitionList.item(i);
            String name = "";
            List<Variable> variable_parents = new ArrayList<>();
            String table = "";
            if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList inner = definitionNode.getChildNodes();
                for (int j = 0; j < inner.getLength(); j++) {
                    Node innerNode = inner.item(j);

                    // getting name
                    if (innerNode.getNodeName().equals("FOR")) {
                        name = innerNode.getTextContent();

                        // getting parents
                    } else if (innerNode.getNodeName().equals("GIVEN")) {
                        NodeList node_parents = innerNode.getChildNodes();
                        for (int k = 0; k < node_parents.getLength(); k++) {
                            variable_parents.add(variableHashMap.get(node_parents.item(k).getTextContent()));
                        }

                        // getting table numbers
                    } else if (innerNode.getNodeName().equals("TABLE")) {
                        table = innerNode.getTextContent();
                    }
                }
            }
            parents.put(name, variable_parents);
            values.put(name, split_table_line(table));
        }

        List<Variable> variables = new ArrayList<>();

        // for each variable initialize parents
        variableHashMap.forEach((key, value) -> {
            List<Double> t1 = values.get(key);
            List<Variable> s1 = parents.get(key);

            double[] t2 = new double[t1.size()];
            for (int i = 0; i < t2.length; i++) t2[i] = t1.get(i);

            Variable[] s2 = new Variable[s1.size()];
            for (int i = 0; i < s2.length; i++) s2[i] = s1.get(i);

            value.initialize_parents(t2, s2);
            variables.add(value);
        });

        // return variables for the bayesian network
        return variables;
    }

    /**
     * this function gets a string of double values from the TABLE tag in the DEFINITION tag in the xml file
     * and return this values in a list of doubles.
     * for example given "0.95 0.05 0.6 0.4" string this function will return List<Double>{0.95, 0.05, 0.6, 0.4}
     *
     * @param line string of double values
     * @return list of doubles
     */
    private static List<Double> split_table_line(String line) {
        String[] split_line = line.split(" ");
        List<Double> result = new ArrayList<>();
        for (String value : split_line) {
            result.add(Double.parseDouble(value));
        }
        return result;
    }
}
