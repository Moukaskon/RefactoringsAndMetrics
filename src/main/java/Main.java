package main.java;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
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
		projects.add("https://github.com/teomaik/DeRec-GEA.git");
//		projects.add("https://github.com/jagrosh/MusicBot");
//		projects.add("https://github.com/apache/maven-archetype");
//		projects.add("https://github.com/apache/commons-io");
//		projects.add("https://github.com/apache/commons-lang");
//		projects.add("https://github.com/apache/griffin");
//		projects.add("https://github.com/apache/johnzon");
//		projects.add("https://github.com/apache/openwebbeans");
//		projects.add("https://github.com/apache/unomi");
//		projects.add("https://github.com/apache/logging-flume");
//		projects.add("https://github.com/apache/commons-rdf");
//		projects.add("https://github.com/apache/giraph");
//		projects.add("https://github.com/docker-java/docker-java");
//		projects.add("https://github.com/Kaaz/DiscordBot");
//		projects.add("https://github.com/DiscordSRV/DiscordSRV");
//		projects.add("https://github.com/jagrosh/Vortex");
//		projects.add("https://github.com/Anuken/CoreBot");
//		projects.add("https://github.com/DenizenScript/dDiscordBot");
//		projects.add("https://github.com/wolfiabot/Wolfia");
//		projects.add("https://github.com/bumptech/glide");
//		projects.add("https://github.com/apache/dubbo");
//		projects.add("https://github.com/Moukaskon/InterfaceTest");
//		projects.add("https://github.com/Moukaskon/ExtractClass");
//		projects.add("https://github.com/Moukaskon/ExtractMeth");
//		projects.add("https://github.com/Moukaskon/MoveMeth"); !!!!!!!!!
//		projects.add("https://github.com/Moukaskon/MoveMethod"); !!!!!!!
//		projects.add("https://github.com/Moukaskon/SplitRef");  !!!!!!!!
//		projects.add("https://github.com/Moukaskon/PullUp");

		System.out.println("Number of Command Line Argument = " + args.length);
		int numOfCommits = 0;
		for (int i = 0; i < args.length; i++) {
			System.out.println("Command Line Argument" + i + "is" + args[i]);
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

		//create csv file
//        try {
//            FileWriter writer = new FileWriter(new File(System.getProperty("user.dir")+"/data_projects.csv"));
//            writer.write("projectName,SHA,file,rank,DSC,WMC,DIT,CC,LCOM,MPC,NOM,RFC,DAC,NOCC,CBO,SIZE1,SIZE2,REFACTORED" + System.lineSeparator());
//
//            String listString = String.join("\n ", csvs);
//            writer.write(listString);
//            writer.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

//		List<CommitObj> commitIds = new ArrayList<CommitObj>();
//		GitService gitService = new GitServiceImpl();
//		for (String prj : projects) {
//			String projectName = "Allprojects"+ File.separator + prj.split("/")[prj.split("/").length - 1];
//            try {
//                Repository repo = gitService.cloneIfNotExists(projectName, prj);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//			String projectPath = System.getProperty("user.dir") + File.separator + projectName;
//            Git git = null;
//            try {
//                git = Git.open(new File(projectPath));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            commitIds = Utils.getCommitIds(git);
//			numOfCommits += commitIds.size();
//		}


//		System.out.println("Number of commits in total = " + numOfCommits);
	}


	public static String runAnalysis(String gitURL) throws Exception {
		// Get url and name
		gitURL = gitURL.replace(".git", "");
		String projectName = "Allprojects" + File.separator + gitURL.split("/")[gitURL.split("/").length - 1];
		String projectPath = System.getProperty("user.dir") + File.separator + projectName;
		String errorMesg = "";

		System.out.println("runAnalysis()" + projectPath);

		// Get refactorings

		System.out.println("after git,miner()");

		List<CommitObj> commitIds = new ArrayList<CommitObj>();

		writeXlsxText(projectName, 5, gitURL, projectPath);

		int commitStep = 5;

		String finalErrors = "***FINAL ERRORS";
//		System.out.println("Here we goo" + commitBeforeRefs.size());
//		for (int commit = 0; commit < commitBeforeRefs.size(); commit += commitStep) {
//			try {
//				System.out.println("\n\nRunning parted analysis for: " + projectName);
//				//csvWriting(commitBeforeRefs, commitAfterRefs, projectName);
//				//partedAnalysis(projectName, projectPath, commitBeforeRefs, commit, commitStep, commitIds);
//				System.out.println("Finished parted analysis for: " + projectName);
//			} catch (Exception e) {
//				finalErrors += "\n" + e.getMessage();
//			}
//		}
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


	//LETS TRY XLSX

	public static void writeXlsxText(String projectName, int step, String gitURL, String projectPath) throws IOException, GitAPIException {

		int commitNumber = 0;
		GitService gitService = new GitServiceImpl();

		int x = commitNumber + step - 1;
		String filePath;
		Workbook workbook;
		Sheet sheet;

		sheet = null;
		workbook = null;

		// Prepare the file path
		filePath = "XLSXs" + File.separator +
				projectName.replace("Allprojects" + File.separator, "") +
				commitNumber + " - " + x + "_refactoring_data.xlsx";

		File file = new File(filePath);

//		if (!file.exists()) {
//			// Create new workbook and sheet
//			workbook = new XSSFWorkbook();
//			sheet = workbook.createSheet("Refactorings");
//
//			// Write header
//			Row header = sheet.createRow(0);
//			header.createCell(0).setCellValue("projectName");
//			header.createCell(1).setCellValue("SHA");
//			header.createCell(2).setCellValue("CommitNumber");
//			header.createCell(3).setCellValue("Files");
//			header.createCell(4).setCellValue("Refactored");
//			header.createCell(5).setCellValue("RefactoringType");
//			header.createCell(6).setCellValue("Affected Files");
//		}
//		else
//		{
//			// Open existing file
//			FileInputStream fis = new FileInputStream(file);
//			workbook = new XSSFWorkbook(fis);
//			sheet = workbook.getSheetAt(0);
//			fis.close();
//		}

		try {
			Repository repo = gitService.cloneIfNotExists(projectName, gitURL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.out.println("In there" + projectPath);
		Git git = Git.open(new File(projectPath));
		System.out.println("afterGit.open()");

		Iterable<RevCommit> commits = git.log().call();
		String sha;
		HashMap<String, Integer> fileList = new HashMap<>();
		ArrayList<Ref> refHandler;


		for (RevCommit commitSHA : commits) {
			Analysis analysis = new Analysis(projectPath);
			analysis.StartAnalysis();
			if(commitNumber % step == 0) {
				x = commitNumber + step - 1;
				filePath = "XLSXs" + File.separator +
						projectName.replace("Allprojects" + File.separator, "") +
						commitNumber + " - " + x + "_refactoring_data.xlsx";

				File startFile = new File(filePath);

				if (!startFile.exists()) {
					// Create new workbook and sheet
					workbook = new XSSFWorkbook();
					sheet = workbook.createSheet("Refactorings");

					// Write header
					Row header = sheet.createRow(0);
					header.createCell(0).setCellValue("projectName");
					header.createCell(1).setCellValue("SHA");
					header.createCell(2).setCellValue("CommitNumber");
					header.createCell(3).setCellValue("Files");
					header.createCell(4).setCellValue("Refactored");
					header.createCell(5).setCellValue("RefactoringType");
					header.createCell(6).setCellValue("Affected Files");
					header.createCell(7).setCellValue("DIT");
					header.createCell(8).setCellValue("CC");
					header.createCell(9).setCellValue("LCOM");
					header.createCell(10).setCellValue("MPC");
					header.createCell(11).setCellValue("NOM");
					header.createCell(12).setCellValue("RFC");
					header.createCell(13).setCellValue("DAC");
					header.createCell(14).setCellValue("NOCC");
					header.createCell(15).setCellValue("CBO");
					header.createCell(16).setCellValue("SIZE1");
				}else {
					// Open existing file
					FileInputStream fis = new FileInputStream(file);
					workbook = new XSSFWorkbook(fis);
					sheet = workbook.getSheetAt(0);
					fis.close();
				}
			}
			int lastRowNum = sheet.getLastRowNum() + 1;
			sha = commitSHA.getName();
			Repository repository = git.getRepository();
			RevWalk revWalk = new RevWalk(repository);
			RevCommit commit = revWalk.parseCommit(repository.resolve(commitSHA.getName()));
			RevTree tree = commit.getTree();
			HashMap<String, FileHandler> handlerListTest = new HashMap<>();

			try (TreeWalk treeWalk = new TreeWalk(repository)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				int index = 0;
				ArrayList<JavaFile> javaFiles = analysis.getJavaFiles();
				while (treeWalk.next()) {
					JavaFile javaFile = javaFiles.get(index);
					String path = treeWalk.getPathString();
					if (path.endsWith(".java")) {
						Row row = sheet.createRow(lastRowNum);
						fileList.put(path, lastRowNum);
						row.createCell(0).setCellValue(projectName);
						row.createCell(1).setCellValue(sha);
						row.createCell(2).setCellValue(commitNumber);
						row.createCell(3).setCellValue(path);
						row.createCell(4).setCellValue(0);
						row.createCell(7).setCellValue(javaFile.getDIT());
						row.createCell(8).setCellValue(javaFile.getCC());
						row.createCell(9).setCellValue(javaFile.getLCOM());
						row.createCell(10).setCellValue(javaFile.getMPC());
						row.createCell(11).setCellValue(javaFile.getNOM());
						row.createCell(12).setCellValue(javaFile.getRFC());
						row.createCell(13).setCellValue(javaFile.getDAC());
						row.createCell(14).setCellValue(javaFile.getNOCC());
						row.createCell(15).setCellValue(javaFile.getCBO());
						row.createCell(16).setCellValue(javaFile.getSIZE1());
						index++;
						lastRowNum++;

						handlerListTest.put(path, new FileHandler());
					}
				}
				System.out.println("Updating commit number");
				commitNumber++;

				refHandler = detectRefs(commitSHA.getName(), repository);

				for(int i = 0; i < refHandler.size(); i++) {
					ArrayList<String> commitBeforeRef = refHandler.get(i).getFilesBeforeRef();
					ArrayList<String> commitAfterRef = refHandler.get(i).getFilesAfterRef();
					String refName = refHandler.get(i).getRefactoringName();
					System.out.println("This is the ref: " + refName);
					for (int j = 0; j < commitBeforeRef.size(); j++) {
//					System.out.println("Is " + commitBeforeRef.get(j) + " in fileList: " + fileList.containsKey(commitBeforeRef.get(j)) +
//							" Keys: " + fileList.get(commitBeforeRef.get(j)));
						String fileName = commitBeforeRef.get(j);
						System.out.println(fileList.containsKey(fileName) + " " + fileName + " " + fileList);
						if (fileList.containsKey(fileName)) {
							int rowIndex = fileList.get(fileName);
							System.out.println(fileName + "\nIn here!! \n" + rowIndex);
							Row row = sheet.getRow(rowIndex);
							row.getCell(4).setCellValue(1);
							Cell cellRefName = row.getCell(5);
							if (cellRefName == null) {
								cellRefName = row.createCell(5);
							}

							cellRefName.setCellValue(refName + ";" + cellRefName.getStringCellValue());
							String allFilesInvolved = "";
							for(String fileInvolved: commitAfterRef){
								allFilesInvolved += fileInvolved + ";";
							}
							Cell cellInvolved = row.getCell(6);
							if (cellInvolved == null) {
								cellInvolved = row.createCell(6);
							}
							cellInvolved.setCellValue(allFilesInvolved + " | " + cellInvolved.getStringCellValue());
						}
					}
				}
			} catch (Exception e) {
				//To do
			}
			if(commitNumber % 5 == 0) {
				FileOutputStream fos = new FileOutputStream(filePath);
				workbook.write(fos);
				fos.close();
				workbook.close();
			}
		}
		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);
		fos.close();
		workbook.close();
	}






	private static void csvWritingTest(String projectName, int step, String gitURL, String projectPath) throws GitAPIException, IOException {
		int commitNumber = 0;
		int refNumber = 0;
		GitService gitService = new GitServiceImpl();

        try {
            Repository repo = gitService.cloneIfNotExists(projectName, gitURL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Git git = Git.open(new File(projectPath));
		System.out.println("afterGit.open()");

		Iterable<RevCommit> commits = git.log().call();
		String sha;
		HashMap<String, ArrayList<Ref>> fileList = new HashMap<>();
		FileWriter writer = null;
		ArrayList<Ref> refHandler;
		ArrayList<CommitBeforeRef> commitBeforeRefs;
		ArrayList<CommitAfterRef> commitAfterRefs;


		for (RevCommit commitSHA : commits) {
			commitNumber++;
			ArrayList<FileHandler> handlerList = new ArrayList<>();
			sha = commitSHA.getName();
			Repository repository = git.getRepository();
			RevWalk revWalk = new RevWalk(repository);
			RevCommit commit = revWalk.parseCommit(repository.resolve(commitSHA.getName()));
			RevTree tree = commit.getTree();
			HashMap<String, FileHandler> handlerListTest = new HashMap<>();

			try (TreeWalk treeWalk = new TreeWalk(repository)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);

				while (treeWalk.next()) {
					String path = treeWalk.getPathString();
					if (path.endsWith(".java")) {
						fileList.put(path, new ArrayList<>());
						handlerListTest.put(path, new FileHandler());
					}
				}
			} catch (Exception e) {
				//To do
			}
			int x = commitNumber + step - 1;
			String filePath = "CSVs" + File.separator + projectName.replace("Allprojects"
					+ File.separator, "") + commitNumber + " - " + x + "_refactoring_data.csv";

			System.out.println("Outside if filepath: " + filePath);

			if(writer == null) {
				System.out.println("Inside filepath if: " + filePath);
				writer = new FileWriter(filePath);
				writer.write("projectName,SHA,CommitNumber,Files,Refactored,RefactoringType,Affected Files\n");
			}

			refHandler = detectRefs(commitSHA.getName(), repository);
			System.out.println(refHandler);

			for(int i = 0; i < refHandler.size(); i++) {
				ArrayList<String> commitBeforeRef = refHandler.get(i).getFilesBeforeRef();
				String refName = refHandler.get(i).getRefactoringName();
				System.out.println("This is the ref: " + refName);
				String refactorings = "";
				String filesInvolved = "";
				for (int j = 0; j < commitBeforeRef.size(); j++) {
//					System.out.println("Is " + commitBeforeRef.get(j) + " in fileList: " + fileList.containsKey(commitBeforeRef.get(j)) +
//							" Keys: " + fileList.get(commitBeforeRef.get(j)));
					String fileName = commitBeforeRef.get(j);
					if(fileList.containsKey(fileName)) {
						ArrayList<Ref> tempRefs = fileList.get(fileName);
						tempRefs.add(refHandler.get(i));
						fileList.put(fileName, tempRefs);
					}

					//writer.write(String.format("%s,%s,\"%s\",%s,\"%s\"%n", projectName.replace("Allprojects" + File.separator, ""), sha, beforeJoined, ref, afterJoined));
				}
				System.out.println("fileList size: " + fileList.size());
				for (Map.Entry<String, ArrayList<Ref>> node : fileList.entrySet()) {
					ArrayList<Ref> refs = node.getValue();
					System.out.println("In the begining: " + node.getValue());
					for (Ref ref : refs) {
						refactorings += ref.getRefactoringName() + " ";
						filesInvolved = String.join(" ", ref.getFilesAfterRef());
						filesInvolved = filesInvolved + "\" ";
					}
					System.out.println("Refactorings to be written: " + refactorings + " for the file: " + node.getKey());
					System.out.println(handlerListTest.get(node.getKey()));
					if(handlerListTest.get(node.getKey()).getFilaPath().equals("")) {
						FileHandler aHandler = new FileHandler(refactorings, filesInvolved, commitSHA.getName(), node.getKey(), String.valueOf(commitNumber));
						handlerListTest.put(node.getKey(), aHandler);
					}else{
						FileHandler handle = handlerListTest.get(node.getKey());
						handle.setAll(filesInvolved, refactorings);
						handlerListTest.put(node.getKey(), handle);
					}
					System.out.println("At the end: " + node.getKey());
					fileList.put(node.getKey(), new ArrayList<>());
				}

			}
//			System.out.println("handlerList size: " + handlerList.size());
		for (Map.Entry<String, FileHandler> node : handlerListTest.entrySet()) {
//				System.out.println(handlerList.get(i).getRefactorings() + " Inside writing to excel: " + String.format("%s,%s,%s,%s,%s,%s,%n",
//						projectName.replace("Allprojects" + File.separator, ""),
//						sha, commitNumber, handlerList.get(i).getFilaPath(), handlerList.get(i).getRefactorings(),
//						handlerList.get(i).getFilesAfterRefs()));

			writer.write(String.format("%s,%s,%s,%s,%s,%s,%n", projectName.replace("Allprojects" +
					File.separator, ""), sha, commitNumber, node.getValue().getFilaPath(),
					node.getValue().getRefactorings(), node.getValue().getFilesAfterRefs()));
		}

		if(commitNumber % 5 == 0){
			writer = null;
		}

//			try (TreeWalk treeWalk = new TreeWalk(repository)) {
//				treeWalk.addTree(tree);
//				treeWalk.setRecursive(true);
//
//				while (treeWalk.next()) {
//					String path = treeWalk.getPathString();
//					if (path.endsWith(".java")) {
//						fileList.add(path);
//					}
//				}
//			} catch (Exception e) {
//				//To do
//			}
//			int x = commitNumber + step - 1;
//			String filePath = "CSVs" + File.separator + projectName.replace("Allprojects"
//					+ File.separator, "") + commitNumber + " - " + x + "_refactoring_data.csv";
//			if(writer == null) {
//				writer = new FileWriter(filePath, true);
//				writer.write("projectName,SHA,CommitNumber,Files,Refactored,RefactoringType,Affected Files\n");
//			}
//			try {
//
//				for (String file : fileList) {
//					writer.write(String.format("%s,%s,%s,%s,%n", projectName.replace("Allprojects" +
//							File.separator, ""), sha, commitNumber, file));
//				}
//
//				fileList.clear();
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//			if (commitNumber % step == 0 && writer != null) {
//				try {
//					writer.close();
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//				writer = null;
//			}

		}

	}

	public static ArrayList<Ref> detectRefs(String commitSHA, Repository repo) throws GitAPIException, IOException {

		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
		ArrayList<Ref> refList = new ArrayList<>();
		ArrayList<String> refactoringTypesToKeep = new ArrayList<>(Arrays.asList("EXTRACT_SUPERCLASS",
				"EXTRACT_INTERFACE", "EXTRACT_CLASS", "MOVE_AND_RENAME_OPERATION", "SPLIT_CLASS",
				"EXTRACT_OPERATION", "MOVE_OPERATION", "PULL_UP_OPERATION", "EXTRACT_AND_MOVE_OPERATION"));
		miner.detectAtCommit(repo, commitSHA, new RefactoringHandler() {
			@Override
			public void handle(String commitId, List<Refactoring> refactorings) {
				System.out.println(commitId);
				if (!refactorings.isEmpty()) {
					for (Refactoring ref : refactorings) {
						// System.out.println("\n\n" + ref.getRefactoringType().toString() + "\n\n\n");
						// try (BufferedWriter writer = new BufferedWriter(new FileWriter("refactorings_output3.txt", true))) {
						// 	writer.write(ref.getRefactoringType().toString() + "\n");
						// 	writer.write("-----\n"); // separator between commits
						// } catch (IOException e) {
						// 	e.printStackTrace();
						// }
						//System.out.println(ref);
						if (!refactoringTypesToKeep.contains(ref.getRefactoringType().toString())) {
							continue;
						}

						Ref aRef = new Ref(ref.getRefactoringType().toString(), commitSHA);
						//System.out.println("\n\n\n" + ref.getInvolvedClassesBeforeRefactoring() + "\n\n\n");

						for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesBeforeRefactoring()) {
							//System.out.println(immutablePair + "1st\n\n\n");
							aRef.addBeforeRefFile(immutablePair.left);
						}

						for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesAfterRefactoring()) {
							//System.out.println(immutablePair + "2nd\n\n\n");
							aRef.addAfterRefFile(immutablePair.left);
						}
						refList.add(aRef);
					}
				}
				//System.out.println("\n\n\n End miner \n\n\n");
				//System.out.println(commitAfterRefs + "\n\n\n" + commitBeforeRefs + "\n\n\n");
			}
		});
		return refList;
	}


//  This is how we work with detect all. Change the detectAtCommit with detect all and you are almost set (you have to change the arguments as well)
//	miner.detectAtCommit(repo, commitSHA, new RefactoringHandler() {
//		@Override
//		public void handle(String commitId, List<Refactoring> refactorings) {
//			System.out.println(commitId);
//			if (!refactorings.isEmpty()) {
//				// Create CommitBeforeRef
//
//				List<String> refactoringTypes = new ArrayList<>();
//				List<List<String>> involvedFilesBeforeRefactoring = new ArrayList<>();
//				List<List<String>> involvedFilesAfterRefactoring = new ArrayList<>();
//
//				for (Refactoring ref : refactorings) {
//					// System.out.println("\n\n" + ref.getRefactoringType().toString() + "\n\n\n");
//					// try (BufferedWriter writer = new BufferedWriter(new FileWriter("refactorings_output3.txt", true))) {
//					// 	writer.write(ref.getRefactoringType().toString() + "\n");
//					// 	writer.write("-----\n"); // separator between commits
//					// } catch (IOException e) {
//					// 	e.printStackTrace();
//					// }
//					//System.out.println(ref);
//					if (!refactoringTypesToKeep.contains(ref.getRefactoringType().toString())) {
//						continue;
//					}
//
//					boolean hadRef = false;
//					int index = 0;
//					System.out.println("\n\n\n" + ref.getInvolvedClassesBeforeRefactoring() + "\n\n\n");
//					refactoringTypes.add(ref.getRefactoringType().toString());
//					involvedFilesBeforeRefactoring.add(new ArrayList<>());
//					involvedFilesAfterRefactoring.add(new ArrayList<>());
//
//					for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesBeforeRefactoring()) {
//						System.out.println(immutablePair + "1st\n\n\n");
//						hadRef = true;
//						involvedFilesBeforeRefactoring.get(involvedFilesBeforeRefactoring.size() - 1).add(immutablePair.left);
//
//					}
//
//
//					for (ImmutablePair<String, String> immutablePair : ref.getInvolvedClassesAfterRefactoring()) {
//						System.out.println(immutablePair + "2nd\n\n\n");
//						involvedFilesAfterRefactoring.get(involvedFilesAfterRefactoring.size() - 1).add(immutablePair.left);
//					}
//
//
//
//				}
//				// Parallel lists, might refactor later. Refactorings list is parallel with commitBeforeRefs and commitAfterRefs
//				if(!involvedFilesBeforeRefactoring.isEmpty()){
//					CommitBeforeRef commitBeforeRef = new CommitBeforeRef(commitId, refactoringTypes,
//							involvedFilesBeforeRefactoring);
//					commitBeforeRefs.add(commitBeforeRef);
//				}
//
//				if(!involvedFilesAfterRefactoring.isEmpty() && !involvedFilesBeforeRefactoring.isEmpty()){
//					CommitAfterRef commitAfterRef = new CommitAfterRef(commitId, refactoringTypes,
//							involvedFilesAfterRefactoring);
//					commitAfterRefs.add(commitAfterRef);
//				}
//			}
//			//System.out.println("\n\n\n End miner \n\n\n");
//		}
//	});
//}
}