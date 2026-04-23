package main.java;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Analysis {

    private String projectPath;
    private ArrayList<JavaFile> javaFiles;

    public Analysis(String projectPath){
        this.projectPath=projectPath;
        javaFiles = new ArrayList<>();
    }

    public void StartAnalysis(){
        javaFiles.clear();
        ArrayList<String> rootFolders = getRootFolders(projectPath);
        getJavaFiles(projectPath);
        System.out.println("number of files: " + javaFiles.size());
        getMetricsCalculatorMetrics();
        System.out.println(javaFiles);
        System.out.println("Got the metrics!");
    }

    /**
     * Get Metrics from Metrics Calculator for every java file
     */
    private void getMetricsCalculatorMetrics() {
        String thread = "1";

        //For Linux
        try {
            String home = System.getProperty("user.dir") + "/src/main/java";
            if(!System.getProperty("os.name").toLowerCase().contains("win")) {
                ProcessBuilder pbuilder2 = new ProcessBuilder("bash", "-c", "cd " + home +
                        "; java -jar -Xmx32g MetricsCalculatorSnap.jar "+ projectPath + " "+thread);
                File err2 = new File("err2.txt");
                System.out.println("Running the jar file ");
                pbuilder2.redirectError(err2);
                Process p2 = pbuilder2.start();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                String line1;
                while ((line1 = reader2.readLine()) != null) {
//                    System.out.println(line1);
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
                String line2;
                while ((line2 = reader3.readLine()) != null) {
//                    System.out.println(line2);
                }
            }
            //For Windows
            else {
                Process proc1 = Runtime.getRuntime().exec("cmd /c \"cd " + home+ " && "+
                        "java -jar MetricsCalculatorSnap.jar " +projectPath+ " " +thread+ "\"");

                System.out.println("cmd /c \"cd " + home+ " && "+
                        "java -jar MetricsCalculatorSnap.jar " +projectPath+ " " +thread+ "\"");

                Thread stdoutThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc1.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                });

                Thread stderrThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc1.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.err.println(line);
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                });

                stdoutThread.start();
                stderrThread.start();

                proc1.waitFor();

                stdoutThread.join();
                stderrThread.join();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            String home = System.getProperty("user.dir") + "/src/main/java/";
            File myObj = new File(home + thread);
//            System.out.println("Looking for file: " + myObj.getAbsolutePath());
//            System.out.println(myObj.exists() + " " + myObj.getName());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.startsWith("FilePath"))
                    continue;

                //save results
                String[] column = data.split(";");
//                System.out.println(Arrays.toString(column));
                String filePath;
                if(Utils.isWindows())
                    filePath = column[0].replace("/", "\\");
                else
                    filePath = column[0];

//                System.out.println("IN THE FILE: " + filePath);
//                System.out.println("Our files now are:" + javaFiles);
                String filePathClean = filePath.split(";", 2)[0];
                for(JavaFile jf: javaFiles) {
                    String jfPath = jf.getPath();
                    if (jfPath.startsWith("\\") || jfPath.startsWith("/")) {
                        jfPath = jfPath.substring(1);
                    }
//                    System.out.println("One more check: " + jfPath);
//                    System.out.println("And it is: " + filePathClean.endsWith(jfPath));
                    if(filePathClean.endsWith(jfPath)) {
                        jf.setWMC(Double.parseDouble(column[1]));
                        jf.setDIT(Integer.parseInt(column[2]));
                        jf.setNOCC(Double.parseDouble(column[3]));
                        jf.setCBO(Double.parseDouble(column[4]));
                        jf.setRFC(Double.parseDouble(column[5]));
                        jf.setLCOM(Double.parseDouble(column[6]));
                        jf.setWMCStar(Double.parseDouble(column[7]));
                        jf.setNOM(Double.parseDouble(column[8]));
                        jf.setMPC(Double.parseDouble(column[9]));
                        jf.setDAC(Integer.parseInt(column[10]));
                        jf.setSIZE1(Double.parseDouble(column[11]));
                        jf.setSIZE2(Double.parseDouble(column[12]));
                        jf.setDSC(Double.parseDouble(column[13]));
                        jf.setNOH(Double.parseDouble(column[14]));
                        jf.setANA(Double.parseDouble(column[15]));
                        jf.setDAM(Double.parseDouble(column[16]));
                        jf.setDCC(Double.parseDouble(column[17]));
                        jf.setCAMC(Double.parseDouble(column[18]));
                        jf.setMOA(Double.parseDouble(column[19]));
                        jf.setMFA(Double.parseDouble(column[20]));
                        jf.setNOP(Double.parseDouble(column[21]));
                        jf.setCIS(Double.parseDouble(column[22]));
                        jf.setNPM(Double.parseDouble(column[23]));
                        jf.setReusability(Double.parseDouble(column[24]));
                        jf.setFlexibility(Double.parseDouble(column[25]));
                        jf.setUnderstandability(Double.parseDouble(column[26]));
                        jf.setFunctionality(Double.parseDouble(column[27]));
                        jf.setExtendibility(Double.parseDouble(column[28]));
                        jf.setEffectiveness(Double.parseDouble(column[29]));
                        jf.setFanIn(Double.parseDouble(column[30]));

                        break;
                    }
                }
            }
            myReader.close();
            myObj.delete();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. " + thread);
            e.printStackTrace();
        }
    }

    /**
     * Get Folders of the root dir of the project
     */
    private ArrayList<String> getRootFolders(String projectPath) {
        ArrayList<String> rootFolders =new ArrayList<>();
        File directory = new File(projectPath);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null){
            for (File file : fList) {
                if (file.isDirectory()) {
                    rootFolders.add( file.getAbsolutePath().replace(projectPath, "").substring(1) );
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
        return rootFolders;
    }

    /**
     * Finds all the files in the directory that will be analyzed
     * @param directoryName the directory to search for files
     */
    private void getJavaFiles(String directoryName) {
        File directory = new File(directoryName);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null){
            for (File file : fList) {
                if (file.isFile() && file.getName().contains(".") && file.getName().charAt(0)!='.') {
                    String[] str=file.getName().split("\\.");
                    // For all the files of this directory get the extension
                    if(str[str.length-1].equalsIgnoreCase("java") )
                        javaFiles.add( new JavaFile(file.getAbsolutePath().replace(projectPath, "")) );
                } else if (file.isDirectory()) {
                    getJavaFiles(file.getAbsolutePath());
                }
            }
        }
    }

    public String getProjectPath() {
        return projectPath;
    }

    public ArrayList<JavaFile> getJavaFiles() {
        return javaFiles;
    }
}
