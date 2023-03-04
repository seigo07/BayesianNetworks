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
 * The class handles a given xml file for building BNs
 */
public class XMLReader {

    /**
     * Reading a xml document by a given xml
     *
     * @param filePath
     * @return doc
     */
    public static Document readXML(String filePath) {

        File xmlFile = new File(filePath);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            if (docBuilder == null) {
                throw new IOException();
            }
            doc = docBuilder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Building a variable list by a given xml document
     *
     * @param doc
     * @return variables
     */
    public static List<Variable> buildVariables(Document doc) {

        // Final names of each variable
        List<String> names = new ArrayList<>();

        // Final outcomes of each variable
        List<List<String>> outcomes = new ArrayList<>();

        // Final parents of each variable
        HashMap<String, List<Variable>> parents = new HashMap<>();

        // Final values of each variable
        HashMap<String, List<Double>> values = new HashMap<>();

        // Final variables
        NodeList variableList = doc.getDocumentElement().getElementsByTagName("VARIABLE");

        for (int i = 0; i < variableList.getLength(); i++) {

            Node node = variableList.item(i);
            List<String> outcome = new ArrayList<>();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList inner = node.getChildNodes();
                for (int j = 0; j < inner.getLength(); j++) {
                    Node innerNode = inner.item(j);
                    if (innerNode.getNodeName().equals("NAME")) {
                        names.add(innerNode.getTextContent());
                    } else if (innerNode.getNodeName().equals("OUTCOME")) {
                        NodeList node_outcomes = innerNode.getChildNodes();
                        for (int k = 0; k < node_outcomes.getLength(); k++) {
                            outcome.add(node_outcomes.item(k).getTextContent());
                        }
                    }
                }
            }
            outcomes.add(outcome);
        }

        // Current variables indexed by name
        HashMap<String, Variable> variablesHashMap = new HashMap<>();
        for (int i = 0; i < variableList.getLength(); i++) {
            variablesHashMap.put(names.get(i), new Variable(names.get(i), outcomes.get(i)));
        }

        // Reading variables
        NodeList nodeList = doc.getDocumentElement().getElementsByTagName("DEFINITION");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node definitionNode = nodeList.item(i);
            String name = "";
            List<Variable> variableParents = new ArrayList<>();
            String table = "";
            if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList inner = definitionNode.getChildNodes();
                for (int j = 0; j < inner.getLength(); j++) {
                    Node innerNode = inner.item(j);
                    if (innerNode.getNodeName().equals("FOR")) {
                        name = innerNode.getTextContent();
                    } else if (innerNode.getNodeName().equals("GIVEN")) {
                        NodeList nodeParents = innerNode.getChildNodes();
                        for (int k = 0; k < nodeParents.getLength(); k++) {
                            variableParents.add(variablesHashMap.get(nodeParents.item(k).getTextContent()));
                        }
                    } else if (innerNode.getNodeName().equals("TABLE")) {
                        table = innerNode.getTextContent();
                    }
                }
            }
            parents.put(name, variableParents);
            values.put(name, splitTableLine(table));
        }

        List<Variable> variables = new ArrayList<>();

        // Initializing parents
        variablesHashMap.forEach((key, value) -> {
            List<Double> t1 = values.get(key);
            List<Variable> s1 = parents.get(key);

            double[] t2 = new double[t1.size()];
            for (int i = 0; i < t2.length; i++) t2[i] = t1.get(i);

            Variable[] s2 = new Variable[s1.size()];
            for (int i = 0; i < s2.length; i++) s2[i] = s1.get(i);

            value.initialize_parents(t2, s2);
            variables.add(value);
        });

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
    private static List<Double> splitTableLine(String line) {
        String[] split_line = line.split(" ");
        List<Double> result = new ArrayList<>();
        for (String value : split_line) {
            result.add(Double.parseDouble(value));
        }
        return result;
    }
}
