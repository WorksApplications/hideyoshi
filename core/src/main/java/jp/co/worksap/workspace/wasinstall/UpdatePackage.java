package jp.co.worksap.workspace.wasinstall;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePackage {
    private String name;
    private String downloadURL;

    @Nonnull
    URL getUrlToDownload() {
        try {
            return URI.create(downloadURL).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
