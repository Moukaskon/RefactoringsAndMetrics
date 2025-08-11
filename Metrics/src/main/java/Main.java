import metricsCalculator.calculator.MetricsCalculator;
import metricsCalculator.infrastructure.entities.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length==2){
            MetricsCalculator mc = new MetricsCalculator(new Project(args[0]));
            mc.start();

            File file = new File("C:\\Users\\kmoukas\\RefactoringsAndMetrics\\thread-" +args[1]+ ".txt");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.append(mc.printResults());
            } finally {
                if (writer != null) writer.close();
            }
        }
    }
}
