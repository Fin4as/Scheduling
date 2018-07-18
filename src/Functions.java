
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

    public double fO(List<Patient> sequence,boolean giveDetails) {
        
        double result = 0;

        Test t = new Test(sequence, s);
        t.addTask(giveDetails);

        result = t.calculateMakespan();

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

        return result;
    }

    public List<Patient> annealingMin(double temperature, int itermax, List<Patient> scur) {
        List<Patient> sold;
        double tempmin = 0.1;
        int numiter = 0;
        double coolingRate = 0.01;
        double dif;
        double rd;
        List<Patient> minb = scur;

        while (temperature >= tempmin) {

            if (numiter < itermax) {
                sold = scur;
                scur = SwappableSequence.deterministicSwap(scur, numiter % (scur.size()), (numiter + 1) % (scur.size()));

                dif = fO(scur, false) - fO(sold, false);

                if (dif <= 0) {
                    sold = scur;
                    double dif2 = fO(sold, false) - fO(minb, false);
                    if (dif2 <= 0) {
                        minb = sold;

                    }

                } else {
                    rd = Math.random();
                    if (rd < exp(-dif / (1.38064852 * pow(10, -23)) * temperature)) {
                        sold = scur;
                    }
                }
                numiter++;

            } else {
                temperature = coolingRate * temperature;
                numiter = 0;
            }

        }
        return minb;
    }

    public List<Patient> grasp(int nbIteration, List<Patient> scur) {
        List<Patient> bestPosition = new ArrayList();
        bestPosition = scur;
        int i = 0;
        while (i < nbIteration) {
           scur = randomizedConstruction(scur);
            scur = localSearch(scur, 100);

            if (fO(scur, false) < fO(bestPosition, false)) {
                bestPosition = scur;
            }
            i++;
        } 
        return bestPosition;
    }
    
    public List<Patient> randomizedConstruction(List<Patient> list){
        List<Patient> sequence = new ArrayList();
        List<Patient> patientList =new ArrayList();
        for (Patient e : list){
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
        int i = 0;
        while (i < nbIteration) {
            scur = greedyRandomizedConstruction(greedyness, scur);
            scur = localSearch(scur, 100);

            if (fO(scur, false) < fO(bestPosition, false)) {
                bestPosition = scur;
            }
            i++;
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
        List<Patient> bestPosition = scur;

        //Initialization of the two lists used for the parents
        List<Patient> bestPopulation1;
        List<Patient> bestPopulation2;

        //Filling of the population by random sequences(replace by Quentin)
        Random rd = new Random();
        List<Patient> randomPatients;
        List<Patient> possiblePatient = new ArrayList();
        Patient randomPatient;
        int iteratorCheck;

        population.add(scur);
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
        //End of the part of Quentin

        //Comparison of fitness of the two first sequences of the population to set -the first two parents
        if (fO(population.get(0), false) < fO(population.get(1), false)) {
            bestPopulation1 = population.get(0);
            bestPopulation2 = population.get(1);
        } else {
            bestPopulation1 = population.get(1);
            bestPopulation2 = population.get(0);
        }

        /*Evolution of the population to find the sequence with the best fitness
        after a fixed number of iterations*/
        int n = 0;
        while (n < nbrGeneration) {

            //Examination of the population to find the two fittest sequences
            bestPopulation2 = population.get(0);
            for (int j = 0; j < sizePopulation; j++) {
                List<Patient> read = population.get(j);
                if (fO(read, false) < fO(bestPopulation2, false)) {
                    if (fO(read, false) < fO(bestPopulation1, false)) {
                        bestPopulation2 = bestPopulation1;
                        bestPopulation1 = read;
                    }
                    bestPopulation2 = read;
                }
            }
            //Realisation of the crossing over to create an offspring supposedly better than its two parents
            List<Patient> child = SwappableSequence.makeACrossingOver(bestPopulation1, bestPopulation2, 4);
//                //This offspring is added in the population 
            population.add(child);
            //The list fit parent in taken out of the population 
            population.remove(population.indexOf(bestPopulation2));

            //A Generation pass
            n++;
        }

        //Find the best sequence at the end of the evolution
        bestPosition = population.get(0);
        for (int m = 1; m < sizePopulation; m++) {
            if (fO(population.get(m), false) < fO(bestPosition, false)) {
                bestPosition = population.get(m);
            }
        }
        return bestPosition;
    }
}
