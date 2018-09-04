
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */
/**
 * Describes a patient
 * @author Hayat 
 */
public class Patient {

    private String patientID; // variable to identify a patient
    private ArrayList<String[]> parallelSchedules; // in case there a parallel task
    private String schedule[]; // List that respresents the time for a patient
    private String processID; // variable to identify the process assigned to a patient
    private int ageInformation;
    private double cancellationLikelihood;
    private ArrayList<ArrayList<Integer>> diagramValues; // this list stores durations of tasks and waiting times. It is used by excel writer to create a diagram
    private ArrayList<Integer> diagram;
/**
 * Constructor of the Patient class
 * @param id id of the patient
 * @param processID id of the process
 * @param ageInformation age of the information about the patient
 * @param typeSurgery type of surgery
 * @param numberPatientsPerSurgery Number of patients per surgery 
 */
    public Patient(String id, String processID, int ageInformation, String typeSurgery, List<Integer> numberPatientsPerSurgery) {
        this.patientID = id;
        schedule = new String[800];
        parallelSchedules = new ArrayList<>();
        parallelSchedules.add(schedule);
        this.processID = processID;
        this.ageInformation = ageInformation;
        double validationLikelihoodInfo;
        if (ageInformation <= 7 || ageInformation > 97) {
            validationLikelihoodInfo = 0;
        } else if (ageInformation > 7 && ageInformation <= 14) {
            validationLikelihoodInfo = (ageInformation - 7) * ((double) 1 / 7);
        } else if (ageInformation > 14 && ageInformation <= 90) {
            validationLikelihoodInfo = 1;
        } else if (ageInformation > 90 && ageInformation <= 97) {
            validationLikelihoodInfo = ((90 - ageInformation) * ((double) 1 / 7) + 1);
        } else {
            throw new IllegalArgumentException("Validation likelihood could not be calculated");
        }
        double observedFrequency;
        double validationLikelihoodTypeSurg = 1;
        switch (typeSurgery) {
            case "Gastroenterology":
                observedFrequency = ((double) (151.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(0));
                break;
            case "Ear/Nose/Throat":
                observedFrequency = ((double) (92.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(1));
                break;
            case "Urology/Endocrinology":
                observedFrequency = ((double) (87.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(2));
                break;
            case "Orthopedics":
                observedFrequency = ((double) (83.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(3));
                break;
            case "Women's clinic":
                observedFrequency = ((double) (81.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(4));
                break;
            case "Anesthesiology Procedures":
                observedFrequency = ((double) (75.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(5));
                break;
            case "Ophthalmology":
                observedFrequency = ((double) (69.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(6));
                break;
            case "Neurosurgery":
                observedFrequency = ((double) (57.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(7));
                break;
            case "Plastic/Hand":
                observedFrequency = ((double) (51.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(8));
                break;
            case "Cardio/Lung/Vascular":
                observedFrequency = ((double) (27.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(9));
                break;
            case "Common Procedures":
                observedFrequency = ((double) (6.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(10));
                break;
            case "Pediatrics":
                observedFrequency = ((double) (4.0 / 129));
                validationLikelihoodTypeSurg = 1 - ((double) observedFrequency / numberPatientsPerSurgery.get(11));
            default:
                break;
        }
        if (validationLikelihoodTypeSurg < 0) {
            validationLikelihoodTypeSurg = 0;
        }
        double validationLikelihood = validationLikelihoodInfo * validationLikelihoodTypeSurg;
        cancellationLikelihood = 1 - validationLikelihood;

        diagramValues = new ArrayList<>();
        diagram = new ArrayList<>();
        diagramValues.add(diagram);

    }

    /**
     *
     * Add parallel list of time
     * @param s table of time of a schedule 
     */
    public void addParallelSchedule(String[] s) {
        parallelSchedules.add(s);
    }
/**
 * This method returns the age of the information
 * @return the age of the information
 */
    public int getAgeInformation() {
        return ageInformation;
    }

    /**
     *This method add values (task duration and waiting times) in list diagramValues 
     */
    public void addArrayDiagram() {
        diagramValues.add(new ArrayList<>());
    }

    /**
     * This method returns the patientID
     * @return the patientID
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     * This is supposedly used for parallel tasks
     * @return parallel schedules
     */
    public ArrayList<String[]> getParallelSchedules() {
        return parallelSchedules;
    }

    /**
     * This method returns the time
     * @return the time
     */
    public String[] getSchedule() {
        return schedule;
    }

    /**
     *
     * This method is used to update the list of time of the patient. Updated by adding the
     * taskID of the current task
     * @param s .
     * @param start .
     * @param avTime .
     * @param taskID .
     */
    public void setSchedule(int s, int start, int avTime, String taskID) {
        String[] currentSchedule = parallelSchedules.get(s);
        for (int i = start; i < start + avTime; i++) {
            currentSchedule[i] = taskID;
        }
    }

    /**
     *
     * This method is used to know when the last task ends , to be able to add the next task.
     * @return the time
     */
    public int getNextAvailableTime() {
        int time = 1;
        if (!isEmptyStringArray(parallelSchedules.get(0))) {
            int i = parallelSchedules.get(0).length - 1;
            boolean found = false;
            time = -1;
            while (!found && i >= 0) {
                if (parallelSchedules.get(0)[i] != null) {
                    time = i + 1;
                    found = true;
                }
                i--;
            }
        }
        return time;
    }

    /**
     *
     * returns a boolean to check if the table of time is empty or not
     * @param array the table of time
     * @return the table of time is empty or not
     */
    public boolean isEmptyStringArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * This methode returns the processID
     * @return processID
     */
    public String getProcessID() {
        return processID;
    }

    /**
     *
     * set the table of time(called schedule) to null. Method needed in AddTask
     * method in class test. As everyTime a new sequence of patients is
     * generated; time table must be cleared 
     */
    public void setZeroSchedule() {
        parallelSchedules = new ArrayList<>();
        parallelSchedules.add(new String[800]);

    }

    /**
     * This method returns the cancellation likelihood
     * @return the cancellation likelihood
     */
    public double getCancellationLikelihood() {
        return cancellationLikelihood;
    }

    /**
     *Method called by AddTask in Class Test
     * method to add task durations and waiting times in list Diagram Values
     * @param i .
     * @param value .
     */
    public void addDiagramValues(int i, int value) {
        diagramValues.get(i).add(value);
    }

    /**
     * List used by excel Writer
     * @return  diagram values list
     */
    public ArrayList<ArrayList<Integer>> getDiagramValues() {
        return diagramValues;
    }
}
