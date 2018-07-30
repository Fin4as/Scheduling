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
public abstract class Sequence {

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

    public static List<Patient> makeACrossingOver(List<Patient> mother, List<Patient> father, int percentage) {

        if (mother == null || father == null) {
            throw new NullPointerException("Error: At least one of the parent has not been set");
        }

        if (mother.size() != father.size()) {
            throw new IllegalArgumentException("Error: Sequences of the parents differ in length");
        }

        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Error: The percentage of the mother sequence kept must be between 0 and 100");
        }

        List<Patient> child = new ArrayList();
        List<Patient> missing = new ArrayList();
        List<Integer> replica = new ArrayList();
        int position = mother.size() * percentage / 100;
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

    public static List<List> sortByCancellationLikelihood(List<Patient> arrivalSequence) {
        int n = arrivalSequence.size();
        List<Patient> sortedCancellationLikelihoods = new ArrayList();
        List<Patient> lowCancellationLikelihoods = new ArrayList();
        List<Patient> highCancellationLikelihoods = new ArrayList();
        List<List> output = new ArrayList();

        for (int i = 0; i < n; i++) {
            int indexInsertion = 0;
            for (int j = 0; j < sortedCancellationLikelihoods.size(); j++) {
                if (arrivalSequence.get(i).getCancellationLikelihood() >= sortedCancellationLikelihoods.get(j).getCancellationLikelihood()) {
                    indexInsertion++;
                } else {
                    break;
                }
            }
            sortedCancellationLikelihoods.add(indexInsertion, arrivalSequence.get(i));
        }

        for (Patient p : sortedCancellationLikelihoods.subList(0, (int) Math.ceil(n / 2.0))) {
            lowCancellationLikelihoods.add(p);
        }
        for (Patient p : sortedCancellationLikelihoods.subList((int) Math.ceil(n / 2.0), n)) {
            highCancellationLikelihoods.add(p);
        }
        
        output.add(lowCancellationLikelihoods);
        output.add(highCancellationLikelihoods);

        return output;
    }

    public static List<Patient> weightedInitialSequence(List<Patient> arrivalSequence) {

        int n = arrivalSequence.size();
        List<Patient> weightedInitialSequence = new ArrayList();
        List<List> sortedCancellationLikelihoods = Sequence.sortByCancellationLikelihood(arrivalSequence);
        List<Patient> lowCancellationLikelihoods = sortedCancellationLikelihoods.get(0);
        List<Patient> highCancellationLikelihoods = sortedCancellationLikelihoods.get(1);
        
        for (int i = 0; i < n / 2; i++) {
            weightedInitialSequence.add(lowCancellationLikelihoods.get(i));
            weightedInitialSequence.add(highCancellationLikelihoods.get(i));
        }
        if (lowCancellationLikelihoods.size() != highCancellationLikelihoods.size()) {
            weightedInitialSequence.add(lowCancellationLikelihoods.get(lowCancellationLikelihoods.size() - 1));
        }
        return weightedInitialSequence;
    }

}