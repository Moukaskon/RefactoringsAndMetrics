package refsandmetrs;

public class FileHandler {
    String filesAfterRefs = "";
    String commitSha = "";
    String isRefactored = "";
    String filaPath = "";
    String commitNumber = "";
    String refactorings = "";


    public FileHandler(String refactorings, String filesAfterRefs, String commitSha, String filaPath, String commitNumber) {
        this.refactorings = refactorings;
        this.filesAfterRefs = filesAfterRefs;
        this.commitSha = commitSha;
        if(filesAfterRefs != "\""){
            this.isRefactored = "1";
        }else{
            this.isRefactored = "0";
        }

        this.filaPath = filaPath;
        this.commitNumber = commitNumber;
    }

    public FileHandler(){

    }

    public String getFilesAfterRefs() {
        return filesAfterRefs;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public String getIsRefactored() {
        return isRefactored;
    }

    public String getFilaPath() {
        return filaPath;
    }

    public String getCommitNumber() {
        return commitNumber;
    }

    public String getRefactorings() {
        return refactorings;
    }

    public void setAll(String filesAfterRefs, String refactorings) {
        this.filesAfterRefs += filesAfterRefs;
        this.refactorings += refactorings;
    }

    @Override
    public String toString() {
        return "FileHandler [filesBeforeRefs=" + ", filesAfterRefs=" +
                filesAfterRefs + ", commitSha=" + commitSha + ", isRefactored="
                + isRefactored + ", filaPath=" + filaPath + ", commitNumber=" + commitNumber + "]";
    }
}
