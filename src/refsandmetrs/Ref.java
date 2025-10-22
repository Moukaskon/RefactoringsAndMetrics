package refsandmetrs;

import java.util.ArrayList;

public class Ref {
    private ArrayList<String> filesBeforeRef = new ArrayList<>();
    private ArrayList<String> filesAfterRef = new ArrayList<>();
    private String refactoringName;
    private String commitSHA;

    public Ref(String refactoringName, String commitSHA) {
        this.refactoringName = refactoringName;
        this.commitSHA = commitSHA;
    }

    public void addBeforeRefFile(String fileName) {
        filesBeforeRef.add(fileName);
    }

    public void addAfterRefFile(String fileName) {
        filesAfterRef.add(fileName);
    }

    public ArrayList<String> getFilesBeforeRef() {
        return filesBeforeRef;
    }

    public ArrayList<String> getFilesAfterRef() {
        return filesAfterRef;
    }

    public String getRefactoringName() {
        return refactoringName;
    }

    @Override
    public String toString() {
        return refactoringName + " " + commitSHA + " " + filesBeforeRef.size() + " " + filesAfterRef.size();
    }
}
