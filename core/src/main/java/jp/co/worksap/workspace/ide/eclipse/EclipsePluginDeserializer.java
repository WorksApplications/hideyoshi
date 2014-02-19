package jp.co.worksap.workspace.ide.eclipse;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import com.google.common.base.Splitter;

public class EclipsePluginDeserializer extends JsonDeserializer<EclipsePlugin> {
    private static final Splitter SLASH_SPLITTER = Splitter.on('/').limit(2);
    private PluginAliasConverter converter = new PluginAliasConverter();

    @Override
    public EclipsePlugin deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        String literal = node.asText();

        List<String> split = SLASH_SPLITTER.splitToList(literal);
        if (split.size() == 2) {
            return EclipsePlugin.of(converter.apply(split.get(0)), split.get(1));
        } else {
            return EclipsePlugin.of(converter.apply(literal));
        }
    }
}
