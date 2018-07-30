
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
    private int totaliterGrasp;
    private int totaliterBest;

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

        try {
            Test t = new Test(sequence, s);
            t.addTask(giveDetails);

            double result = t.calculateMakespan();

            result += wait(t.getTotalWaitingTime());
            result += late(t.getLateness());

//            System.out.println(result);
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
            return result;
        } catch (IllegalArgumentException e) {
            return Double.MAX_VALUE;
        }
    }

    public List<Patient> annealingMin(double temperature, double tempmin, int itermax, List<Patient> sold) {
        double startRuntime = System.nanoTime();
        double currentRuntime = System.nanoTime();
        double totalRuntime;
        List<Patient> scur = new ArrayList();
        double coolingRate = 0.70;
        int numtemp = 0;
        int numiter = 1;
        int numiterBest = 1;
        double dif;
        double rd;
        List<Patient> minb = new ArrayList();
        sold = Sequence.weightedInitialSequence(scur);
        for (Patient e : sold) {
            minb.add(e);
        }

        try (Writer writerAnnealing = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementAnnealing.txt", false)))) {
            if (fO(sold, false) == Double.MAX_VALUE) {
                writerAnnealing.write("Initial value : The initial sequence is not schedulable" + System.getProperty("line.separator"));
                writerAnnealing.write(System.getProperty("line.separator"));
                writerAnnealing.write("Temperature : " + temperature + System.getProperty("line.separator"));
                writerAnnealing.write(System.getProperty("line.separator"));
            } else {
                writerAnnealing.write("Initial value : " + fO(sold, false) + System.getProperty("line.separator"));
                writerAnnealing.write(System.getProperty("line.separator"));
                writerAnnealing.write("Temperature : " + temperature + System.getProperty("line.separator"));
                writerAnnealing.write(System.getProperty("line.separator"));
            }

            while (temperature >= tempmin) {

                if (numiter <= itermax) {
                    scur = new ArrayList();
                    for (Patient i : sold) {
                        scur.add(i);
                    }
                    Sequence.deterministicSwap(scur, numiter % (scur.size()), (numiter + 1) % (scur.size()));
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
                            numiterBest = itermax * numtemp + numiter;
                            currentRuntime = (System.nanoTime() - startRuntime) / pow(10, 9);
                            writerAnnealing.write("Improved optimum found in the neighbourhood: " + fO(minb, false) + " Current total of generated sequences to get this optimum: " + numiterBest + " Current runtime : " + currentRuntime + " s." + System.getProperty("line.separator"));
                        }

                    } else if (dif < pow(10, 9)) {
                        rd = Math.random();
                        double choice = exp(-dif / temperature);
                        if (rd < choice) {
                            sold = new ArrayList();
                            for (Patient p : scur) {
                                sold.add(p);
                            }
//                            writerAnnealing.write("Accepted value: " + fO(sold, false) + " " + numiter + System.getProperty("line.separator"));
                        }
                    }

                } else {
                    temperature = coolingRate * temperature;
                    numiter = 0;
                    numtemp++;
                    if (temperature > tempmin) {
                        writerAnnealing.write(System.getProperty("line.separator"));
                        writerAnnealing.write(System.getProperty("line.separator"));
                        writerAnnealing.write("Temperature : " + temperature + System.getProperty("line.separator"));
                        writerAnnealing.write(System.getProperty("line.separator"));
                    }
                }
                numiter++;
            }
            totalRuntime = (System.nanoTime() - startRuntime) / pow(10, 9);
            List<String> bestSolution = new ArrayList();
            for (Patient p : minb) {
                bestSolution.add(p.getPatientID());
            }
            writerAnnealing.write(System.getProperty("line.separator"));
            writerAnnealing.write("Best solution proposed by the algorithm : " + bestSolution + " Objective function value associed : " + fO(minb, false) + System.getProperty("line.separator"));
            writerAnnealing.write("Found in " + currentRuntime + " s. on a total runtime of " + totalRuntime + " s." + System.getProperty("line.separator"));
            writerAnnealing.write("This solution has been reached by generating " + numiterBest + " sequences on a fixed total generated sequences of " + numtemp * itermax + " sequences.");
            writerAnnealing.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return minb;
    }

    public List<Patient> grasp(int nbIteration, int maxNonImprov, List<Patient> scur) {
        double startRuntime = System.nanoTime();
        double currentRuntime1 = System.nanoTime();
        double currentRuntime2 = System.nanoTime();
        double totalRuntime;
        int totaliterBestGrasp = 0;
        List<Patient> bestPosition = new ArrayList();
        List<Patient> backUp = new ArrayList();
//        scur = Sequence.weightedInitialSequence(scur);
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int i = 0;
        totaliterGrasp = 0;
        try (Writer writerGrasp = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGrasp.txt", false)))) {
            if (fO(bestPosition, false) == Double.MAX_VALUE) {
                writerGrasp.write("Value of the initial sequence: The initial sequence is not schedulable" + System.getProperty("line.separator"));
                writerGrasp.write(System.getProperty("line.separator"));
            } else {
                writerGrasp.write("Value of the initial sequence: " + fO(bestPosition, false) + System.getProperty("line.separator"));
                writerGrasp.write(System.getProperty("line.separator"));
            }

            while (i < nbIteration) {
                backUp = new ArrayList();
                for (Patient p : scur) {
                    backUp.add(p);
                }
                scur = new ArrayList();
                for (Patient p : randomizedConstruction(backUp)) {
                    scur.add(p);
                }
                backUp = new ArrayList();
                for (Patient p : localSearch(scur, maxNonImprov, startRuntime, writerGrasp)) {
                    backUp.add(p);
                }
                scur = new ArrayList();
                for (Patient p : backUp) {
                    scur.add(p);
                }

                if (fO(scur, false) < fO(bestPosition, false)) {

                    bestPosition = new ArrayList();
                    for (Patient p : scur) {
                        bestPosition.add(p);
                    }
                    currentRuntime1 = (System.nanoTime() - startRuntime) / pow(10, 9);
                    totaliterBestGrasp = totaliterBest;
                    writerGrasp.write(System.getProperty("line.separator"));
                    writerGrasp.write("==> New update of the optimum : " + fO(bestPosition, false) + " Minimum number of generated sequences to get this optimum : " + totaliterBest + " Current runtime : " + currentRuntime1 + " s." + System.getProperty("line.separator"));
                    writerGrasp.write(System.getProperty("line.separator"));
                    writerGrasp.write(System.getProperty("line.separator"));
                } else {
                    currentRuntime2 = (System.nanoTime() - startRuntime) / pow(10, 9);
                    writerGrasp.write(System.getProperty("line.separator"));
                    writerGrasp.write("==> No update of the optimum." + " Current runtime : " + currentRuntime2 + " s." + System.getProperty("line.separator"));
                    writerGrasp.write(System.getProperty("line.separator"));
                    writerGrasp.write(System.getProperty("line.separator"));
                }
                i++;
            }
            totalRuntime = (System.nanoTime() - startRuntime) / pow(10, 9);
            List<String> bestSolution = new ArrayList();
            for (Patient p : bestPosition) {
                bestSolution.add(p.getPatientID());
            }
            writerGrasp.write(System.getProperty("line.separator"));
            writerGrasp.write("Best solution proposed by the algorithm : " + bestSolution + " Objective function value associed : " + fO(bestPosition, false) + System.getProperty("line.separator"));
            writerGrasp.write("Found in " + currentRuntime1 + " s. on a total runtime of " + totalRuntime + " s." + System.getProperty("line.separator"));
            writerGrasp.write("This solution has been reached by generating " + totaliterBestGrasp + " sequences on a total generated sequences of " + totaliterGrasp + " sequences.");
            writerGrasp.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return bestPosition;
    }

    public List<Patient> localSearch(List<Patient> scur, int maxNonImprov, double startRuntime, Writer writer) {
        List<Patient> bestPosition = new ArrayList();
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int numiter = 1;
        int numiterBest = 0;
        totaliterBest = totaliterGrasp;
        try {
            if (fO(bestPosition, false) == Double.MAX_VALUE) {
                writer.write("-------------------------------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
                writer.write("Value of the new random sequence : This random sequence is not schedulable" + System.getProperty("line.separator"));
                writer.write("--> Search for improvement in the neighbourhood" + System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
            } else {
                writer.write("-------------------------------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
                writer.write("Value of the new random sequence : " + fO(bestPosition, false) + System.getProperty("line.separator"));
                writer.write("--> Search for improvement in the neighbourhood" + System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
            }
            while (numiter <= maxNonImprov) {
                scur = Sequence.deterministicSwap(scur, (numiter - 1) % (scur.size()), numiter % (scur.size()));
                if (fO(scur, false) < fO(bestPosition, false)) {
                    totaliterGrasp++;
                    numiterBest += numiter;
                    numiter = 1;

                    bestPosition = new ArrayList();
                    for (Patient p : scur) {
                        bestPosition.add(p);
                    }

                    double currentRuntime = (System.nanoTime() - startRuntime) / pow(10, 9);
                    writer.write("Improved value found in the neighbourhood : " + fO(bestPosition, false) + " Current total of generated sequences in this try : " + numiterBest + " Current runtime : " + currentRuntime + " s." + System.getProperty("line.separator"));
                } else {
                    totaliterGrasp++;
                    numiter++;
                }
            }
            totaliterBest += numiterBest;
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
    public List<Patient> graspRCL(double greedyness, int nbIteration, int maxNonImprov, List<Patient> scur) {
        //defined by a random function        
        double startRuntime = System.nanoTime();
        double currentRuntime1 = System.nanoTime();
        double currentRuntime2 = System.nanoTime();
        double totalRuntime;
        int totaliterBestGrasp = 0;
        List<Patient> bestPosition = new ArrayList();
        List<Patient> backUp = new ArrayList();
//        scur = Sequence.weightedInitialSequence(scur);
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int i = 0;
        totaliterGrasp = 0;
        try (Writer writerGraspRCL = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGraspRCL.txt", false)))) {
            if (fO(bestPosition, false) == Double.MAX_VALUE) {
                writerGraspRCL.write("Value of the initial sequence: The initial sequence is not schedulable" + System.getProperty("line.separator"));
                writerGraspRCL.write(System.getProperty("line.separator"));
            } else {
                writerGraspRCL.write("Value of the initial sequence: " + fO(bestPosition, false) + System.getProperty("line.separator"));
                writerGraspRCL.write(System.getProperty("line.separator"));
            }

            while (i < nbIteration) {
                backUp = new ArrayList();
                for (Patient p : scur) {
                    backUp.add(p);
                }
                scur = new ArrayList();
                for (Patient p : greedyRandomizedConstruction(greedyness, backUp)) {
                    scur.add(p);
                }
                backUp = new ArrayList();
                for (Patient p : localSearch(scur, maxNonImprov, startRuntime, writerGraspRCL)) {
                    backUp.add(p);
                }
                scur = new ArrayList();
                for (Patient p : backUp) {
                    scur.add(p);
                }

                if (fO(scur, false) < fO(bestPosition, false)) {
                    bestPosition = new ArrayList();
                    for (Patient p : scur) {
                        bestPosition.add(p);
                    }
                    currentRuntime1 = (System.nanoTime() - startRuntime) / pow(10, 9);
                    totaliterBestGrasp = totaliterBest;
                    writerGraspRCL.write(System.getProperty("line.separator"));
                    writerGraspRCL.write("==> New update of the optimum : " + fO(bestPosition, false) + " Minimum number of generated sequences to get this optimum : " + totaliterBest + " Current runtime : " + currentRuntime1 + " s." + System.getProperty("line.separator"));
                    writerGraspRCL.write(System.getProperty("line.separator"));
                    writerGraspRCL.write(System.getProperty("line.separator"));
                } else {
                    currentRuntime2 = (System.nanoTime() - startRuntime) / pow(10, 9);
                    writerGraspRCL.write(System.getProperty("line.separator"));
                    writerGraspRCL.write("==> No update of the optimum." + " Current runtime : " + currentRuntime2 + " s." + System.getProperty("line.separator"));
                    writerGraspRCL.write(System.getProperty("line.separator"));
                    writerGraspRCL.write(System.getProperty("line.separator"));
                }
                i++;
            }
            totalRuntime = (System.nanoTime() - startRuntime) / pow(10, 9);
            List<String> bestSolution = new ArrayList();
            for (Patient p : bestPosition) {
                bestSolution.add(p.getPatientID());
            }
            writerGraspRCL.write(System.getProperty("line.separator"));
            writerGraspRCL.write("Best solution proposed by the algorithm : " + bestSolution + " Objective function value associed : " + fO(bestPosition, false) + System.getProperty("line.separator"));
            writerGraspRCL.write("Found in " + currentRuntime1 + " s. on a total runtime of " + totalRuntime + " s." + System.getProperty("line.separator"));
            writerGraspRCL.write("This solution has been reached by generating " + totaliterBestGrasp + " sequences on a total generated sequences of " + totaliterGrasp + " sequences.");
            writerGraspRCL.close();
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
        List<List> sortedCancellationLikelihoods = Sequence.sortByCancellationLikelihood(list);
        List<Patient> possibilitiesL = sortedCancellationLikelihoods.get(0);
        List<Patient> possibilitiesLbackup = new ArrayList();
        for (Patient p : possibilitiesL) {
            possibilitiesLbackup.add(p);
        }
        List<Patient> possibilitiesH = sortedCancellationLikelihoods.get(1);
        List<Patient> possibilitiesHbackup = new ArrayList();
        for (Patient p : possibilitiesH) {
            possibilitiesHbackup.add(p);
        }
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
     * Genetic Algorithm
     *
     * @param sizePopulation size of the population studied
     * @param nbrGeneration number of generation done before finding the best
     * sequence
     * @return the sequence with the best fitness
     */
    public List<Patient> genetic(int sizePopulation, int nbrGeneration, List<Patient> scur, int percentage) {
        // List of Sequences considered as a population
        List<List<Patient>> population = new ArrayList();
        // Declaration of the initial sequence 
        List<Patient> bestPosition = new ArrayList();
//        scur = Sequence.weightedInitialSequence(scur);
        for (Patient p : scur) {
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

        try (Writer writerGenetic = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGenetic.txt", true)))) {
            writerGenetic.append("Original position : " + fO(bestPosition, false) + "\r\n");
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
                            writerGenetic.append("Improved : " + fO(bestPopulation1, false) + "\r\n");
                        }
                        bestPopulation2 = new ArrayList();
                        for (Patient p : read) {
                            bestPopulation2.add(p);
                        }
                    }

                }
                //Realisation of the crossing over to create an offspring supposedly better than its two parents
                List<Patient> child = new ArrayList();
                for (Patient p : Sequence.makeACrossingOver(bestPopulation1, bestPopulation2, percentage)) {
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
            for (Patient p : population.get(0)) {
                bestPosition.add(p);
            }
            for (int m = 1; m < sizePopulation; m++) {
                if (fO(population.get(m), false) < fO(bestPosition, false)) {
                    bestPosition = new ArrayList();
                    for (Patient p : population.get(m)) {
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
