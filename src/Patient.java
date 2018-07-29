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
    private ArrayList<String[]> parallelSchedules;
    private String schedule[];
    private String processID;
    private int arrivalTime;
    private int ageInformation;
    private double cancellationLikelihood;
    private ArrayList<ArrayList<Integer>> diagramValues;
    private ArrayList<Integer> diagram;

    //Create the notion of Distance for the Greedy Algorithm this distance is used in the function getDistance()
    public Patient(String id, String processID, int arrivalTime, int ageInformation) {
        this.patientID = id;
        schedule = new String[800];
        parallelSchedules = new ArrayList<>();
        parallelSchedules.add(schedule);
        this.processID = processID;
        this.ageInformation = ageInformation;
        if (ageInformation <= 84) {
            this.cancellationLikelihood = 0.4 * (- 1 / (0.05 * (ageInformation + 20)) + ((double) 31 / 26));
        } else {
            this.cancellationLikelihood = 1 / (1 + Math.exp(-0.2 * (ageInformation - (5 * Math.log((double) 3 / 2) + 84))));
        }
        this.arrivalTime = arrivalTime;
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
    
    public void addArrayDiagram(int i){
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
        int time = arrivalTime;
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
