package test.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


public class FieldMapper {

    private static final String INSIDE_CURLY_BRACES = "^\\{(.*)\\}$";
    private static final String DELIMITER = "~";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String map(JsonNode template, JsonNode results) {
        try{
            recursiveSwap(template, results);
            return mapper.writeValueAsString(template);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }   
    }

    private static void recursiveSwap(JsonNode template, JsonNode results) {
        Iterator<Entry<String, JsonNode>> fields = template.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();

            if (fieldValue.isValueNode()) {
                if (fieldValue.isTextual() && fieldValue.asText().indexOf("{") == 0) {
                    valueSwap(template, results, fieldName, fieldValue.asText().replaceAll(INSIDE_CURLY_BRACES, "$1"));
                }
            } else {
                recursiveSwap(fieldValue, results);
            }
        }
    }

    private static void valueSwap(JsonNode template, JsonNode results, String fieldName, String op){
        if (op.contains(DELIMITER)){
            String[] ops = splitString(op);
            String outer = ops[0];
            String inner = ops[1];
            JsonNode outerNode = results.findValue(outer);
            String resultString = multiValueSwap(outerNode, outer, inner);
            ((ObjectNode) template).replace(fieldName, new TextNode(resultString));
        } else {
            ((ObjectNode) template).replace(fieldName, results.findValue(op));
        }
    }

    private static String multiValueSwap(JsonNode outerNode, String outer, String inner){
        String finalString = "";

        if(inner.contains("[")){
            List<String> labels = getLabelsInBraces(inner);
            Map<String, List<JsonNode>> data = new HashMap<>();
            for(String label: labels){
                if (label.contains(DELIMITER)){
                    String[] nestedLabels = splitString(label);
                    String nestedOuter = nestedLabels[0];
                    String nestedInner = nestedLabels[1];
                    List<JsonNode> nesteds = outerNode.findValues(nestedOuter);
                    List<JsonNode> nestedValues = new ArrayList<>();
                    for (JsonNode nested : nesteds){
                        String nestV = multiValueSwap(nested, nestedOuter, nestedInner);
                        nestedValues.add(new TextNode(nestV));
                    }
                    data.put(label, nestedValues);
                } else {
                    data.put(label, outerNode.findValues(label));
                }
                
            }
            final int SIZE = data.get(labels.get(0)).size();
            for(int i = 0; i < SIZE; i++){
                String resultTemplate = inner;
                for(String label: labels){
                    resultTemplate = resultTemplate.replace("[" + label + "]", data.get(label).get(i).asText());
                }
                String lastChar = (i == SIZE - 1) ? "" : ", ";
                finalString = finalString + resultTemplate + lastChar;
            }
        } else {
            finalString += outerNode.findValue(inner).asText();
        }
        return finalString.replace("()", "");
    }

    public static List<String> getLabelsInBraces(String input) {
        List<String> words = new ArrayList<>();
        int bracketCount = 0;
        String label = "";

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ']') bracketCount--;
            if (bracketCount >= 1) label += c;
            if (c == '[') bracketCount++;
            if (bracketCount == 0 && !label.equals("")) {
                words.add(label);
                label = "";
            }
        }
        return words;
    }
    
    public static String[] splitString(String input) {
        int splitIndex = input.indexOf(DELIMITER);
        String[] splitArray = new String[2];
        splitArray[0] = input.substring(0, splitIndex);
        splitArray[1] = input.substring(splitIndex + 1);
        return splitArray;
    }
}
