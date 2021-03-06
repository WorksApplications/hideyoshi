package jp.co.worksap.workspace.repository.git;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class GitHookConfiguration implements GitRemoteHost {
    private String uri;
    private String branch = "master";
}
