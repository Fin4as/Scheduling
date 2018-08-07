
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

        List<Patient> arrivalSequence = new ArrayList();
        Patient p0 = new Patient("P0", "PR2", 98, "Gastroenterology");
        Patient p1 = new Patient("P1", "PR1", 38, "Ear/Nose/Throat");
        Patient p2 = new Patient("P2", "PR1", 105, "Urology/Endocrinology");
        Patient p3 = new Patient("P3", "PR1", 13, "Orthopedics");
        Patient p4 = new Patient("P4", "PR2", 5, "Women's clinic");
        Patient p5 = new Patient("P5", "PR2", 56, "Anesthesiology Procedures");
        Patient p6 = new Patient("P6", "PR2", 118, "Ophthalmology");
        Patient p7 = new Patient("P7", "PR1", 24, "Neurosurgery");
        Patient p8 = new Patient("P8", "PR1", 93, "Plastic/Hand");
        Patient p9 = new Patient("P9", "PR1", 72, "Cardio/Lung/Vascular");
        Patient p10 = new Patient("P10", "PR2", 56, "Common Procedures");
        Patient p11 = new Patient("P11", "PR1", 67, "Pediatrics");
        Patient p12 = new Patient("P12", "PR2", 104, "Ophthalmology");
        Patient p13 = new Patient("P13", "PR1", 71, "Women's clinic");
        Patient p14 = new Patient("P14", "PR2", 60, "Ear/Nose/Throat");
        Patient p15 = new Patient("P15", "PR1", 45, "Neurosurgery");
        Patient p16 = new Patient("P16", "PR1", 5, "Pediatrics");
        Patient p17 = new Patient("P17", "PR2", 29, "Cardio/Lung/Vascular");
        Patient p18 = new Patient("P18", "PR2", 11, "Neurosurgery");
        Patient p19 = new Patient("P19", "PR1", 122, "Gastroenterology");

        arrivalSequence.add(p7);
        arrivalSequence.add(p10);
        arrivalSequence.add(p0);
        arrivalSequence.add(p1);
        arrivalSequence.add(p9);
        arrivalSequence.add(p11);
        arrivalSequence.add(p18);
        arrivalSequence.add(p16);
        arrivalSequence.add(p8);
        arrivalSequence.add(p12);
        arrivalSequence.add(p15);
        arrivalSequence.add(p6);
        arrivalSequence.add(p14);
        arrivalSequence.add(p4);
        arrivalSequence.add(p3);
        arrivalSequence.add(p2);
        arrivalSequence.add(p5);
        arrivalSequence.add(p17);
        arrivalSequence.add(p13);
        arrivalSequence.add(p19);

        Schedule s = new Schedule(arrivalSequence);
        Functions f = new Functions(s);

//        for (int i = 0; i < 1000; i++) {
        try (Writer writer1 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("resultsAnnealing.txt", true), "utf-8"))) {
            long t_0A = System.nanoTime();
            List<Patient> best = f.annealingMin(1, 0.5, 20, arrivalSequence);
            long t_1A = System.nanoTime();
            if (f.fO(best, false) != Double.MAX_VALUE) {
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
        try (Writer writer2 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("resultsGenetic.txt", true), "utf-8"))) {
            long t_0G = System.nanoTime();
            List<Patient> gene = f.genetic(25, 30, arrivalSequence, 20, 40, true);
            long t_1G = System.nanoTime();
            if (f.fO(gene, false) != Double.MAX_VALUE) {
                System.out.println(gene + " " + f.fO(gene, false));
                System.out.println("Length of the algorithm Genetic: " + (t_1G - t_0G) / pow(10, 9) + " s.");
                writer2.append("" + f.fO(gene, false) + "\r\n");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
                writer2.append("NULL" + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }

//        for (int i = 0; i < 1000; i++) {
        try (Writer writer3 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("resultsGRASP_RCL.txt", true), "utf-8"))) {
            long t_0GRCL = System.nanoTime();
            List<Patient> graspRCL = f.graspRCL(0.4, 20, 45, arrivalSequence);
            long t_1GRCL = System.nanoTime();
            if (f.fO(graspRCL, false) != Double.MAX_VALUE) {
                System.out.println(graspRCL + " " + f.fO(graspRCL, false));
                System.out.println("Length of the algorithm GRASP RCL: " + (t_1GRCL - t_0GRCL) / pow(10, 9) + " s.");
                writer3.append("" + f.fO(graspRCL, false) + "\r\n");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
                writer3.append("NULL");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        }
//        for (int i = 0; i < 1000; i++) {
        try (Writer writer4 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("resultsGRASP.txt", true), "utf-8"))) {
            long t_0GR = System.nanoTime();
            List<Patient> grasp = f.grasp(20, 45, arrivalSequence);
            long t_1GR = System.nanoTime();
            if (f.fO(grasp, false) != Double.MAX_VALUE) {
                System.out.println(grasp + " " + f.fO(grasp, false));
                System.out.println("Length of the algorithm GRASP: " + (t_1GR - t_0GR) / pow(10, 9) + " s.");
                writer4.append("" + f.fO(grasp, false) + "\r\n");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
                writer4.append("NULL");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }
}
