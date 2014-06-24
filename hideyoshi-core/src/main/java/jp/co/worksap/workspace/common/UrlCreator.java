package jp.co.worksap.workspace.common;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UrlCreator {
    public URL createFrom(URI uri) throws MalformedURLException {
        return createFrom(uri, new File("."));
    }

    public URL createFrom(URI uri, File currentFolder) throws MalformedURLException {
        checkNotNull(uri);
        checkNotNull(currentFolder);
        checkArgument(currentFolder.isDirectory());

        if (uri.isOpaque()) {
            throw new IllegalArgumentException("Given uri is opaque:" + uri.toString());
        } else if (uri.isAbsolute()) {
            return uri.normalize().toURL();
        } else {
            return new File(currentFolder, uri.toString()).toURI().normalize().toURL();
        }
    }

    public URL createFrom(String string) throws MalformedURLException {
        return createFrom(URI.create(string));
    }
}
