package jp.co.worksap.workspace.wasprofile;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSecurityConfiguration {
    private String alias;
    private String userid;
    private String password;
}
