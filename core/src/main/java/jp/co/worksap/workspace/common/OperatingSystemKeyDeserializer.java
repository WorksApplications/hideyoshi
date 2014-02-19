package jp.co.worksap.workspace.common;

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.KeyDeserializer;

public class OperatingSystemKeyDeserializer extends KeyDeserializer {
    private final OperatingSystemLiteralConverter converter = new OperatingSystemLiteralConverter();

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return converter.apply(key);
    }
}
