
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author robin
 */
public class Functions {

    Schedule s;

    public Functions(Schedule s) {
        this.s = s;
    }

    public double wait(int w) {
        double result = 0.0;
        result = ((double) 1 / 6) * w;
        return result;
    }

    public double late(int l) {
        double result = 0.0;
        result = 0.125 * l;
        return result;
    }

    public double fO(List<Patient> sequence, boolean giveDetails) {

        try{
        Test t = new Test(sequence, s);
        t.addTask(giveDetails);

        double result = t.calculateMakespan();

        result += wait(t.getTotalWaitingTime());
        result += late(t.getLateness());
//         System.out.println(wait(t.totalWaitingTime));

//        for(int i =0; i<sequence.size(); i++){
//            System.out.print(sequence.get(i).getPatientID());
//            System.out.println(Arrays.toString(sequence.get(i).getSchedule()));
//        }
//        System.out.println("");
//        
//        for(int j =0; j<t.getListResource().size(); j++){
//            System.out.print(t.getListResource().get(j).getResourceID());
//            System.out.println(Arrays.toString(t.getListResource().get(j).getTime()));
//        }
        if (giveDetails == true) {
            for (int i = 0; i < sequence.size(); i++) {
                System.out.print(sequence.get(i).getPatientID());
                System.out.println(Arrays.toString(sequence.get(i).getSchedule()));
            }
            System.out.println("");

            for (int j = 0; j < t.getListResource().size(); j++) {
                System.out.print(t.getListResource().get(j).getResourceID());
                System.out.println(Arrays.toString(t.getListResource().get(j).getTime()));
            }
                        ExcelWriter excelWriter = new ExcelWriter();
            excelWriter.write(t.listPatient);
        }

        return result;}
        catch(IllegalArgumentException e){
            return Double.MAX_VALUE;
        }
    }

    public List<Patient> annealingMin(double temperature, int itermax, List<Patient> sold) {
        List<Patient> scur = new ArrayList();
        double tempmin = 0.1;
        int numiter = 1;
        int numiterBest = 1;
        double coolingRate = 0.01;
        double dif;
        double rd;
        List<Patient> minb = new ArrayList();
        for (Patient e : sold) {
            minb.add(e);
        }

        try (Writer writer1 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementAnnealing.txt", true)))) {
            writer1.append("Original value: " + fO(sold, false) + "\r\n");
            while (temperature >= tempmin) {

                while (temperature >= tempmin) {

                    if (numiter <= itermax) {
                        scur = new ArrayList();
                        for (Patient i : sold) {
                            scur.add(i);
                        }
                        SwappableSequence.deterministicSwap(scur, numiter % (scur.size()), (numiter + 1) % (scur.size()));
                        dif = fO(scur, false) - fO(sold, false);

                        if (dif <= 0) {
                            sold = new ArrayList();
                            for (Patient p : scur) {
                                sold.add(p);
                            }
                            double dif2 = fO(sold, false) - fO(minb, false);
                            if (dif2 < 0) {
                                minb = new ArrayList();
                                for (Patient d : sold) {
                                    minb.add(d);
                                }
                                System.out.println(fO(minb, false));
                                numiterBest = Integer.valueOf(numiter);
                                writer1.append("Improved : " + fO(minb, false) + "\r\n");
                            }

                        } else {
                            rd = Math.random();
                            if (rd < exp(-dif / (1.38064852 * pow(10, -23)) * temperature)) {
                                for (Patient p : scur){
                                    sold.add(p);
                                }
                            }
                        }

                    } else {
                        temperature = coolingRate * temperature;
                        numiter = 0;
                    }
                    numiter++;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return minb;
    }

    public List<Patient> grasp(int nbIteration, List<Patient> scur) {
        List<Patient> bestPosition = new ArrayList();
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int i = 0;
        try (Writer writer2 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGRASP.txt", true)))) {
            writer2.append("Original position : " + fO(bestPosition, false) + "\r\n");

            while (i < nbIteration) {
                scur = randomizedConstruction(scur);
                scur = localSearch(scur, 100);

                if (fO(scur, false) < fO(bestPosition, false)) {
                    bestPosition = new ArrayList();

                    bestPosition = scur;
                    System.out.println(fO(bestPosition, false));
                    writer2.append("Improvement : " + fO(bestPosition, false) + "\r\n");

                }
                i++;
            }
            i++;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return bestPosition;
    }

    public List<Patient> randomizedConstruction(List<Patient> list) {
        List<Patient> sequence = new ArrayList();
        List<Patient> patientList = new ArrayList();
        for (Patient e : list) {
            patientList.add(e);
        }
        Random rand = new Random();
        Patient randomElement;

        while (sequence.size() < list.size()) {
            randomElement = patientList.get(rand.nextInt(patientList.size()));
            sequence.add(randomElement);
            patientList.remove(randomElement);
        }
        return sequence;
    }

    /**
     *
     * @param greedyness
     * @param nbIteration
     * @param scur
     * @return
     */
    public List<Patient> graspRCL(double greedyness, int nbIteration, List<Patient> scur) {
        //defined by a random function
        List<Patient> bestPosition = new ArrayList<Patient>();
        bestPosition = scur;
        try (Writer writer3 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementgraspRCL.txt", true)))) {
            writer3.append("Original position : " + fO(bestPosition, false) + "\r\n");
            int i = 0;
            while (i < nbIteration) {
                scur = greedyRandomizedConstruction(greedyness, scur);
                scur = localSearch(scur, 100);

                if (fO(scur, false) < fO(bestPosition, false)) {
                    bestPosition = scur;
                    writer3.append("Improved : " + fO(bestPosition, false) + "\r\n");
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return bestPosition;
    }

    /**
     *
     * @param greedyness
     * @param list
     * @return
     */
    public List<Patient> greedyRandomizedConstruction(double greedyness, List<Patient> list) {
        List<Patient> sequence = new ArrayList();
        List<Patient> possibilitiesL = SwappableSequence.weightedSequence(list).get(1);
        List<Patient> possibilitiesLbackup = SwappableSequence.weightedSequence(list).get(1);
        List<Patient> possibilitiesH = SwappableSequence.weightedSequence(list).get(2);
        List<Patient> possibilitiesHbackup = SwappableSequence.weightedSequence(list).get(2);
        Patient randomElement;

        Random rand = new Random();
        int firstpo = rand.nextInt(possibilitiesL.size());
        sequence.add(possibilitiesL.get(firstpo));
        possibilitiesL.remove(firstpo);

        while (sequence.size() < list.size()) {
            List<Patient> rcl = new ArrayList();
            List<Double> cost = new ArrayList();
            if (possibilitiesHbackup.contains(sequence.get(sequence.size() - 1))) {
                for (int h = 0; h < possibilitiesL.size(); h++) {
                    cost.add(Math.abs(sequence.get(sequence.size() - 1).getCancellationLikelihood() - possibilitiesL.get(h).getCancellationLikelihood()));

                }
                double maxcost = Collections.max(cost);
                double mincost = Collections.min(cost);

                for (int k = 0; k < possibilitiesL.size(); k++) {
                    if (cost.get(k) <= (mincost + greedyness * (maxcost - mincost))) {
                        rcl.add(possibilitiesL.get(k));
                    }
                }
                int limit = rcl.size() - 1;
                if (limit == 0) {
                    sequence.add(rcl.get(limit));
                    randomElement = rcl.get(limit);
                } else {
                    randomElement = rcl.get(rand.nextInt(limit));
                    sequence.add(randomElement);
                }
                possibilitiesL.remove(possibilitiesL.indexOf(randomElement));

            } else if ((possibilitiesLbackup.contains(sequence.get(sequence.size() - 1)))) {
                for (int h = 0; h < possibilitiesH.size(); h++) {
                    cost.add(Math.abs(sequence.get(sequence.size() - 1).getCancellationLikelihood() - possibilitiesH.get(h).getCancellationLikelihood()));
                }

                double maxcost = Collections.max(cost);
                double mincost = Collections.min(cost);

                for (int k = 0; k < possibilitiesH.size(); k++) {
                    if (cost.get(k) <= (mincost + greedyness * (maxcost - mincost))) {
                        rcl.add(possibilitiesH.get(k));
                    }
                }
                int limit = rcl.size() - 1;
                if (limit == 0) {
                    sequence.add(rcl.get(limit));
                    randomElement = rcl.get(limit);
                } else {
                    randomElement = rcl.get(rand.nextInt(limit));
                    sequence.add(randomElement);
                }

                possibilitiesH.remove(possibilitiesH.indexOf(randomElement));

            }
        }
        return sequence;
    }

    /**
     *
     * @param scur
     * @param nboccur
     * @return
     */
    public List<Patient> localSearch(List<Patient> scur, int nboccur) {
        List<Patient> bestPosition = scur;
        int improv = 0;
        int numiter = 0;
        while (improv < nboccur) {
            scur = SwappableSequence.deterministicSwap(scur, numiter % (scur.size()), (numiter + 1) % (scur.size()));
            if (fO(scur, false) < fO(bestPosition, false)) {
                bestPosition = scur;
            } else {
                improv++;
            }
        }
        return bestPosition;
    }

    /**
     * Genetic Algorithm
     *
     * @param sizePopulation size of the population studied
     * @param nbrGeneration number of generation done before finding the best
     * sequence
     * @return the sequence with the best fitness
     */
    public List<Patient> genetic(int sizePopulation, int nbrGeneration, List<Patient> scur) {
        // List of Sequences considered as a population
        List<List<Patient>> population = new ArrayList();
        // Declaration of the initial sequence 
        List<Patient> bestPosition = new ArrayList();
        for (Patient p : scur){
            bestPosition.add(p);
        }

        //Initialization of the two lists used for the parents
        List<Patient> bestPopulation1;
        List<Patient> bestPopulation2;

        //Filling of the population by random sequences
        Random rd = new Random();
        List<Patient> randomPatients;
        List<Patient> possiblePatient = new ArrayList();
        Patient randomPatient;
        int iteratorCheck;
        population.add(scur);
       

        try (Writer writer4 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGenetic.txt", true)))) {
            writer4.append("Original position : " + fO(bestPosition, false) + "\r\n");
            while (population.size() < sizePopulation) {
                for (int i = 0; i < scur.size(); i++) {
                    possiblePatient.add(scur.get(i));
                }
                randomPatients = new ArrayList();
                iteratorCheck = 0;
                while (randomPatients.size() < scur.size()) {
                    randomPatient = possiblePatient.get(rd.nextInt(possiblePatient.size()));
                    randomPatients.add(randomPatient);
                    possiblePatient.remove(randomPatient);

                }
                while (iteratorCheck < population.size()) {
                    if (population.contains(randomPatients)) {
                        break;
                    } else {
                        iteratorCheck++;
                    }
                    if (iteratorCheck == population.size()) {
                        population.add(randomPatients);
                    }
                }
            }
            
            //Comparison of fitness of the two first sequences of the population to set -the first two parents
            if (fO(population.get(0), false) < fO(population.get(1), false)) {
                bestPopulation1 = new ArrayList();
                for (Patient p : population.get(0)) {
                    bestPopulation1.add(p);
                }
                bestPopulation2 = new ArrayList();
                for (Patient p : population.get(1)) {
                    bestPopulation2.add(p);
                }
            } else {
                bestPopulation1 = new ArrayList();
                for (Patient p : population.get(1)) {
                    bestPopulation1.add(p);
                }
                bestPopulation2 = new ArrayList();
                for (Patient p : population.get(0)) {
                    bestPopulation2.add(p);
                }
            }

            /*Evolution of the population to find the sequence with the best fitness
        after a fixed number of iterations*/
            int n = 0;
            while (n < nbrGeneration) {
                //Examination of the population to find the two fittest sequences
                bestPopulation2 = new ArrayList();
                for (Patient e : population.get(0)) {
                    bestPopulation2.add(e);
                }
                for (int j = 0; j < sizePopulation; j++) {
                    List<Patient> read = new ArrayList();
                    for (Patient l : population.get(j)) {
                        read.add(l);
                    }
                    if (fO(read, false) < fO(bestPopulation2, false)) {
                        if (fO(read, false) < fO(bestPopulation1, false)) {
                            bestPopulation2 = new ArrayList();
                            for (Patient p : bestPopulation1) {
                                bestPopulation2.add(p);
                            }
                            bestPopulation1 = new ArrayList();
                            for (Patient p : read) {
                                bestPopulation1.add(p);
                            }
                            writer4.append("Improved : " + fO(bestPopulation1, false) + "\r\n");
                        }
                        bestPopulation2 = new ArrayList();
                        for (Patient p : read ){
                            bestPopulation2.add(p);
                        }    
                    }

                }
                //Realisation of the crossing over to create an offspring supposedly better than its two parents
                List<Patient> child = new ArrayList();
                for (Patient p: SwappableSequence.makeACrossingOver(bestPopulation1, bestPopulation2, 4)){
                    child.add(p);
                }
//                //This offspring is added in the population 
                population.add(child);
                //The list fit parent in taken out of the population 
                population.remove(population.indexOf(bestPopulation2));

                //A Generation pass
                n++;
            }

            //Find the best sequence at the end of the evolution
            bestPosition = new ArrayList();
            for (Patient p :population.get(0) ){
                bestPosition.add(p);
            }
            for (int m = 1; m < sizePopulation; m++) {
                if (fO(population.get(m), false) < fO(bestPosition, false)) {
                    bestPosition = new ArrayList();
                    for (Patient p : population.get(m) ){
                        bestPosition.add(p);
                    }               
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return bestPosition;
    }
}
