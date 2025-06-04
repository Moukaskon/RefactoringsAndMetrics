import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    ArrayList<CommitBeforeRef> commitBeforeRefs;
    ArrayList<CommitAfterRef> commitAfterRefs;
    String commitSha;

    public FileHandler(ArrayList<CommitBeforeRef> commitBeforeRefs, ArrayList<CommitAfterRef> commitAfterRefs, String commitSha) {
        this.commitBeforeRefs = commitBeforeRefs;
        this.commitAfterRefs = commitAfterRefs;
        this.commitSha = commitSha;
    }

    public ArrayList<CommitBeforeRef> getCommitBeforeRefs() {
        return commitBeforeRefs;
    }

    public ArrayList<CommitAfterRef> getCommitAfterRefs() {
        return commitAfterRefs;
    }
    public String getCommitSha() {
        return commitSha;
    }

    @Override
    public String toString() {
        return "FileHandler [commitBeforeRefs=" + commitBeforeRefs + ", commitAfterRefs=" + commitAfterRefs + "]";
    }
}
