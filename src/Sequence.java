
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains all the methods useful for the sequencing: Neighbourhood
 * search methods and tools to build sequences with a cancellation likelihood
 * structure.
 *
 * @author Quentin Pierunek
 */
public abstract class Sequence {

    /**
     * This method modifies a sequence by swapping the two patients at the given
     * positions.
     *
     * @param sequence the sequence to be modified.
     * @param i the position of the first patient to be swapped.
     * @param j the position of the second patient to be swapped.
     * @throws IllegalArgumentException if the value of i and j is the same.
     * @return the same instance of the sequence given in parameters with the
     * patients in position i and j that were swapped.
     */
    public static List<Patient> simpleSwap(List<Patient> sequence, int i, int j) {

        // Throw an error to forbid not to do a swap.
        if (i == j) {
            throw new IllegalArgumentException("Error: i should be different from j.");
        }

        Patient tmp = sequence.get(j); // Create a copy of the second patient
        sequence.set(j, sequence.get(i)); // Replace the second patient by the first patient
        sequence.set(i, tmp); // Replace the first patient by the copy of the second patient

        return sequence;
    }

    /**
     * This method modifies a sequence by reversing the subsequence within two
     * patients (both included).
     *
     * @param sequence the sequence to be modified.
     * @param i the position of the first bound of the subsequence to be
     * reversed (the patient is included in the subsequence)
     * @param j the position of the second bound of the subsequence to be
     * reversed (the patient is included in the subsequence)
     * @throws IllegalArgumentException if the value of i and j is the same.
     * @return the same instance of the sequence given in parameters with the
     * subsequence between patients in position i and j that was reversed.
     */
    public static List<Patient> reverseSubsequence(List<Patient> sequence, int i, int j) {

        // Throw an error to forbid not to do a swap.
        if (i == j) {
            throw new IllegalArgumentException("Error: i should be different from j.");
        }

        // Make sure that i is lower than j to reverse the subsequence
        if (j < i) {
            int tmp = j; // Create a copy of the second index
            j = i; // Replace the second index by the first index 
            i = tmp; // Replace the first index by the copy of the second index
        }
        int reversedLength = j - i + 1; // Number of patients in the subsequence
        int numSwaps = reversedLength / 2; // Number of swaps to do to reverse the subsequence
        
        /* For each swaps to be done, the two patients at the bounds of the
           sequence are swapped, then both bounds are narrowed of one patient*/
        for (int k = 0; k < numSwaps; k++) {
            Patient tmp = sequence.get(j); // Create a copy of the second patient
            sequence.set(j, sequence.get(i)); // Replace the second patient by the first patient
            sequence.set(i, tmp); // Replace the first patient by the copy of the second patient
            i = i + 1; // The lower bound is increased
            j = j - 1; // The upper bound is lowered
        }

        return sequence;
    }

    /**
     * This method creates a sequence by doing a crossing over with two other sequences
     * @param mother the first sequence used to create the new one
     * @param father the second sequence used to create the second one
     * @param startPercentage the lower percentage bound of the father sequence kept
     * @param endPercentage the upper percentage bound of the father sequence kept
     * @param reverse true if the sequence created must be reversed at the end, false otherwise
     * @throws NullPointerException if at least one of the parents has not been set
     * @throws IllegalArgumentException if the sequences of the parents differ in length
     * @throws IllegalArgumentException if the percentage bounds of the father sequence kept are not between 0 and 100
     * @throws IllegalArgumentException if the lower percentage bound of the father sequence kept is higher than the upper percentage
     * @return the sequence created by crossing over 
     */
    public static List<Patient> makeACrossingOver(List<Patient> mother, List<Patient> father, int startPercentage, int endPercentage, boolean reverse) {

        if (mother == null || father == null) {
            throw new NullPointerException("Error: At least one of the parents has not been set");
        }

        if (mother.size() != father.size()) {
            throw new IllegalArgumentException("Error: Sequences of the parents differ in length");
        }

        if (startPercentage < 0 || endPercentage > 100) {
            throw new IllegalArgumentException("Error: The percentage bounds of the father sequence kept must be between 0 and 100");
        }

        if (startPercentage > endPercentage) {
            throw new IllegalArgumentException("Error: startPercentage must be lower than endPercentage");
        }

        List<Patient> child = new ArrayList();
        List<Patient> missing = new ArrayList();
        List<Integer> replica = new ArrayList();
        int start = father.size() * startPercentage / 100;
        int end = father.size() * endPercentage / 100;

        for (int i = 0; i < start; i++) {
            child.add(mother.get(i));
        }
        for (int i = start; i < end; i++) {
            child.add(father.get(i));
        }
        for (int i = end; i < mother.size(); i++) {
            child.add(mother.get(i));
        }

        for (Patient gene : mother) {
            if (!child.contains(gene)) {
                missing.add(gene);
            }
        }

        // Single point crossing over
        if (end == father.size()) {
            for (int index = start; index < child.size(); index++) {
                int occur = 0;
                for (int i = 0; i < child.size(); i++) {
                    Patient gene = child.get(index);
                    if (child.get(i).equals(gene)) {
                        occur++;
                    }
                }
                if (occur > 1) {
                    replica.add(index);
                }
            }
        } // Two point crossing over
        else {
            for (int indexNewGene = start; indexNewGene < end; indexNewGene++) {
                int occur = 0;
                int indexReplica = -1;
                for (int i = 0; i < child.size(); i++) {
                    Patient newGene = child.get(indexNewGene);
                    if (child.get(i).equals(newGene)) {
                        occur++;
                        if (!(i >= start && i < end)) {
                            indexReplica = i;
                        }
                    }
                }
                if (occur > 1) {
                    replica.add(indexReplica);
                }
            }
        }

        for (int indexReplica : replica) {
            Patient mutation = missing.get(0);
            child.set(indexReplica, mutation);
            missing.remove(missing.indexOf(mutation));
        }

        if (reverse == true) {
            Collections.reverse(child);
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
        List<Patient> lowCancellationLikelihoods = new ArrayList();
        List<Patient> highCancellationLikelihoods = new ArrayList();
        List<List> sortedCancellationLikelihoods = Sequence.sortByCancellationLikelihood(arrivalSequence);
        List<Patient> sortedLowCancellationLikelihoods = sortedCancellationLikelihoods.get(0);
        List<Patient> sortedHighCancellationLikelihoods = sortedCancellationLikelihoods.get(1);
        
        for (Patient p : arrivalSequence){
            if (sortedLowCancellationLikelihoods.contains(p)){
                lowCancellationLikelihoods.add(p);
            }
            else if (sortedHighCancellationLikelihoods.contains(p)){
                highCancellationLikelihoods.add(p);
            }
            else{
                throw new IllegalArgumentException("Error: Cannot find the cancellation likelihood of a patient in the initial sequence. This patient might not have been properly initialized.");
            }
        }

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
