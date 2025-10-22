package refsandmetrs;

import main.java.Analysis;

import java.util.List;

public class CommitBeforeRef {
    String refactoringCommit;
    String commitBeforeRefactoring;
    List<String> refactoringTypes;
    List<List<String>> involvedFilesBeforeRefactoring;
    Analysis analysis;

    public CommitBeforeRef(String refactoringCommit, List<String> refactoringTypes, List<List<String>> involvedFilesBeforeRefactoring) {
        this.refactoringCommit = refactoringCommit;
        this.refactoringTypes = refactoringTypes;
        this.involvedFilesBeforeRefactoring = involvedFilesBeforeRefactoring;
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

    public String getCommitBeforeRefactoring() {
        return commitBeforeRefactoring;
    }

    public void setCommitBeforeRefactoring(String commitBeforeRefactoring) {
        this.commitBeforeRefactoring = commitBeforeRefactoring;
    }

    public List<String> getRefactoringTypes() {
        return refactoringTypes;
    }

    public void setRefactoringTypes(List<String> refactoringTypes) {
        this.refactoringTypes = refactoringTypes;
    }

    public List<List<String>> getInvolvedFilesBeforeRefactoring() {
        return involvedFilesBeforeRefactoring;
    }

    public void setInvolvedFilesBeforeRefactoring(List<List<String>> involvedFilesBeforeRefactoring) {
        this.involvedFilesBeforeRefactoring = involvedFilesBeforeRefactoring;
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
                ", commitBeforeRefactoring='" + commitBeforeRefactoring + '\'' +
                ", refactoringTypes=" + refactoringTypes +
                ", involvedFilesBeforeRefactoring=" + involvedFilesBeforeRefactoring +
                '}';
    }
}
