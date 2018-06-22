/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author quent
 */
public abstract class SwappableSequence {

    public static List<Patient> deterministicSwap(List<Patient> sequence, int i, int j) {

        if (i == j) {
            throw new IllegalArgumentException("Error: i should be different from j.");
        }

        Patient tmp = sequence.get(j);
        sequence.set(j, sequence.get(i));
        sequence.set(i, tmp);

        return sequence;
    }

    public static List<Patient> reverseSubsequence(List<Patient> sequence, int i, int j) {

        if (i == j) {
            throw new IllegalArgumentException("Error: i should be different from j.");
        }

        if (j < i) {
            int tmp = j;
            j = i;
            i = tmp;
        }
        int reversedLength = j - i + 1;
        int numSwaps = reversedLength / 2;
        for (int k = 0; k < numSwaps; k++) {
            Patient tmp = sequence.get(j);
            sequence.set(j, sequence.get(i));
            sequence.set(i, tmp);
            i = i + 1;
            j = j - 1;
        }

        return sequence;
    }

    public static List<Patient> makeACrossingOver(List<Patient> mother, List<Patient> father, int position) {

        if (mother == null || father == null) {
            throw new NullPointerException("Error: At least one of the parent has not been set");
        }

        if (mother.size() != father.size()) {
            throw new IllegalArgumentException("Error: Sequences of the parents differ in length");
        }

        List<Patient> child = new ArrayList();
        List<Patient> missing = new ArrayList();
        List<Integer> replica = new ArrayList();
        Random rd = new Random();

        for (int i = 0; i < position; i++) {
            child.add(mother.get(i));
        }
        for (int i = position; i < father.size(); i++) {
            child.add(father.get(i));
        }
        for (Patient gene : mother) {
            if (child.contains(gene)) {
                int occur = 0;
                for (int index = 0; index < child.size(); index++) {
                    if (child.get(index).equals(gene)) {
                        occur++;
                        if (occur > 1) {
                            replica.add(index);
                        }
                    }
                }
            } else {
                missing.add(gene);
            }
        }
        for (int index : replica) {
            Patient mutation = missing.get(rd.nextInt(missing.size()));
            child.set(index, mutation);
            missing.remove(missing.indexOf(mutation));
        }
        return child;
    }

    public static List<Patient> weightedInitialSolution(List<Patient> arrivalSequence, List<Integer> data) {

        List<Double> cancellationLikelihoods = new ArrayList();
        double cancellationLikelihood;
        List<Double> sortedCancellationLikelihoods = new ArrayList();
        int n = arrivalSequence.size();
        Double median;
        List<Patient> lowCancellationLikelihoods = new ArrayList();
        List<Patient> highCancellationLikelihoods = new ArrayList();
        List<Patient> weightedInitialSolution = new ArrayList();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) <= 84) {
                cancellationLikelihood = 0.5 * (- 1 / (0.05 * (data.get(i) + 20)) + ((double) 31 / 26));
            } else {
                cancellationLikelihood = 1 / (1 + Math.exp(-0.2 * (data.get(i) - 84)));
            }
            cancellationLikelihoods.add(cancellationLikelihood);
        }

        sortedCancellationLikelihoods.add(cancellationLikelihoods.get(0));

        for (int i = 1; i < n; i++) {
            int indexInsertion = 0;
            for (int j = 0; j < sortedCancellationLikelihoods.size(); j++) {
                if (cancellationLikelihoods.get(i) >= sortedCancellationLikelihoods.get(j)) {
                    indexInsertion++;
                } else {
                    break;
                }
            }
            sortedCancellationLikelihoods.add(indexInsertion, cancellationLikelihoods.get(i));
        }

        if (n % 2 == 0) {
            median = (sortedCancellationLikelihoods.get((n / 2) - 1) + sortedCancellationLikelihoods.get(n / 2)) / 2;
        } else {
            median = sortedCancellationLikelihoods.get(n / 2);
        }

        for (int i = 0; i < n; i++) {
            if (cancellationLikelihoods.get(Integer.getInteger(arrivalSequence.get(i).getPatientID().substring(1))) < median) {
                lowCancellationLikelihoods.add(arrivalSequence.get(i));
            } else if (cancellationLikelihoods.get(Integer.getInteger(arrivalSequence.get(i).getPatientID().substring(1))).equals(median)) {
                if (lowCancellationLikelihoods.size() <= highCancellationLikelihoods.size()) {
                    lowCancellationLikelihoods.add(arrivalSequence.get(i));
                } else {
                    highCancellationLikelihoods.add(arrivalSequence.get(i));
                }
            } else {
                highCancellationLikelihoods.add(arrivalSequence.get(i));
            }
        }

        for (int i = 0; i < n / 2; i++) {
            weightedInitialSolution.add(lowCancellationLikelihoods.get(i));
            weightedInitialSolution.add(highCancellationLikelihoods.get(i));
        }

        if (n % 2 != 0) {
            weightedInitialSolution.add(lowCancellationLikelihoods.get(n / 2));
        }
        
        return weightedInitialSolution;
    }

}
