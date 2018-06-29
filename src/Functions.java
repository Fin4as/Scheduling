
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.util.ArrayList;
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

    public double fO(List<Patient> sequence) {
        double result = 0;

        Test t = new Test(sequence, s);
        t.addTask();

        result = t.calculateMakespan();

        result += wait(t.getTotalWaitingTime());
        result += late(t.getLateness());
        // System.out.println(wait(t.totalWaitingTime));
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
//        System.out.println("scur init : " + fO(scur));
//        System.out.println("minb init : " + fO(minb));
        while (temperature >= tempmin) {
            //System.out.println("hey");
            if (numiter < itermax) {
                sold = scur;
                scur = SwappableSequence.deterministicSwap(scur, numiter % (scur.size()), (numiter + 1) % (scur.size()));
                //System.out.println(snew.toString());
//                System.out.println("scur - sold" + fO(scur) + "    " + fO(sold) + " = dif : " + (fO(scur) - fO(sold)));
                dif = fO(scur) - fO(sold);
                //System.out.println(f(snew)+ "-" + f(scur)+" = "+dif);

                if (dif <= 0) {
                    sold = scur;
                    double dif2 = fO(sold) - fO(minb);
                    if (dif2 <= 0) {
                        minb = sold;
//                        System.out.println(fO(minb));

                    }

                } else {
                    rd = Math.random();
                    if (rd < exp(-dif / (1.38064852 * pow(10, -23)) * temperature)) {
                        sold = scur;
                    }
                }
                numiter++;
//                    System.out.println("");
//                    System.out.println(temperature);
//                    System.out.println("iteration n°"+numiter);
//                    System.out.println("Current x min : "+ minb);
//                    System.out.println("Current minimum:"+ f(minb));
//                    System.out.println("Position of the scur: "+ scur);
//                    System.out.println("Outcome of the f(scur): "+ f(scur));
//                    System.out.println("Position of snew: "+snew);
//                    System.out.println("Outcome of the f(snew):" + f(snew));

            } else {
                temperature = coolingRate * temperature;
                numiter = 0;
            }

        }
        System.out.print("The minimum is located  ");
        return minb;
    }

    /**
     *
     * @param greedyness
     * @param nbIteration
     * @param scur
     * @return
     */
    public List<Patient> grasp(double greedyness, int nbIteration, List<Patient> scur) {
        //defined by a random function
        List<Patient> bestPosition = new ArrayList<Patient>();
        bestPosition = scur;
        int i = 0;
        while (i < nbIteration) {
            scur = greedyRandomizedConstruction(greedyness, scur);
            scur = localSearch(scur, 100);

            if (fO(scur) < fO(bestPosition)) {
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
                    System.out.println(limit);
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
                    System.out.println(limit);
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
            if (fO(scur) < fO(bestPosition)) {
                bestPosition = scur;
            } else {
                improv++;
            }
        }
        return bestPosition;
    }
}
