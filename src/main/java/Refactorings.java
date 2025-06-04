import java.util.ArrayList;

public class Refactorings {
    ArrayList<String> filesBeforeRef = new ArrayList<>();
    ArrayList<String> filesAfterRef = new ArrayList<>();
    String refactoringName;
    String commitSHA;

    public Refactorings(String refactoringName, String commitSHA) {
        this.refactoringName = refactoringName;
        this.commitSHA = commitSHA;
    }

    public ArrayList<String> getFilesBeforeRef() {
        return filesBeforeRef;
    }

    public ArrayList<String> getFilesAfterRef() {
        return filesAfterRef;
    }

    public void addBeforeRefFile(String fileName) {
        filesBeforeRef.add(fileName);
    }

    public void addAfterRefFile(String fileName) {
        filesAfterRef.add(fileName);
    }
}
