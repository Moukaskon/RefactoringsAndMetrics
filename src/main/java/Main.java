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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
//		projects.add("https://github.com/teomaik/DeRec-GEA.git");
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

		System.out.println("Command Line Argument length: " + args.length);
		projects.add(args[0]);
		String startingCommit = null;
		int startingCommitNumber = 0;
		if (args.length == 2) {
			startingCommit = args[1];
		} else if (args.length == 3) {
			startingCommitNumber = Integer.parseInt(args[2]);
		}

		try {
			for (String prj : projects) {
				csvs.add(runAnalysis(prj, startingCommit, startingCommitNumber));
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


	public static String runAnalysis(String gitURL, String startingCommit, int startingCommitNumber) throws Exception {
		// Get url and name
		gitURL = gitURL.replace(".git", "");
		String projectName = "Allprojects" + File.separator + gitURL.split("/")[gitURL.split("/").length - 1];
		String projectPath = System.getProperty("user.dir") + File.separator + projectName;
		String errorMesg = "";

		System.out.println("runAnalysis()" + projectPath);

		// Get refactorings

		System.out.println("after git,miner()");

		List<CommitObj> commitIds = new ArrayList<CommitObj>();

		writeXlsxText(projectName, 5, gitURL, projectPath, startingCommit, startingCommitNumber);

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

	public static String normalizePath(String path) {
		// Remove leading slashes, convert all to forward slashes, lowercase
		String norm = path.replace("\\", "/").replaceAll("^/+", "").toLowerCase();
		return norm;
	}

	public static void writeXlsxText(String projectName, int step, String gitURL, String projectPath, String startCommitSHA, int commitNumberArg) throws IOException, GitAPIException {

		int commitNumber = commitNumberArg;

		GitService gitService = new GitServiceImpl();

		try {
			Repository repo = gitService.cloneIfNotExists(projectName, gitURL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Git git = Git.open(new File(projectPath));

		Repository repository = git.getRepository();

		Iterable<RevCommit> commitsIterable;

		boolean reverseCommits = false;

		if (startCommitSHA != null) {

			AnyObjectId startCommit = repository.resolve(startCommitSHA);
			if (startCommit == null) {
				throw new IllegalArgumentException("Invalid start commit: " + startCommitSHA);
			}

			AnyObjectId headCommit = repository.resolve("HEAD");

			commitsIterable = git.log()
					.addRange(startCommit, headCommit)
					.call();
		} else {
			commitsIterable = git.log().call();
			reverseCommits = true;
		}

		List<RevCommit> commits = new ArrayList<>();
		for (RevCommit c : commitsIterable) {
			commits.add(c);
		}

		if (reverseCommits) {
			Collections.reverse(commits);
		}


		String sha;
		HashMap<String, Integer> fileList = new HashMap<>();
		ArrayList<Ref> refHandler;

		String branchName = getDefaultBranchName(projectName);

		Workbook workbook = null;
		Sheet sheet = null;
		String filePath = null;
		int batchStart = 0;
		Iterator<RevCommit> commitIter = commits.iterator();
		RevCommit commitSHA = commitIter.next();
		RevCommit previousCommit = null;
		Row previousSummaryRow = null;

		while (true) {
			try {
				if (workbook == null || commitNumber % step == 0) {
					if (workbook != null) {
						try (FileOutputStream fos = new FileOutputStream(filePath)) {
							workbook.write(fos);
						}
						workbook.close();
					}

					batchStart = commitNumber;
					int batchEnd = batchStart + step - 1;
					String cleanProjectName = projectName.replace("Allprojects" + File.separator, "");

					File projectDir = new File("XLSXs" + File.separator + cleanProjectName);
					if (!projectDir.exists()) {
						projectDir.mkdirs();
					}

					filePath = projectDir.getPath() + File.separator +
							batchStart + " - " + batchEnd + "_refactoring_data.xlsx";

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
					header.createCell(7).setCellValue("WMC");
					header.createCell(8).setCellValue("DIT");
					header.createCell(9).setCellValue("NOCC");
					header.createCell(10).setCellValue("CBO");
					header.createCell(11).setCellValue("RFC");
					header.createCell(12).setCellValue("LCOM");
					header.createCell(13).setCellValue("WMC*");
					header.createCell(14).setCellValue("NOM");
					header.createCell(15).setCellValue("MPC");
					header.createCell(16).setCellValue("DAC");
					header.createCell(17).setCellValue("SIZE1");
					header.createCell(18).setCellValue("SIZE2");
					header.createCell(19).setCellValue("DSC");
					header.createCell(20).setCellValue("NOH");
					header.createCell(21).setCellValue("ANA");
					header.createCell(22).setCellValue("DAM");
					header.createCell(23).setCellValue("DCC");
					header.createCell(24).setCellValue("CAMC");
					header.createCell(25).setCellValue("MOA");
					header.createCell(26).setCellValue("MFA");
					header.createCell(27).setCellValue("NOP");
					header.createCell(28).setCellValue("CIS");
					header.createCell(29).setCellValue("NPM");
					header.createCell(30).setCellValue("Reusability");
					header.createCell(31).setCellValue("Flexibility");
					header.createCell(32).setCellValue("Understandability");
					header.createCell(33).setCellValue("Functionality");
					header.createCell(34).setCellValue("Extendibility");
					header.createCell(35).setCellValue("Effectiveness");
					header.createCell(36).setCellValue("FanIn");
					header.createCell(37).setCellValue("AllChangedFiles");
					header.createCell(38).setCellValue("Balance");
					header.createCell(39).setCellValue("Equilibrium");
					header.createCell(40).setCellValue("Density");
					header.createCell(41).setCellValue("Regularity");
					header.createCell(42).setCellValue("Rhythm");
					header.createCell(43).setCellValue("Sequence");
					header.createCell(44).setCellValue("Simplicity");
					header.createCell(45).setCellValue("Symmetry");
				}

				git.checkout().setName(commitSHA.getName()).call();

				RevWalk revWalk = new RevWalk(repository);
				RevCommit currentCommit = revWalk.parseCommit(repository.resolve(commitSHA.getName()));

				Analysis analysis = new Analysis(projectPath);
				analysis.StartAnalysis();
				ArrayList<JavaFile> javaFiles = analysis.getJavaFiles();
				HashMap<String, JavaFile> pathToJavaFile = new HashMap<>();
				for (JavaFile jf : javaFiles) {
					String normPath = normalizePath(jf.getPath());
					pathToJavaFile.put(normPath, jf);
				}

				if (previousCommit != null && previousSummaryRow != null) {

					List<String> changedFiles = new ArrayList<>();

					try (ObjectReader reader = repository.newObjectReader()) {

						CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
						CanonicalTreeParser newTreeIter = new CanonicalTreeParser();

						oldTreeIter.reset(reader, previousCommit.getTree());
						newTreeIter.reset(reader, currentCommit.getTree());

						DiffFormatter diffFormatter =
								new DiffFormatter(DisabledOutputStream.INSTANCE);
						diffFormatter.setRepository(repository);

						List<DiffEntry> diffs =
								diffFormatter.scan(oldTreeIter, newTreeIter);

						for (DiffEntry entry : diffs) {
							String path = entry.getNewPath();
							if (path.endsWith(".java")) {
								changedFiles.add(normalizePath(path));
							}
						}
					}

					if (!changedFiles.isEmpty()) {
						String str = String.join(";", changedFiles);
						if(str.length() > 30000){
							str = str.substring(0, 30000);
						}
						previousSummaryRow
								.createCell(37)
								.setCellValue(str);
					}
				}

				int summaryRowIndex = sheet.getLastRowNum() + 1;
				Row summaryRow = sheet.createRow(summaryRowIndex);

				summaryRow.createCell(0).setCellValue(projectName);
				summaryRow.createCell(1).setCellValue(commitSHA.getName());
				summaryRow.createCell(2).setCellValue(commitNumber);

				fileList.clear();

				sha = commitSHA.getName();
				// Old code
				//			Repository repository = git.getRepository();
				//			RevWalk revWalk = new RevWalk(repository);
				//			RevCommit commit = revWalk.parseCommit(repository.resolve(commitSHA.getName()));
				RevTree tree = currentCommit.getTree();
				HashMap<String, FileHandler> handlerListTest = new HashMap<>();

				int lastRowNum = summaryRowIndex + 1;

				try (TreeWalk treeWalk = new TreeWalk(repository)) {
					treeWalk.addTree(tree);
					treeWalk.setRecursive(true);
					while (treeWalk.next()) {
						String path = normalizePath(treeWalk.getPathString());
						if (!path.endsWith(".java")) continue;
						JavaFile javaFile = pathToJavaFile.get(path);
						if (javaFile == null) {
							System.out.println("No JavaFile found for: " + path);
							continue;
						}
						Row row = sheet.createRow(lastRowNum);
						fileList.put(path, lastRowNum);
						row.createCell(0).setCellValue(projectName);
						row.createCell(1).setCellValue(sha);
						row.createCell(2).setCellValue(commitNumber);
						row.createCell(3).setCellValue(path);
						row.createCell(4).setCellValue(0);
						row.createCell(7).setCellValue(javaFile.getWMC());
						row.createCell(8).setCellValue(javaFile.getDIT());
						row.createCell(9).setCellValue(javaFile.getNOCC());
						row.createCell(10).setCellValue(javaFile.getCBO());
						row.createCell(11).setCellValue(javaFile.getRFC());
						row.createCell(12).setCellValue(javaFile.getLCOM());
						row.createCell(13).setCellValue(javaFile.getWMCStar());
						row.createCell(14).setCellValue(javaFile.getNOM());
						row.createCell(15).setCellValue(javaFile.getMPC());
						row.createCell(16).setCellValue(javaFile.getDAC());
						row.createCell(17).setCellValue(javaFile.getSIZE1());
						row.createCell(18).setCellValue(javaFile.getSIZE2());
						row.createCell(19).setCellValue(javaFile.getDSC());
						row.createCell(20).setCellValue(javaFile.getNOH());
						row.createCell(21).setCellValue(javaFile.getANA());
						row.createCell(22).setCellValue(javaFile.getDAM());
						row.createCell(23).setCellValue(javaFile.getDCC());
						row.createCell(24).setCellValue(javaFile.getCAMC());
						row.createCell(25).setCellValue(javaFile.getMOA());
						row.createCell(26).setCellValue(javaFile.getMFA());
						row.createCell(27).setCellValue(javaFile.getNOP());
						row.createCell(28).setCellValue(javaFile.getCIS());
						row.createCell(29).setCellValue(javaFile.getNPM());
						row.createCell(30).setCellValue(javaFile.getReusability());
						row.createCell(31).setCellValue(javaFile.getFlexibility());
						row.createCell(32).setCellValue(javaFile.getUnderstandability());
						row.createCell(33).setCellValue(javaFile.getFunctionality());
						row.createCell(34).setCellValue(javaFile.getExtendibility());
						row.createCell(35).setCellValue(javaFile.getEffectiveness());
						row.createCell(36).setCellValue(javaFile.getFanIn());
						String fullPath = new File(projectPath, path).getAbsolutePath();
						BeautyMetrics bm = runPythonForFile(fullPath);

						if(bm != null){
							row.createCell(38).setCellValue(bm.getBalance());
							row.createCell(39).setCellValue(bm.getEquilibrium());
							row.createCell(40).setCellValue(bm.getDensity());
							row.createCell(41).setCellValue(bm.getRegularity());
							row.createCell(42).setCellValue(bm.getRhythm());
							row.createCell(43).setCellValue(bm.getSequence());
							row.createCell(44).setCellValue(bm.getSimplicity());
							row.createCell(45).setCellValue(bm.getSymmetry());
						}


						lastRowNum++;
						handlerListTest.put(path, new FileHandler());
					}
					previousCommit = currentCommit;
					previousSummaryRow = summaryRow;
					commitNumber++;
					if (!commitIter.hasNext()) {
						break;
					}
					commitSHA = commitIter.next();

					refHandler = detectRefs(commitSHA.getName(), repository);
					if (refHandler != null && !refHandler.isEmpty()) {
						for (int i = 0; i < refHandler.size(); i++) {
							ArrayList<String> commitBeforeRef = refHandler.get(i).getFilesBeforeRef();
							ArrayList<String> commitAfterRef = refHandler.get(i).getFilesAfterRef();
							String refName = refHandler.get(i).getRefactoringName();
							for (int j = 0; j < commitBeforeRef.size(); j++) {
								String fileNameTemp = commitBeforeRef.get(j);
								String fileName = normalizePath(fileNameTemp);
								if (fileList.containsKey(fileName)) {
									int rowIndex = fileList.get(fileName);
									Row row = sheet.getRow(rowIndex);
									row.getCell(4).setCellValue(1);
									Cell cellRefName = row.getCell(5);
									if (cellRefName == null) {
										cellRefName = row.createCell(5);
									}
									String oldValue = cellRefName.getStringCellValue();
									if (oldValue == null) oldValue = "";
									cellRefName.setCellValue(refName + ";" + oldValue);
									String allFilesInvolved = "";
									for (String fileInvolved : commitAfterRef) {
										allFilesInvolved += fileInvolved + ";";
									}
									Cell cellInvolved = row.getCell(6);
									if (cellInvolved == null) {
										cellInvolved = row.createCell(6);
									}
									String oldValue2 = cellInvolved.getStringCellValue();
									if (oldValue2 == null) oldValue2 = "";
									cellInvolved.setCellValue(refName + ";" + oldValue2);
									cellInvolved.setCellValue(allFilesInvolved + " | " + cellInvolved.getStringCellValue());
								}
							}
						}
					}

					List<String> changedFiles = new ArrayList<>();
				} catch (Exception e) {
					System.out.println("An error occurred in the while loop" + e);
				}
			} catch (Exception e) {
				System.err.println(
						"Skipping commit " + commitSHA.getName() +
								" due to error: " + e.getMessage()
				);
				e.printStackTrace();
				commitNumber++;
			}

		}
		if (workbook != null) {
			try (FileOutputStream fos = new FileOutputStream(filePath)) {
				workbook.write(fos);
			}
			workbook.close();
		}

		git.checkout().setName(branchName).call();
	}

	public static BeautyMetrics runPythonForFile(String filePath) {

		try {

			ProcessBuilder pb = new ProcessBuilder(
					"python",
					"C:\\Users\\kmoukas\\Downloads\\SomethingUnimportant\\Code_Beauty_Calculator\\code aesthetics\\aesthetics_main.py",
					filePath
			);

			pb.redirectErrorStream(true);

			Process process = pb.start();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream())
			);

			StringBuilder output = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				output.append(line);
			}

			String json = output.toString();

			if (json == null || json.isEmpty()) {
				System.out.println("Python returned no output for: " + filePath);
				return null;
			}

			process.waitFor();

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);

			return new BeautyMetrics(
					(Double) obj.get("balance"),
					(Double) obj.get("equilibrium"),
					(Double) obj.get("density"),
					(Double) obj.get("regularity"),
					(Double) obj.get("rhythm"),
					(Double) obj.get("sequence"),
					(Double) obj.get("simplicity"),
					(Double) obj.get("symmetry")
			);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<Ref> detectRefs(String commitSHA, Repository repo) throws GitAPIException, IOException {

		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
		ArrayList<Ref> refList = new ArrayList<>();
		ArrayList<String> refactoringTypesToKeep = new ArrayList<>(Arrays.asList("EXTRACT_SUPERCLASS",
				"EXTRACT_INTERFACE", "EXTRACT_CLASS", "MOVE_AND_RENAME_OPERATION", "SPLIT_CLASS",
				"EXTRACT_OPERATION", "MOVE_OPERATION", "PULL_UP_OPERATION", "EXTRACT_AND_MOVE_OPERATION",
				"EXTRACT_VARIABLE", "ENCAPSULATE_ATTRIBUTE", "INLINE_VARIABLE", "MOVE_ATTRIBUTE", "PULL_UP_ATTRIBUTE",
				"REPLACE_VARIABLE_WITH_ATTRIBUTE", "INLINE_OPERATION", "PARAMETERIZE_VARIABLE", "EXTRACT_ATTRIBUTE",
				"MOVE_AND_INLINE_OPERATION", "MOVE_RENAME_ATTRIBUTE", "LOCALIZE_PARAMETER", "PUSH_DOWN_OPERATION",
				"MERGE_CONDITIONAL", "REPLACE_LOOP_WITH_PIPELINE", "SPLIT_OPERATION", "SPLIT_CONDITIONAL", "REPLACE_ANONYMOUS_WITH_LAMBDA"));
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