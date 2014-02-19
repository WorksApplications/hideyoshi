package jp.co.worksap.workspace.wasinstall;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class WASInstallConfiguration {
    private String installLocation;
    private String downloadURL;
    @Nullable
    private String updateInstallerDownloadURL;
    @Nullable
    private List<UpdatePackage> updatePackages;

    @Nonnull
    public List<UpdatePackage> getUpdatePackages() {
        if (updatePackages == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(updatePackages);
    }

    @Nonnull
    URL getUrlToDownload() {
        try {
            return URI.create(downloadURL).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nonnull
    URL getUpdateInstallerUrlToDownload() {
        try {
            return URI.create(updateInstallerDownloadURL).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
