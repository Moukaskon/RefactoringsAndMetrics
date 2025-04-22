import java.util.List;

public class CommitAfterRef {
    String refactoringCommit;
    String commitAfterRefactoring;
    List<String> refactoringTypes;
    List<List<String>> involvedFilesAfterRefactoring;
    Analysis analysis;

    public CommitAfterRef(String refactoringCommit, List<String> refactoringTypes, List<List<String>> involvedFilesAfterRefactoring) {
        this.refactoringCommit = refactoringCommit;
        this.refactoringTypes = refactoringTypes;
        this.involvedFilesAfterRefactoring = involvedFilesAfterRefactoring;
    }

    public void destroyMe() {
    	this.analysis = null;
    }

    public String getRefactoringCommit() {
        return refactoringCommit;
    }

    public void setRefactoringCommit(String refactoringCommit) {
        this.refactoringCommit = refactoringCommit;
    }

    public String getCommitAfterRefactoring() {
        return commitAfterRefactoring;
    }

    public void setCommitAfterRefactoring(String commitAfterRefactoring) {
        this.commitAfterRefactoring = commitAfterRefactoring;
    }

    public List<String> getRefactoringTypes() {
        return refactoringTypes;
    }

    public void setRefactoringTypes(List<String> refactoringTypes) {
        this.refactoringTypes = refactoringTypes;
    }

    public List<List<String>> getInvolvedFilesAfterRefactoring() {
        return involvedFilesAfterRefactoring;
    }

    public void setInvolvedFilesAfterRefactoring(List<List<String>> involvedFilesAfterRefactoring) {
        this.involvedFilesAfterRefactoring = involvedFilesAfterRefactoring;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    @Override
    public String toString() {
        return "CommitBeforeRef{" +
                "refactoringCommit='" + refactoringCommit + '\'' +
                ", commitBeforeRefactoring='" + commitAfterRefactoring + '\'' +
                ", refactoringTypes=" + refactoringTypes +
                ", involvedFilesBeforeRefactoring=" + involvedFilesAfterRefactoring +
                '}';
    }
}
