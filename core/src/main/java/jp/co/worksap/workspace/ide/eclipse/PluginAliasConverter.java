package jp.co.worksap.workspace.ide.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

final class PluginAliasConverter implements Function<String, String> {
    private final Map<String, String> aliasMap;

    PluginAliasConverter() {
        try (InputStream input = getClass().getResourceAsStream("plugin-aliases.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            aliasMap = Maps.fromProperties(properties);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Nullable
    public String apply(@Nullable String mightBeAlias) {
        String converted = aliasMap.get(mightBeAlias);
        if (converted != null) {
            return converted;
        } else {
            return mightBeAlias;
        }
    }
}