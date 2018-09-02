
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
     * This method creates a sequence by doing a crossing over with two other
     * sequences
     *
     * @param mother the first sequence used to create the new one
     * @param father the second sequence used to create the new one
     * @param startPercentage the lower percentage bound of the father sequence
     * kept
     * @param endPercentage the upper percentage bound of the father sequence
     * kept
     * @param reverse true if the sequence created must be reversed at the end,
     * false otherwise
     * @throws NullPointerException if at least one of the parents has not been
     * set
     * @throws IllegalArgumentException if the sequences of the parents differ
     * in length
     * @throws IllegalArgumentException if the percentage bounds of the father
     * sequence kept are not between 0 and 100
     * @throws IllegalArgumentException if the lower percentage bound of the
     * father sequence kept is higher than the upper percentage
     * @return the sequence created by crossing over
     */
    public static List<Patient> makeACrossingOver(List<Patient> mother, List<Patient> father, int startPercentage, int endPercentage, boolean reverse) {
        // Throw errors to forbid unexpected input. These errors should never happen unless the metaheuristic algorithms gave wrong parameters to this method.
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

        List<Patient> child = new ArrayList(); // The new sequence generated by the method
        List<Patient> missing = new ArrayList(); // The sublist of patients missing in the child, ordered in the same order than in the mother sequence
        List<Integer> replica = new ArrayList(); // The sublist of the positions of the patients existing twice in the child, ordered in the same order than in the child
        int start = father.size() * startPercentage / 100; // Conversion of the lower percentage bound of the father sequence kept to an index
        int end = father.size() * endPercentage / 100; // Conversion of the upper percentage bound of the father sequence kept to an index

        // Merging of the two parents to create a child
        for (int i = 0; i < start; i++) {
            child.add(mother.get(i));
        }
        for (int i = start; i < end; i++) {
            child.add(father.get(i));
        }
        for (int i = end; i < mother.size(); i++) {
            child.add(mother.get(i));
        }

        // Filling of the sublist of patients missing in the child 
        for (Patient gene : mother) {
            if (!child.contains(gene)) {
                missing.add(gene);
            }
        }

        // Only one of the following crossing over is executed, depending on the parameters given to the method
        // Single point crossing over
        if (end == father.size()) {
            for (int index = start; index < child.size(); index++) {
                int occur = 0; // Counts the number of occurrences of each patient
                for (int i = 0; i < child.size(); i++) {
                    Patient gene = child.get(index); // The patient currently checked
                    if (child.get(i).equals(gene)) {
                        occur++;
                    }
                }
                // If a patient exists more than once in the sequence, the position of the replica is added to the related sublist
                if (occur > 1) {
                    replica.add(index);
                }
            }
        } // Two point crossing over
        else {
            for (int indexNewGene = start; indexNewGene < end; indexNewGene++) {
                int occur = 0; // Counts the number of occurrences of each patient
                int indexReplica = -1; // Allows to check that the position of the replica is in the paternal part of the child
                for (int i = 0; i < child.size(); i++) {
                    Patient newGene = child.get(indexNewGene); // The patient currently checked
                    if (child.get(i).equals(newGene)) {
                        occur++;
                        // indexReplica saves the position of the replica only if its the one in the paternal part of the child
                        if (!(i >= start && i < end)) {
                            indexReplica = i;
                        }
                    }
                }
                // If a patient exists more than once in the sequence, the position of the replica is added to the related sublist
                if (occur > 1) {
                    replica.add(indexReplica);
                }
            }
        }
        // Mutations : Replica in the child are replaced by the missing patients, in the same order than in the mother sequence
        for (int indexReplica : replica) {
            Patient mutation = missing.get(0); // We select the first missing patient
            child.set(indexReplica, mutation); // We replace the first replica by th previously selected patient
            missing.remove(missing.indexOf(mutation)); // We remove the selected patient from the sublist as it is no longer missing
        }

        // An option of genetic mixing. Reverses the child sequence after the mutations if asked in the parameters.
        if (reverse == true) {
            Collections.reverse(child);
        }

        return child;
    }

    /**
     * This method goes through a sequence of patients and creates two lists.
     * Half of the patients with the lowest cancellation likelihoods are added
     * to the first list. Half of the patients with the highest cancellation
     * likelihoods are added to the second list. The order of patients in each
     * sublists is sorted by ascending value of cancellation likelihood.
     *
     * @param arrivalSequence the sequence to be divided into two sublists
     * @return a list containing two sublists of patients, one for the low
     * cancellation likelihood patients, the other for the high cancellation
     * likelihood patients, all taken from the given sequence.
     */
    public static List<List> sortByCancellationLikelihood(List<Patient> arrivalSequence) {

        int n = arrivalSequence.size(); // The size of the arrival sequence
        List<Patient> sortedCancellationLikelihoods = new ArrayList(); // A copy of the arrival sequence, sorted by ascending value of cancellation likelihood
        List<Patient> lowCancellationLikelihoods = new ArrayList(); // The sublist filled with the low cancellation likelihood patients
        List<Patient> highCancellationLikelihoods = new ArrayList(); // The sublist filled with the high cancellation likelihood patients
        List<List> output = new ArrayList(); // The list containing the two previous sublists due to output restrictions

        // An insertion sort in done on the arrival sequence to fill its copy sorted by ascending value of cancellation likelihood
        for (int i = 0; i < n; i++) {
            int indexInsertion = 0;
            // The list is scanned until reaching the last patient with a lower cancellation likelihood than the patient to be inserted
            for (int j = 0; j < sortedCancellationLikelihoods.size(); j++) {
                if (arrivalSequence.get(i).getCancellationLikelihood() >= sortedCancellationLikelihoods.get(j).getCancellationLikelihood()) {
                    indexInsertion++;
                } else {
                    break;
                }
            }
            sortedCancellationLikelihoods.add(indexInsertion, arrivalSequence.get(i)); // The patient to be inserted is added right after the patient previously reached
        }

        // The first half of the sorted list is used to fill the first sublist
        for (Patient p : sortedCancellationLikelihoods.subList(0, (int) Math.ceil(n / 2.0))) {
            lowCancellationLikelihoods.add(p);
        }
        // The second half of the sorted list is used to fill the second sublist
        for (Patient p : sortedCancellationLikelihoods.subList((int) Math.ceil(n / 2.0), n)) {
            highCancellationLikelihoods.add(p);
        }
        // The two sublists are added to a list to return both of them
        output.add(lowCancellationLikelihoods);
        output.add(highCancellationLikelihoods);

        return output;
    }

    /**
     * This method applies a low and high cancellation probability structure to
     * the given list. The sorted list is as close as possible to the original
     * list.
     *
     * @param arrivalSequence the sequence on which to apply the low and high
     * cancellation likelihood structure
     * @return the sequence as close as possible to the one given in parameters
     * having a low and high cancellation likelihood structure
     */
    public static List<Patient> weightedInitialSequence(List<Patient> arrivalSequence) {

        int n = arrivalSequence.size(); // The size of the arrival sequence
        List<Patient> weightedInitialSequence = new ArrayList(); // The sequence to be returned
        List<Patient> lowCancellationLikelihoods = new ArrayList(); // The sublist to be filled with the low cancellation likelihood patients of the arrival sequence, sorted in the same order than in the arrival sequence
        List<Patient> highCancellationLikelihoods = new ArrayList(); // The sublist to be filled with the high cancellation likelihood patients of the arrival sequence, sorted in the same order than in the arrival sequence
        List<List> sortedCancellationLikelihoods = Sequence.sortByCancellationLikelihood(arrivalSequence);
        List<Patient> sortedLowCancellationLikelihoods = sortedCancellationLikelihoods.get(0); // The sublist filled with the patients of the sequence given in parameters, sorted by ascending value of low cancellation likelihood
        List<Patient> sortedHighCancellationLikelihoods = sortedCancellationLikelihoods.get(1); // The sublist filled with the patients of the sequence given in parameters, sorted by ascending value of high cancellation likelihood

        // The sublists are filled according to the value of cancellation likelihood of each patient. The order is saved as in the arrival sequence.
        for (Patient p : arrivalSequence) {
            // If the patient has a low cancellation likelihood, we add the patient in the related sublist
            if (sortedLowCancellationLikelihoods.contains(p)) {
                lowCancellationLikelihoods.add(p);
            } // If the patient has a high cancellation likelihood, we add the patient in the related sublist
            else if (sortedHighCancellationLikelihoods.contains(p)) {
                highCancellationLikelihoods.add(p);
            } // Error taken into account by the method, but it should never happen as a patient cannot be instantiated without a cancellation likelihood
            else {
                throw new IllegalArgumentException("Error: Cannot find the cancellation likelihood of a patient in the initial sequence. This patient might not have been properly initialized.");
            }
        }

        // We fill the sequence to be returned by adding alternatively a patient with a low or a high cancellation likelihood
        for (int i = 0; i < n / 2; i++) {
            weightedInitialSequence.add(lowCancellationLikelihoods.get(i));
            weightedInitialSequence.add(highCancellationLikelihoods.get(i));
        }
        // We add the last patient if the arrival sequence contains an odd number of patients
        if (lowCancellationLikelihoods.size() != highCancellationLikelihoods.size()) {
            weightedInitialSequence.add(lowCancellationLikelihoods.get(lowCancellationLikelihoods.size() - 1));
        }
        return weightedInitialSequence;
    }

}
