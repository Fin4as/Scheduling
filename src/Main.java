
import static java.lang.Math.pow;
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

        Data s = new Data();
        List<Patient> arrivalSequence = s.getListPatients();
        Functions f = new Functions(s);

        long t_0A = System.nanoTime();
        List<Patient> best = f.annealingMin(100, 20, 100, arrivalSequence);
        long t_1A = System.nanoTime();
        if (f.fO(best, false) != Double.MAX_VALUE) {
            System.out.println(best + " " + f.fO(best, true));
            System.out.println("Length of the algorithm annealing: " + (t_1A - t_0A) / pow(10, 9) + " s.");
        } else {
            System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
        }

        long t_0GR = System.nanoTime();
        List<Patient> grasp = f.grasp(20, 45, arrivalSequence);
        long t_1GR = System.nanoTime();
        if (f.fO(grasp, false) != Double.MAX_VALUE) {
            System.out.println(grasp + " " + f.fO(grasp, true));
            System.out.println("Length of the algorithm GRASP: " + (t_1GR - t_0GR) / pow(10, 9) + " s.");
        } else {
            System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
        }

        long t_0GRCL = System.nanoTime();
        List<Patient> graspRCL = f.graspRCL(0.4, 20, 45, arrivalSequence);
        long t_1GRCL = System.nanoTime();
        if (f.fO(graspRCL, false) != Double.MAX_VALUE) {
            System.out.println(graspRCL + " " + f.fO(graspRCL, true));
            System.out.println("Length of the algorithm GRASP RCL: " + (t_1GRCL - t_0GRCL) / pow(10, 9) + " s.");
        } else {
            System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
        }

        long t_0G = System.nanoTime();
        List<Patient> gene = f.genetic(25, 100, arrivalSequence, 20, 40, true);
        long t_1G = System.nanoTime();
        if (f.fO(gene, false) != Double.MAX_VALUE) {
            System.out.println(gene + " " + f.fO(gene, true));
            System.out.println("Length of the algorithm Genetic: " + (t_1G - t_0G) / pow(10, 9) + " s.");
        } else {
            System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
        }
    }
}
