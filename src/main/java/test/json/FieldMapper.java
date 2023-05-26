package test.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


public class FieldMapper {

    private static final String INSIDE_CURLY_BRACES = "^\\{(.*)\\}$";
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
                if (fieldValue.isTextual() && fieldValue.asText().contains("{")) {
                    valueSwap(template, results, fieldName, fieldValue);
                }
            } else {
                recursiveSwap(fieldValue, results);
            }
        }
    }
    private static void valueSwap(JsonNode template, JsonNode results, String fieldName, JsonNode fieldValue){
        String operations = fieldValue.asText().replaceAll(INSIDE_CURLY_BRACES, "$1");
        if (operations.contains(":")){
            multiValueSwap(template, results, fieldName, operations);
        } else {
            ((ObjectNode) template).replace(fieldName, results.findValue(operations));
        }
    }
    private static void multiValueSwap(JsonNode template, JsonNode results, String fieldName, String operations){
        String[] ops = operations.split(":");
        String outer = ops[0];
        String inner = ops[1];
        JsonNode outerNode = results.path(outer);
        if(inner.contains("[")){
            List<String> labels = getLabelsInBraces(inner);
            Map<String, List<JsonNode>> data = new HashMap<>();
            for(String label: labels){
                data.put(label, outerNode.findValues(label));
            }
            String finalString = "";
            final int SIZE = data.get(labels.get(0)).size();
            for(int i = 0; i < SIZE; i++){
                String resultTemplate = inner;

                for(String label: labels){
                    resultTemplate = resultTemplate.replace("[" + label + "]", data.get(label).get(i).asText());
                }
                String lastChar = (i == SIZE - 1) ? "" : ", ";
                finalString = finalString + resultTemplate + lastChar;
            }
            ((ObjectNode) template).replace(fieldName, new TextNode(finalString));
        }
    }
    public static List<String> getLabelsInBraces(String input) {
        List<String> labels = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String word = matcher.group(1);
            labels.add(word);
        }
        return labels;
    }
}
