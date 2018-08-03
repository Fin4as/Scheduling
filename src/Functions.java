
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double waiting(int w) {
        double result = 0.0;
        if (w < 60) {
            result = (1 / 15) * w;
        } else {
            result = (-40 + w) / 5;
        }
        return result;
    }

    public double late(int l) {
        double result = 0.0;
        result = round(Math.exp(l / (240 / Math.log(30))), 4);
        return result;
    }

    public double fO(List<Patient> sequence, boolean giveDetails) {

        try {
            Test t = new Test(sequence, s);
            t.addTask(giveDetails);

            double result = t.calculateMakespan();

            result += waiting(t.getTotalWaitingTime());
            result += late(t.getLateness());

//        System.out.println(result);
//        System.out.println(wait(t.totalWaitingTime));
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
                    for (String[] e : sequence.get(i).getParallelSchedules()) {
                        System.out.println(Arrays.toString(e));

                    }
                }
                System.out.println("");

                for (int j = 0; j < t.getListResource().size(); j++) {
                    System.out.print(t.getListResource().get(j).getResourceID());
                    System.out.println(Arrays.toString(t.getListResource().get(j).getTime()));
                }
                ExcelWriter excelWriter = new ExcelWriter();
                excelWriter.write(t.listPatient);
                for (int h = 0; h < t.getListResource().size(); h++) {
                    excelWriter.update(t.getListResource());
                }

            }

            return result;
        } catch (IllegalArgumentException e) {
            return Double.MAX_VALUE;
        }
    }

    public List<Patient> annealingMin(double temperature, double tempmin, int itermax, List<Patient> sold) {
        List<Patient> scur = new ArrayList();
        double coolingRate = 0.70;
        int numiter = 1;
        double dif;
        double rd;
        List<Patient> minb = new ArrayList();
//        sold = Sequence.weightedInitialSequence(scur);
        for (Patient e : sold) {
            minb.add(e);
        }

        try (Writer writer1 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementAnnealing.txt", false)))) {
            if (fO(sold, false) == Double.MAX_VALUE) {
                writer1.write("Original value: The initial sequence is not schedulable" + System.getProperty("line.separator"));
                writer1.write(System.getProperty("line.separator"));
                writer1.write("Temperature : " + temperature + System.getProperty("line.separator"));
                writer1.write(System.getProperty("line.separator"));
            } else {
                writer1.write("Original value: " + fO(sold, false) + System.getProperty("line.separator"));
                writer1.write(System.getProperty("line.separator"));
                writer1.write("Temperature : " + temperature + System.getProperty("line.separator"));
                writer1.write(System.getProperty("line.separator"));
            }

            while (temperature >= tempmin) {

                if (numiter <= itermax) {
                    scur = new ArrayList();
                    for (Patient i : sold) {
                        scur.add(i);
                    }
                    Sequence.deterministicSwap(scur, (numiter - 1) % (scur.size()), numiter % (scur.size()));
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
                            writer1.write("Improved value: " + fO(minb, false) + System.getProperty("line.separator"));
                        }

                    } else if (dif < pow(10, 9)) {
                        rd = Math.random();
                        double choice = exp(-dif / temperature);
                        if (rd < choice) {
                            sold = new ArrayList();
                            for (Patient p : scur) {
                                sold.add(p);
                            }
//                            writer1.write("Accepted value: " + fO(sold, false) + " " + numiter + System.getProperty("line.separator"));
                        }
                    }

                } else {
                    temperature = coolingRate * temperature;
                    numiter = 0;
                    if (temperature > tempmin) {
                        writer1.write(System.getProperty("line.separator"));
                        writer1.write(System.getProperty("line.separator"));
                        writer1.write("Temperature : " + temperature + System.getProperty("line.separator"));
                        writer1.write(System.getProperty("line.separator"));
                    }
                }
                numiter++;
            }

            writer1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return minb;
    }

    public List<Patient> grasp(int nbIteration, int maxNonImprov, List<Patient> scur) {
        List<Patient> bestPosition = new ArrayList();
        List<Patient> backUp = new ArrayList();
//        scur = Sequence.weightedInitialSequence(scur);
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int i = 0;
        try (Writer writer2 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGRASP.txt", true)))) {
            writer2.append("Original position : " + fO(bestPosition, false) + "\r\n");

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
                for (Patient p : localSearch(scur, maxNonImprov)) {
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

                    writer2.append("Improvement : " + fO(bestPosition, false) + "\r\n");

                }
                i++;
            }

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
        List<Patient> bestPosition = new ArrayList<Patient>();
        List<Patient> backUp = new ArrayList();
//        scur = Sequence.weightedInitialSequence(scur);
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        try (Writer writer3 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementgraspRCL.txt", true)))) {
            writer3.append("Original position : " + fO(bestPosition, false) + "\r\n");
            int i = 0;
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
                for (Patient p : localSearch(scur, maxNonImprov)) {
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
     *
     * @param scur
     * @param nboccur
     * @return
     */
    public List<Patient> localSearch(List<Patient> scur, int maxNonImprov) {
        List<Patient> bestPosition = new ArrayList();
        for (Patient p : scur) {
            bestPosition.add(p);
        }
        int numiter = 1;
        while (numiter <= maxNonImprov) {
            scur = Sequence.deterministicSwap(scur, (numiter - 1) % (scur.size()), numiter % (scur.size()));
            if (fO(scur, false) < fO(bestPosition, false)) {
                bestPosition = new ArrayList();
                for (Patient p : scur) {
                    bestPosition.add(p);
                }
                numiter = 1;
            } else {
                numiter++;
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
    public List<Patient> genetic(int sizePopulation, int nbrGeneration, List<Patient> scur, int startPercentage, int endPercentage, boolean reverse) {
        // List of Sequences considered as a population
        List<List<Patient>> population = new ArrayList();
        // Declaration of the initial sequence 
        List<Patient> bestPositionImprovement = new ArrayList();
        for (Patient p : scur) {
            bestPositionImprovement.add(p);
        }
        //Initialization of the two lists used for the parents
        List<Patient> bestPopulation1;
        List<Patient> bestPopulation2;

        //Filling of the population by random sequences
        Random rd = new Random();
        List<Patient> randomPatients;
        List<Patient> possiblePatient = new ArrayList();
        Patient randomPatient;
        population.add(scur);

        try (Writer writer4 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("improvementGenetic.txt", true)))) {
            writer4.append("Original position : " + fO(bestPositionImprovement, false) + "\r\n");
            while (population.size() < sizePopulation) {
                for (int i = 0; i < scur.size(); i++) {
                    possiblePatient.add(scur.get(i));
                }
                randomPatients = new ArrayList();
                while (randomPatients.size() < scur.size()) {
                    randomPatient = possiblePatient.get(rd.nextInt(possiblePatient.size()));
                    randomPatients.add(randomPatient);
                    possiblePatient.remove(randomPatient);

                }
                if (!population.contains(randomPatients)) {
                    population.add(randomPatients);
                }
            }


            /*Evolution of the population to find the sequence with the best fitness
        after a fixed number of iterations*/
            int n = 0;
            while (n < nbrGeneration) {

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

                //Examination of the population to find the two fittest sequences
                for (int j = 0; j < sizePopulation; j++) {
                    List<Patient> read = new ArrayList();
                    for (Patient p : population.get(j)) {
                        read.add(p);
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
                        } else if (fO(read, false) != fO(bestPopulation1, false)) {
                            bestPopulation2 = new ArrayList();
                            for (Patient p : read) {
                                bestPopulation2.add(p);
                            }
                        }
                    }
                }

                if (fO(bestPopulation1, false) < fO(bestPositionImprovement, false)) {
                    bestPositionImprovement = new ArrayList();
                    for (Patient p : bestPopulation1) {
                        bestPositionImprovement.add(p);
                    }
                }

                //Realisation of the crossing over to create an offspring supposedly better than its two parents
                List<Patient> child = new ArrayList();
                for (Patient p : Sequence.makeACrossingOver(bestPopulation1, bestPopulation2, startPercentage, endPercentage, reverse)) {
                    child.add(p);
                }
                //This offspring is added in the population 
                population.add(child);
                //The list fit parent in taken out of the population 
                population.remove(population.indexOf(bestPopulation2));

                //A Generation pass
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return bestPositionImprovement;
    }
}
