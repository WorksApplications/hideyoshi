package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public abstract class Downloader {
    @CheckReturnValue
    public abstract boolean accept(@Nullable String scheme);

    public abstract void download(URI from, File to) throws IOException;

    public static Downloader createFor(URI uri) {
        return new StandardDownloader();
    }
}
