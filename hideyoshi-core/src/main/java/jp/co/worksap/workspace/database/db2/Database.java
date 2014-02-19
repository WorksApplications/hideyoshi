package jp.co.worksap.workspace.database.db2;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class Database {
    private String database;
    private String alias;
    private String node;

    @Nonnull
    public String catalogCommand() {
        return "CATALOG DATABASE " + this.getDatabase() + " AS  " + this.getAlias() + " AT NODE " + this.getNode();
    }
}
