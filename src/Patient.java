
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hayat
 */
public class Patient {

    private String patientID;
    private ArrayList<String[]> parallelSchedules;
    private String schedule[];
    private String processID;
    private int ageInformation;
    private double cancellationLikelihood;
    private ArrayList<ArrayList<Integer>> diagramValues;
    private ArrayList<Integer> diagram;

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

    public void addParallelSchedule(String[] s) {
        parallelSchedules.add(s);
    }

    public int getAgeInformation() {
        return ageInformation;
    }

    public void addArrayDiagram(int i) {
        diagramValues.add(new ArrayList<>());
    }

    /**
     * @return the patientID
     */
    public String getPatientID() {
        return patientID;
    }

    public ArrayList<String[]> getParallelSchedules() {
        return parallelSchedules;
    }

    /**
     * @return the time
     */
    public String[] getSchedule() {
        return schedule;
    }

    public void setSchedule(int s, int start, int avTime, String taskID) {
        String[] currentSchedule = parallelSchedules.get(s);
        for (int i = start; i < start + avTime; i++) {
            currentSchedule[i] = taskID;
        }
    }

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

    public boolean isEmptyStringArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }

    public String getProcessID() {
        return processID;
    }

    public void setZeroSchedule() {
        parallelSchedules = new ArrayList<>();
        parallelSchedules.add(new String[800]);

    }

    public double getCancellationLikelihood() {
        return cancellationLikelihood;
    }

    public void addDiagramValues(int i, int value) {
        diagramValues.get(i).add(value);
    }

    public ArrayList<ArrayList<Integer>> getDiagramValues() {
        return diagramValues;
    }
}
