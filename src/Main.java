
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Math.pow;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author robin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        List<String> arrivalSequence = new ArrayList();
        arrivalSequence.add("P0");
        arrivalSequence.add("P1");
        arrivalSequence.add("P2");
        arrivalSequence.add("P3");
        arrivalSequence.add("P4");
        arrivalSequence.add("P7");
        arrivalSequence.add("P10");
        arrivalSequence.add("P12");

        Schedule s = new Schedule(arrivalSequence);
        List <Patient> arrivalSeq = s.getListPatients();
        Functions f = new Functions(s);

//        for (int i = 0; i < 1000; i++) {
        try (Writer writer1 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("resultsAnnealing.txt", true), "utf-8"))) {
            long t_0A = System.nanoTime();
            List<Patient> best = f.annealingMin(1, 0.5, 20, arrivalSeq);
            long t_1A = System.nanoTime();
            if (f.fO(best, true) != Double.MAX_VALUE) {
                System.out.println(best + " " + f.fO(best, false));
                System.out.println("Length of the algorithm annealing: " + (t_1A - t_0A) / pow(10, 9) + " s.");
                writer1.append("" + f.fO(best, false) + "\r\n");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
                writer1.append("NULL" + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }

//        for (int i = 0; i < 1000; i++) {
//        try (Writer writer2 = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("resultsGenetic.txt", true), "utf-8"))) {
//            long t_0G = System.nanoTime();
//            List<Patient> gene = f.genetic(25, 30, arrivalSeq, 20, 40, true);
//            long t_1G = System.nanoTime();
//            if (f.fO(gene, false) != Double.MAX_VALUE) {
//                System.out.println(gene + " " + f.fO(gene, false));
//                System.out.println("Length of the algorithm Genetic: " + (t_1G - t_0G) / pow(10, 9) + " s.");
//                writer2.append("" + f.fO(gene, false) + "\r\n");
//            } else {
//                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
//                writer2.append("NULL" + "\r\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        }

//        for (int i = 0; i < 1000; i++) {
//        try (Writer writer3 = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("resultsGRASP_RCL.txt", true), "utf-8"))) {
//            long t_0GRCL = System.nanoTime();
//            List<Patient> graspRCL = f.graspRCL(0.4, 20, 45, arrivalSeq);
//            long t_1GRCL = System.nanoTime();
//            if (f.fO(graspRCL, false) != Double.MAX_VALUE) {
//                System.out.println(graspRCL + " " + f.fO(graspRCL, false));
//                System.out.println("Length of the algorithm GRASP RCL: " + (t_1GRCL - t_0GRCL) / pow(10, 9) + " s.");
//                writer3.append("" + f.fO(graspRCL, false) + "\r\n");
//            } else {
//                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
//                writer3.append("NULL");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        }
//        for (int i = 0; i < 1000; i++) {
//        try (Writer writer4 = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("resultsGRASP.txt", true), "utf-8"))) {
//            long t_0GR = System.nanoTime();
//            List<Patient> grasp = f.grasp(20, 45, arrivalSeq);
//            long t_1GR = System.nanoTime();
//            if (f.fO(grasp, false) != Double.MAX_VALUE) {
//                System.out.println(grasp + " " + f.fO(grasp, false));
//                System.out.println("Length of the algorithm GRASP: " + (t_1GR - t_0GR) / pow(10, 9) + " s.");
//                writer4.append("" + f.fO(grasp, false) + "\r\n");
//            } else {
//                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
//                writer4.append("NULL");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        }
    }
}
