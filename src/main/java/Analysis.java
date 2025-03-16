import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Analysis {

    private String projectPath;
    private ArrayList<JavaFile> javaFiles;

    public Analysis(String projectPath){
        this.projectPath=projectPath;
        javaFiles = new ArrayList<>();
    }

    public void startAnalysis(){
        ArrayList<String> rootFolders = getRootFolders(projectPath);
        getJavaFiles(projectPath);
        System.out.println("number of files: " + javaFiles.size());
        getMetricsCalculatorMetrics();
        System.out.println("Got the metrics!");
    }

    /**
     * Get Metrics from Metrics Calculator for every java file
     */
    private void getMetricsCalculatorMetrics() {
        String thread = "1";

        //For Linux
        try {
            String home = System.getProperty("user.dir");
            if(!System.getProperty("os.name").toLowerCase().contains("win")) {
                ProcessBuilder pbuilder2 = new ProcessBuilder("bash", "-c", "cd " + home +
                        "; java -jar -Xmx32g metricsCalculatorLite-1.0-SNAPSHOT-jar-with-dependencies.jar "+ projectPath + " "+thread);
                File err2 = new File("err2.txt");
                pbuilder2.redirectError(err2);
                Process p2 = pbuilder2.start();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                String line1;
                while ((line1 = reader2.readLine()) != null) {
                    System.out.println(line1);
                }
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
                String line2;
                while ((line2 = reader3.readLine()) != null) {
                    System.out.println(line2);
                }
            }
            //For Windows
            else {
                Process proc1 = Runtime.getRuntime().exec("cmd /c \"cd " + home+ " && "+
                        "java -jar metricsCalculatorLite-1.0-SNAPSHOT-jar-with-dependencies.jar " +projectPath+ " " +thread+ "\"");
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    System.out.println(line1);
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    System.out.println(line2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            File myObj = new File("thread-" +thread+ ".txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                if(data.startsWith("FilePath"))
                    continue;

                //save results
                String[] column = data.split("\t");
                String filePath;
                if(Utils.isWindows())
                    filePath = column[0].replace("/", "\\");
                else
                    filePath = column[0];

                for(JavaFile jf: javaFiles) {
                    if(filePath.endsWith(jf.getPath().substring(1))) {
                        jf.setDSC(Double.parseDouble(column[1]));
                        jf.setWMC(Double.parseDouble(column[2]));
                        jf.setDIT(Integer.parseInt(column[3]));
                        jf.setCC(Double.parseDouble(column[4]));
                        jf.setLCOM(Double.parseDouble(column[5]));
                        jf.setMPC(Double.parseDouble(column[6]));
                        jf.setNOM(Double.parseDouble(column[7]));
                        jf.setRFC(Double.parseDouble(column[8]));
                        jf.setDAC(Integer.parseInt(column[9]));
                        jf.setNOCC(Double.parseDouble(column[10]));
                        jf.setCBO(Double.parseDouble(column[11]));
                        jf.setSIZE1(Double.parseDouble(column[12]));
                        jf.setSIZE2(Double.parseDouble(column[13]));
                        break;
                    }
                }
            }
            myReader.close();
            myObj.delete();
        } catch (FileNotFoundException e) {
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
