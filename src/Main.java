
import java.io.FileOutputStream;
import java.io.IOException;
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

        Data s = new Data(); // get Data from Data Base
        List<Patient> arrivalSequence = s.getListPatients();
        arrivalSequence = Sequence.weightedInitialSequence(arrivalSequence);
        List<String> arrivalSequenceDisplay = new ArrayList();
        for (int i = 0; i < arrivalSequence.size(); i++) {
            arrivalSequenceDisplay.add(arrivalSequence.get(i).getPatientID());
        }
        System.out.println("Initial sequence (low and high cancellation likelihood structure): " + arrivalSequenceDisplay);
        Functions f = new Functions(s);

        try {
            new FileOutputStream("improvementAnnealing.txt", false);
            new FileOutputStream("improvementAnnealingStatSequence.txt", false);
            new FileOutputStream("improvementAnnealingStatTime.txt", false);
            new FileOutputStream("improvementAnnealingStatValue.txt", false);
            new FileOutputStream("improvementGrasp.txt", false);
            new FileOutputStream("improvementGraspStatSequence.txt", false);
            new FileOutputStream("improvementGraspStatTime.txt", false);
            new FileOutputStream("improvementGraspStatValue.txt", false);
            new FileOutputStream("improvementGraspRCL.txt", false);
            new FileOutputStream("improvementGraspRCLStatSequence.txt", false);
            new FileOutputStream("improvementGraspRCLStatTime.txt", false);
            new FileOutputStream("improvementGraspRCLStatValue.txt", false);
            new FileOutputStream("improvementGenetic.txt", false);
            new FileOutputStream("improvementGeneticStatSequence.txt", false);
            new FileOutputStream("improvementGeneticStatTime.txt", false);
            new FileOutputStream("improvementGeneticStatValue.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int swap = 1; swap <= 8; swap++) {
            long t_0A = System.nanoTime();
            List<Patient> anneal = f.annealingMin(100, 20, 100, arrivalSequence, swap);
            long t_1A = System.nanoTime();
            if (f.fO(anneal, true) != Double.MAX_VALUE) {
                List<String> annealDisplay = new ArrayList();
                for (int i = 0; i < anneal.size(); i++) {
                    annealDisplay.add(anneal.get(i).getPatientID());
                }
                System.out.println("Neighbourhood search method used: Type " + swap + ". Refer to the documentation to know the corresponding method.");
                System.out.println("Best sequence found by the algorithm: " + annealDisplay);
                System.out.println("Value of the objective function for the best sequence: " + f.fO(anneal, false));
                System.out.println("Length of the algorithm annealing: " + (t_1A - t_0A) / pow(10, 9) + " s.");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
            }

            long t_0GR = System.nanoTime();
            List<Patient> grasp = f.grasp(20, 45, arrivalSequence, swap);
            long t_1GR = System.nanoTime();
            if (f.fO(grasp, true) != Double.MAX_VALUE) {
                List<String> graspDisplay = new ArrayList();
                for (int i = 0; i < grasp.size(); i++) {
                    graspDisplay.add(grasp.get(i).getPatientID());
                }
                System.out.println("Neighbourhood search method used: Type " + swap + ". Refer to the documentation to know the corresponding method.");
                System.out.println("Best sequence found by the algorithm: " + graspDisplay);
                System.out.println("Value of the objective function for the best sequence: " + f.fO(grasp, false));
                System.out.println("Length of the algorithm GRASP: " + (t_1GR - t_0GR) / pow(10, 9) + " s.");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
            }

            long t_0GRCL = System.nanoTime();
            List<Patient> graspRCL = f.graspRCL(0.4, 20, 45, arrivalSequence, swap);
            long t_1GRCL = System.nanoTime();
            if (f.fO(graspRCL, true) != Double.MAX_VALUE) {
                List<String> graspRCLDisplay = new ArrayList();
                for (int i = 0; i < graspRCL.size(); i++) {
                    graspRCLDisplay.add(graspRCL.get(i).getPatientID());
                }
                System.out.println("Neighbourhood search method used: Type " + swap + ". Refer to the documentation to know the corresponding method.");
                System.out.println("Best sequence found by the algorithm: " + graspRCLDisplay);
                System.out.println("Value of the objective function for the best sequence: " + f.fO(graspRCL, false));
                System.out.println("Length of the algorithm GRASP RCL: " + (t_1GRCL - t_0GRCL) / pow(10, 9) + " s.");
            } else {
                System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
            }
        }

        long t_0G = System.nanoTime();
        List<Patient> gene = f.genetic(25, 100, arrivalSequence, 20, 40, true);
        long t_1G = System.nanoTime();
        if (f.fO(gene, true) != Double.MAX_VALUE) {
            List<String> geneDisplay = new ArrayList();
            for (int i = 0; i < gene.size(); i++) {
                geneDisplay.add(gene.get(i).getPatientID());
            }
            System.out.println("Best sequence found by the algorithm: " + geneDisplay);
            System.out.println("Value of the objective function for the best sequence: " + f.fO(gene, false));
            System.out.println("Length of the algorithm Genetic: " + (t_1G - t_0G) / pow(10, 9) + " s.");
        } else {
            System.out.println("You don't have enough resources to create a full schedule or you don't test enough sequences for this algorithm. Please add more resources, reduce the number of patients to add to the schedule or test more sequences.");
        }

    }
}
