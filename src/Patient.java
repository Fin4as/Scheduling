//package Scheduling_First_Try;

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
    private String schedule[];
    private String processID;
    private int arrivalTime;
    private int ageInformation;
    private double cancellationLikelihood;
    private ArrayList<Integer> diagramValues;
    private ArrayList<String> diagramResourceIdUsed;
    //Create the notion of Distance for the Greedy Algorithm this distance is used in the function getDistance()
    //Quentin I trust you on this one ;)

    public Patient(String id, String processID, int arrivalTime, int ageInformation) {
        this.patientID = id;
        schedule = new String[800];
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
        this.arrivalTime = arrivalTime;
        diagramValues = new ArrayList<>();
        diagramResourceIdUsed = new ArrayList<>();
    }

    /**
     * @return the patientID
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     * @return the time
     */
    public String[] getSchedule() {
        return schedule;
    }

    public void setSchedule(int start, int avTime, String taskID) {
        for (int i = start; i < start + avTime; i++) {
            schedule[i] = taskID;
        }
    }

    public int getNextAvailableTime() {
        int time = arrivalTime;
        if (!isEmptyStringArray(schedule)) {
            int i = schedule.length - 1;
            boolean found = false;
            time = -1;
            while (!found && i >= 0) {
                if (schedule[i] != null) {
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
        this.schedule = new String[800];
    }

    public double getCancellationLikelihood() {
        return cancellationLikelihood;
    }

    public void addDiagramValues(int i) {
        diagramValues.add(i);
    }

    public void addDiagramResourceUsed(String r) {
        diagramResourceIdUsed.add(r);
    }

    public ArrayList<String> getDiagramResourcesIdUsed() {
        return diagramResourceIdUsed;
    }

    public ArrayList<Integer> getDiagramValues() {
        return diagramValues;
    }
}
