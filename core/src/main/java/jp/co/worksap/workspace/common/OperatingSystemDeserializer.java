package jp.co.worksap.workspace.common;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class OperatingSystemDeserializer extends
        JsonDeserializer<OperatingSystem> {
    private final OperatingSystemLiteralConverter converter = new OperatingSystemLiteralConverter();

    @Override
    public OperatingSystem deserialize(JsonParser jsonParser,
            DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        return converter.apply(node.asText());
    }

}
