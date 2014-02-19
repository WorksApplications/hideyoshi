package jp.co.worksap.workspace.ide.eclipse;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class VersionDeserializer extends JsonDeserializer<Version> {
    private final VersionLiteralConverter converter = new VersionLiteralConverter();

    @Override
    public Version deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return converter.apply(node.asText());
    }

}
