package jp.co.worksap.workspace.database.db2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.UrlCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DB2Configuration {
    private String username;
    private String password;
    private String downloadURL;
    private List<Node> nodes;
    private List<Database> databases;

    @Nonnull
    public List<Node> getNodes() {
        if (nodes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(nodes);
    }

    @Nonnull
    public List<Database> getDatabases() {
        if (databases == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(databases);
    }

    @Nonnull
    URL getUrlToDownload() {
        try {
            return new UrlCreator().createFrom(downloadURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
