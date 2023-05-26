package test.json;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

public class FieldMapperTest 
{
    @Test
    public void fieldMapperTest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(System.getProperty("user.dir"));
            String path = "src/test/java/test/json/resources/";
            path += "readme_examples/";
            InputStream template = new FileInputStream(path+"template.json");
            InputStream results = new FileInputStream(path+"results.json");
            InputStream output = new FileInputStream(path+"output.json");
            String outputString = FieldMapper.map(mapper.readTree(template), mapper.readTree(results));
            String outputJson = mapper.readTree(output).toString();

            String diff = StringUtils.difference(outputString, outputJson);
            assertEquals("", diff);

        } catch (IOException e) {
            throw new IllegalArgumentException("JSON parsing error occurred" + e.getMessage(), e);
        }
    }
}
