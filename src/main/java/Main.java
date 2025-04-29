import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		//projects.add("https://github.com/apache/maven-archetype");
		//projects.add("https://github.com/apache/commons-io");
		//projects.add("https://github.com/apache/commons-lang");
		//projects.add("https://github.com/apache/griffin");
		// projects.add("https://github.com/apache/johnzon");
		// projects.add("https://github.com/apache/openwebbeans");
		// projects.add("https://github.com/apache/unomi");
		// projects.add("https://github.com/apache/logging-flume");
		// projects.add("https://github.com/apache/commons-rdf");
		// projects.add("https://github.com/apache/giraph");
		//projects.add("https://github.com/docker-java/docker-java");
		//projects.add("https://github.com/Kaaz/DiscordBot");
		//projects.add("https://github.com/DiscordSRV/DiscordSRV");
		//projects.add("https://github.com/jagrosh/Vortex");
		//projects.add("https://github.com/Anuken/CoreBot");
		//projects.add("https://github.com/DenizenScript/dDiscordBot");
		//projects.add("https://github.com/wolfiabot/Wolfia");
		//projects.add("https://github.com/elastic/elasticsearch"); //Big one 86.000 comits
		projects.add("https://github.com/Anuken/Mindustry"); // Game, big one

		
		
		System.out.println("Number of Command Line Argument = " + args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.println(String.format("Command Line Argument %d is %s", i, args[i]));
			projects.add(args[i]);
		}

		try {
			for (String prj : projects) {
				csvs.add(runAnalysis(prj));
			}
		} catch (Exception e) {
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

	}


	public static String runAnalysis(String gitURL) {
		// Get url and name
		gitURL = gitURL.replace(".git", "");
		String projectName = "Allprojects"+ File.separator + gitURL.split("/")[gitURL.split("/").length - 1];
		String projectPath = System.getProperty("user.dir") + File.separator + projectName;
		String errorMesg = "";

		System.out.println("runAnalysis()");

		// Get refactorings
		List<CommitBeforeRef> commitBeforeRefs = new ArrayList<>();
		List<CommitAfterRef> commitAfterRefs = new ArrayList<>();
		GitService gitService = new GitServiceImpl();
		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

		System.out.println("after git,miner()");
		ArrayList<String> refactoringTypesToKeep = new ArrayList<>(Arrays.asList("EXTRACT_METHOD","MOVE_METHOD",
		"PULL_UP_METHOD","EXTRACT_SUPERCLASS", "EXTRACT_INTERFACE", "EXTRACT_AND_MOVE_METHOD", "EXTRACT_CLASS",
		"MOVE_AND_RENAME_METHOD", "SPLIT_CLASS"));

		List<CommitObj> commitIds = new ArrayList<CommitObj>();
		try {
			Repository repo = gitService.cloneIfNotExists(projectName, gitURL);

			Git git = Git.open(new File(projectPath));
			System.out.println("afterGit.open()");
			
			commitIds = Utils.getCommitIds(git);

			miner.detectAll(repo, null, new RefactoringHandler() {
				@Override
				public void handle(String commitId, List<Refactoring> refactorings) {
					if (!refactorings.isEmpty()) {
						// Create CommitBeforeRef
						List<String> refactoringTypes = new ArrayList<>();
						List<List<String>> involvedFilesBeforeRefactoring = new ArrayList<>();
						List<List<String>> involvedFilesAfterRefactoring = new ArrayList<>();
						
						for (Refactoring ref : refactorings) {
							if (!refactoringTypesToKeep.contains(ref.getRefactoringType().toString())) {
								continue;
							}

							boolean hadRef = false;
							int index = 0;
							System.out.println("\n\n\n" + ref.getInvolvedClassesBeforeRefactoring() + "\n\n\n");
							refactoringTypes.add(ref.getRefactoringType().toString());
							involvedFilesBeforeRefactoring.add(new ArrayList<>());
							involvedFilesAfterRefactoring.add(new ArrayList<>());
							for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesBeforeRefactoring()) {
								System.out.println(immutablePair + "1st\n\n\n");
								hadRef = true;
								involvedFilesBeforeRefactoring.get(involvedFilesBeforeRefactoring.size() - 1).add(immutablePair.left);

							}

							
							for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesAfterRefactoring()) {
								System.out.println(immutablePair + "2nd\n\n\n");
								involvedFilesAfterRefactoring.get(involvedFilesAfterRefactoring.size() - 1).add(immutablePair.left);
							}
							

							
						}
						// Parallel lists, might refactor later. Refactorings list is parallel with commitBeforeRefs and commitAfterRefs
						if(!involvedFilesBeforeRefactoring.isEmpty()){
							CommitBeforeRef commitBeforeRef = new CommitBeforeRef(commitId, refactoringTypes,
									involvedFilesBeforeRefactoring);
							commitBeforeRefs.add(commitBeforeRef);
						}

						if(!involvedFilesAfterRefactoring.isEmpty() && !involvedFilesBeforeRefactoring.isEmpty()){
							CommitAfterRef commitAfterRef = new CommitAfterRef(commitId, refactoringTypes,
									involvedFilesAfterRefactoring);
							commitAfterRefs.add(commitAfterRef);
						}
					}
					//System.out.println("\n\n\n End miner \n\n\n");

				}
			});
		} catch (Exception e) {
			errorMesg += e + "\n";
			System.out.println(e);
		}

		int commitStep = 5;
		
		String finalErrors = "***FINAL ERRORS";
		System.out.println("Here we goo" + commitBeforeRefs.size());
		for(int commit=0; commit<commitBeforeRefs.size(); commit+=commitStep) {
			try {
				System.out.println("\n\nRunning parted analysis for: "+projectName);
				csvWriting(commitBeforeRefs, commitAfterRefs, projectName);
				//partedAnalysis(projectName, projectPath, commitBeforeRefs, commit, commitStep, commitIds);
				System.out.println("Finished parted analysis for: "+projectName);
			}catch(Exception e) {
				finalErrors+="\n"+e.getMessage();	
			}
		}
		
		return finalErrors;
	}


	public static String getDefaultBranchName(String pathDirPrj) {
		String branch = "";
		try {
			Git git = Git.open(new File(pathDirPrj));
			branch = git.getRepository().getBranch();
			git.close(); // Close the Git repository
		} catch (Exception e) {
			e.printStackTrace();
		}
		return branch;
	}

	// public static void partedAnalysis(String projectName, String projectPath, List<CommitBeforeRef> commitArray, int currentCommit, int commitStep, List<CommitObj> commitIds){


	// 	String errorMesg = "";
		
	// 	int lastCommit = currentCommit+commitStep;
	// 	if(lastCommit>commitArray.size()) {
	// 		lastCommit=commitArray.size();
	// 	}
	// 	try {
	// 		for(int comm = currentCommit; comm<(lastCommit); comm++){
	        	
	//             CommitBeforeRef commitBeforeRef = commitArray.get(comm);
	            
	//             String previousSha = Utils.findPreviousSha(commitBeforeRef.getRefactoringCommit(),projectName);
	//             commitBeforeRef.setCommitBeforeRefactoring(previousSha);

	//             //Checkout previous SHA
	//             Utils.checkoutGitProject(projectName,previousSha);

	//             //Analyze commit and save
	//             // Analysis analysis = new Analysis(projectPath);
	//             // analysis.StartAnalysis();
	//             // commitBeforeRef.setAnalysis(analysis);

	//         }

	//         //gather data for Csv file
	//         ArrayList<String> csvLines = new ArrayList();
	// 		csvLines.add("projectName,SHA,CommitNumber,file,DIT,CC,LCOM,MPC,NOM,RFC,DAC,NOCC,CBO,SIZE1,REFACTORED");
	//         String cwdPath = System.getProperty("user.dir");

	//         for(int comm = currentCommit; comm<(lastCommit); comm++){
	//         	System.out.println("++++++++++++Wrinting commit "+comm+" / "+(commitArray.size()-1));
	//             CommitBeforeRef commitBeforeRef = commitArray.get(comm);
	            
	//             // Analysis tempAnalysis = commitBeforeRef.getAnalysis();
	//             // ArrayList<JavaFile> javaFiles = tempAnalysis.getJavaFiles();
	//             Hashtable<String, String> classes = new Hashtable<String, String>();
				
	// 			ArrayList<String> refactoredClasses = new ArrayList<>();
	// 			Set<String> set = new HashSet<>(commitBeforeRef.getInvolvedFilesBeforeRefactoring());
	// 			refactoredClasses.addAll(set);

	// 			String commitNumber = "";
	// 			for(int i=0; i<commitIds.size(); i++) {
	// 				if(!commitIds.get(i).getSha().equals(commitBeforeRef.getCommitBeforeRefactoring())){
	// 					continue;
	// 				}

	// 				commitNumber = "" + (i+1);
	// 				break;
	// 			}

	// 			String filePath = projectName + "_refactoring_data.csv";

	// 			// for( JavaFile tempFile: javaFiles){
	//             //     String line = projectName + ","+commitBeforeRef.getCommitBeforeRefactoring()+","+commitNumber;

	//             //     String filePath = tempFile.getPath().replace(File.separator, "/");
	//             //     filePath = filePath.replaceFirst("/", "");
				
	// 			// 	if (!refactoredClasses.contains(filePath)) {
	// 			// 		continue;
	// 			// 	}
	
	// 			// 	line += "," + filePath;
	// 			// 	line += "," + tempFile.getDIT(); //
	// 			// 	line += "," + tempFile.getCC();//
	// 			// 	line += "," + tempFile.getLCOM();//
	// 			// 	line += "," + tempFile.getMPC();//
	// 			// 	line += "," + tempFile.getNOM();//
	// 			// 	line += "," + tempFile.getRFC();//
	// 			// 	line += "," + tempFile.getDAC();//
	// 			// 	line += "," + tempFile.getNOCC();//
	// 			// 	line += "," + tempFile.getCBO();//
	// 			// 	line += "," + tempFile.getSIZE1();//
	// 			// 	line += "," + "1";	//Refactored
	// 			// 	classes.put(filePath, line);

	//             //     System.out.println(line);
	//             // }

	//             classes.forEach((k, ln) -> {
	//                 csvLines.add(ln);
	//             });
	            
	//         }

	//         String join = String.join("\n ", csvLines);
	        
	//         //end of correct code
	//         String result = "";
	//         //temporary code for first analysis
	//         try {
	//             FileWriter writer = new FileWriter(new File(System.getProperty("user.dir")+"/data_"+projectName+"_"+currentCommit+"-"+lastCommit+".csv"));
	//             writer.write(join);
	//             writer.close();
	//             writeTxtFile(projectName+"_error_msg", "done \n"+errorMesg);
	//             result= projectName+" true!";
	//         } catch (Exception e) {

	//             errorMesg += e+"\n";
	//             writeTxtFile(projectName+"_error_msg", "failed \n"+errorMesg);
	//             result= projectName+" false! \n"+e;
	//         }
	        
	// 	}catch(Exception e) {
	// 		errorMesg += "\n"+e.getMessage();
	// 		writeTxtFile(projectName+"_fatal_error_msg", "done \n"+errorMesg);
	// 	}
        
    //     for(int comm = currentCommit; comm<lastCommit; comm++){
    //     	System.out.println("**********deleting commit "+comm+" / "+(commitArray.size()-1));
    //         CommitBeforeRef commitBeforeRef = commitArray.get(comm);
    //         commitBeforeRef.destroyMe();

    //     }
        
    //     //--------------------------------------------------------------------------------------------------------------
        
    // }
	
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

    private static void csvWriting(List<CommitBeforeRef> commitBeforeRefs, List<CommitAfterRef> commitAfterRefs, String projectName) {
        String filePath = "CSVs" + File.separator + projectName.replace("Allprojects" + File.separator, "") + "_refactoring_data.csv";

		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write("projectName,SHA,Files Before ref,RefactoringType,files After\n");

			System.out.println(commitBeforeRefs.size() + "Size");
	
			for (int comm = 0; comm < commitBeforeRefs.size(); comm++) {
				CommitBeforeRef commitBeforeRef = commitBeforeRefs.get(comm);
				CommitAfterRef commitAfterRef = commitAfterRefs.get(comm);
				String sha = commitBeforeRef.getRefactoringCommit();

				List<String> refs = commitBeforeRef.getRefactoringTypes();
				int size = refs.size();

				// for (int i = 0; i < size; i++) {
				// 	writer.write(String.format("%s,%s,%s,%s,%s%n", projectName, sha, 
				// 	commitBeforeRef.getInvolvedFilesBeforeRefactoring().get(i), refs.get(i), 
				// 	commitAfterRef.getInvolvedFilesAfterRefactoring().get(i)));
				// }


				for (int i = 0; i < size; i++) {
					List<String> beforeFiles = commitBeforeRef.getInvolvedFilesBeforeRefactoring().get(i);
					String ref = refs.get(i);
					List<String> afterFiles = commitAfterRef.getInvolvedFilesAfterRefactoring().get(i);
				
					String beforeJoined = String.join("; ", beforeFiles);
					String afterJoined = String.join("; ", afterFiles);
				
					writer.write(String.format("%s,%s,\"%s\",%s,\"%s\"%n", projectName.replace("Allprojects" + File.separator, ""), sha, beforeJoined, ref, afterJoined));
				}


			}
			System.out.println("CSV file saved: " + filePath);
		} catch (IOException e) {
			System.out.println("Error writing CSV: " + e.getMessage());
		}
    }

}