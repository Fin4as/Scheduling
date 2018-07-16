
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
        Patient p0 = new Patient("P0", "PR1", 85);
        Patient p1 = new Patient("P1", "PR1", 22);
        Patient p2 = new Patient("P2", "PR1", 102);
        Patient p3 = new Patient("P3", "PR1", 68);
        Patient p4 = new Patient("P4", "PR1", 19);
        Patient p5 = new Patient("P5", "PR1", 29);
        Patient p6 = new Patient("P6", "PR1", 23);
        Patient p7 = new Patient("P7", "PR1", 86);
        Patient p8 = new Patient("P8", "PR1", 27);
        Patient p9 = new Patient("P9", "PR1", 76);
        arrivalSequence.add(p3);
        arrivalSequence.add(p6);
        arrivalSequence.add(p9);
        arrivalSequence.add(p0);
        arrivalSequence.add(p4);
        arrivalSequence.add(p5);
        arrivalSequence.add(p7);
        arrivalSequence.add(p2);
        arrivalSequence.add(p1);
        arrivalSequence.add(p8);
        
//        arrivalSequence = SwappableSequence.weightedSequence(arrivalSequence).get(0);

        Schedule s = new Schedule(arrivalSequence);
        Functions f = new Functions(s);
//
//       Test t = new Test(listPatient,s);
//       t.addTask();
////
        long t_0A = System.nanoTime();
        List<Patient> best = f.annealingMin(3, 5, arrivalSequence);
        System.out.println(best + " " + f.fO(best));
        long t_1A = System.nanoTime();
        System.out.println("Length of the algorithm annealing: " + (t_1A - t_0A) / pow(10, 9) + " s.");

    }

}
