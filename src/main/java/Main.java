import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Main {

	public static void main(String[] args) {
		// Get url and name
		ArrayList<String> csvs = new ArrayList<>();
		ArrayList<String> projects = new ArrayList<>();
		//projects.add("https://github.com/teomaik/DeRec-GEA.git");
		//projects.add("https://github.com/jagrosh/MusicBot");
		//projects.add("https://github.com/docker-java/docker-java");
		projects.add("https://github.com/Kaaz/DiscordBot");
		
		System.out.println("Number of Command Line Argument = " + args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.println(String.format("Command Line Argument %d is %s", i, args[i]));
			projects.add(args[i]);
		}

		try {
			for (String prj : projects) {
				csvs.add(runAnalysis(prj));
				System.out.println("Found ya!" + csvs);
				System.out.println("In for!");
			}
		} catch (Exception e) {
			//System.out.println("In all");
			csvs.add("error during exec: \n" + e);
		}
		String listString = String.join("\n ", csvs);
		System.out.println(listString);

		writeTxtFile("final_results", listString);
//
//        //create csv file
//        try {
//            FileWriter writer = new FileWriter(new File(System.getProperty("user.dir")+"/data_projects.csv"));
////            writer.write("projectName,SHA,file,rank,DSC,WMC,DIT,CC,LCOM,MPC,NOM,RFC,DAC,NOCC,CBO,SIZE1,SIZE2,REFACTORED" + System.lineSeparator());
//
//            String listString = String.join("\n ", csvs);
//            writer.write(listString);
//            writer.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

System.out.println("Maybe here?");
	}



	public static String getDefaultBranchName(String pathDirPrj) {
		String branch = "";
		//System.out.println("Are you here?");
		try {
			Git git = Git.open(new File(pathDirPrj));
			branch = git.getRepository().getBranch();
			git.close(); // Close the Git repository
			//System.out.println("All good!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return branch;
	}

	public static void partedAnalysis(String projectName, String projectPath, List<CommitBeforeRef> commitArray, int currentCommit, int commitStep, List<CommitObj> commitIds) {
		int lastCommit = currentCommit + commitStep;
		if (lastCommit > commitArray.size()) {
			lastCommit = commitArray.size();
		}
	
		// Define CSV file path
		String filePath = projectName + "_refactoring_data.csv";
	
		try (FileWriter writer = new FileWriter(filePath)) {
			// Write CSV header
			writer.write("projectName,SHA,CommitNumber,fileName,RefactoringType\n");
	
			for (int comm = currentCommit; comm < lastCommit; comm++) {
				CommitBeforeRef commitBeforeRef = commitArray.get(comm);
				String sha = commitBeforeRef.getRefactoringCommit();
				String commitNumber = "UNKNOWN";  // Default value if not found
	
				// Compute Commit Number
				for (int i = 0; i < commitIds.size(); i++) {
					if (commitIds.get(i).getSha().equals(commitBeforeRef.getCommitBeforeRefactoring())) {
						commitNumber = String.valueOf(i + 1); // Use index as commit number
						break;
					}
				}
	
				// Ensure unique file names
				Set<String> uniqueFiles = new HashSet<>(commitBeforeRef.getInvolvedFilesBeforeRefactoring());
	
				for (String fileName : uniqueFiles) {
					for (String refactoringType : commitBeforeRef.getRefactoringTypes()) {
						writer.write(String.format("%s,%s,%s,%s,%s\n", projectName, sha, commitNumber, fileName, refactoringType));
					}
				}
			}
			System.out.println("CSV file saved: " + filePath);
		} catch (IOException e) {
			System.err.println("Error writing CSV: " + e.getMessage());
		}
	}
	


	public static String runAnalysis(String gitURL) {
		// Get url and name
		gitURL = gitURL.replace(".git", "");
		String projectName = gitURL.split("/")[gitURL.split("/").length - 1];
		String projectPath = System.getProperty("user.dir") + File.separator + projectName;
		String errorMesg = "";

		System.out.println("runAnalysis()");

		// Get refactorings
		List<CommitBeforeRef> commitBeforeRefs = new ArrayList<>();
		GitService gitService = new GitServiceImpl();
		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

		System.out.println("after git,miner()");
		ArrayList<String> refactoringTypesToKeep = new ArrayList<>(Arrays.asList("EXTRACT_METHOD","MOVE_METHOD", "PULL_UP_METHOD",
		"PUSH_DOWN_METHOD", "EXTRACT_SUPERCLASS", "EXTRACT_INTERFACE", "EXTRACT_AND_MOVE_METHOD", 
		"EXTRACT_CLASS", "MOVE_AND_RENAME_METHOD", "SPLIT_CLASS"));

		List<CommitObj> commitIds = new ArrayList<CommitObj>();
		try {
			Repository repo = gitService.cloneIfNotExists(projectName, gitURL);

			Git git = Git.open(new File(projectPath));
			System.out.println("afterGit.open()");
			commitIds = Utils.getCommitIds(git);
			System.out.println("Just before miner");
			System.out.println(projectPath);
			miner.detectAll(repo, null, new RefactoringHandler() {
				@Override
				public void handle(String commitId, List<Refactoring> refactorings) {
					System.out.println("In miner");
					List<String> refactoringTypes = new ArrayList<>();
					List<String> involvedFilesBeforeRefactoring = new ArrayList<>();
					if (!refactorings.isEmpty()) {
						// Create CommitBeforeRef
						for (Refactoring ref : refactorings) {
							if (!refactoringTypesToKeep.contains(ref.getRefactoringType().toString())) {
								continue;
							}

							refactoringTypes.add(ref.getRefactoringType().toString());
							System.out.println(ref.getRefactoringType().toString());
							System.out.println(ref.getInvolvedClassesBeforeRefactoring());
							for (ImmutablePair<String, String> immutablePair : ref
									.getInvolvedClassesBeforeRefactoring()) {
								involvedFilesBeforeRefactoring.add(immutablePair.left);
							}
						}
						if(!involvedFilesBeforeRefactoring.isEmpty()){
							CommitBeforeRef commitBeforeRef = new CommitBeforeRef(commitId, refactoringTypes,
									involvedFilesBeforeRefactoring);
							commitBeforeRefs.add(commitBeforeRef);
						}
					}
				}
			});
		} catch (Exception e) {
			System.out.println("In catch");
			errorMesg += e + "\n";
			System.out.println(e);
		}

		int commitStep = 5;
		
		String finalErrors = "***FINAL ERRORS";
		for(int commit=0; commit<commitBeforeRefs.size(); commit+=commitStep) {
			System.out.println("In catch3");
			try {
				System.out.println("\n\nRunning parted analysis for: "+projectName);
				partedAnalysis(projectName, projectPath, commitBeforeRefs, commit, commitStep, commitIds);
				System.out.println("Finished parted analysis for: "+projectName);
			}catch(Exception e) {
				finalErrors+="\n"+e.getMessage();
				System.out.println("In catch2");	
			}
		}
		
		return finalErrors;
	}
	
	public static void writeCSVFile(String fileName, String txt) {
		try {
			FileWriter writer = new FileWriter(new File(System.getProperty("user.dir") + "/" + fileName + ".csv"));
			writer.write(txt);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeTxtFile(String filename, String txt) {
		try {
			PrintWriter writer = new PrintWriter(filename + ".txt", "UTF-8");
			writer.println(txt);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}