
import java.util.ArrayList;

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

    public Patient(String id, String processID, int ageInformation, String typeSurgery) {
        this.patientID = id;
        schedule = new String[800];
        parallelSchedules = new ArrayList<>();
        parallelSchedules.add(schedule);
        this.processID = processID;
        this.ageInformation = ageInformation;
        if (ageInformation <= 7 || ageInformation > 112) {
            cancellationLikelihood = 1;
        } else if (ageInformation > 7 && ageInformation <= 15) {
            cancellationLikelihood = (- 1 / (1 + Math.exp(-2 * (ageInformation - 11)))) + 1;
        } else if (ageInformation > 15 && ageInformation <= 105) {
            cancellationLikelihood = 0;
        } else if (ageInformation > 105 && ageInformation <= 112) {
            cancellationLikelihood = (- 1 / (1 + Math.exp(-2 * (ageInformation - 109))));
        } else {
            throw new IllegalArgumentException("Cancellation likelihood could not be calculated");
        }
        switch (typeSurgery) {
            case "Gastroenterology":
                cancellationLikelihood += ((Double) (151.0 / (780*129)));
                break;
            case "Ear/Nose/Throat":
                cancellationLikelihood += ((Double) (91.0 / (780*129)));
                break;
            case "Urology/Endocrinology":
                cancellationLikelihood += ((Double) (88.0 / (780*129)));
                break;
            case "Orthopedics":
                cancellationLikelihood += ((Double) (82.0 / (780*129)));
                break;
            case "Women's clinic":
                cancellationLikelihood += ((Double) (80.0 / (780*129)));
                break;
            case "Anesthesiology Procedures":
                cancellationLikelihood += ((Double) (75.0 / (780*129)));
                break;
            case "Ophthalmology":
                cancellationLikelihood += ((Double) (69.0 / (780*129)));
                break;
            case "Neurosurgery":
                cancellationLikelihood += ((Double) (58.0 / (780*129)));
                break;
            case "Plastic/Hand":
                cancellationLikelihood += ((Double) (51.0 / (780*129)));
                break;
            case "Cardio/Lung/Vascular":
                cancellationLikelihood += ((Double) (26.0 / (780*129)));
                break;
            case "Common Procedures":
                cancellationLikelihood += ((Double) (5.0 / (780*129)));
                break;
            case "Pediatrics":
                cancellationLikelihood += ((Double) (4.0 / (780*129)));
            default:
                break;
        }
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
