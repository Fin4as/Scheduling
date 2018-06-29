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
    private int ageInformation;
    private double cancellationLikelihood;
    //Create the notion of Distance for the Greedy Algorithm this distance is used in the function getDistance()
    //Quentin I trust you on this one ;)

    public Patient(String id, String processID, int ageInformation) {
        this.patientID = id;
        schedule = new String[800];
        this.processID = processID;
        this.ageInformation = ageInformation;
        if (ageInformation <= 84) {
            this.cancellationLikelihood = 0.4 * (- 1 / (0.05 * (ageInformation + 20)) + ((double) 31 / 26));
        } else {
            this.cancellationLikelihood = 1 / (1 + Math.exp(-0.2 * (ageInformation - (5 * Math.log((double) 3 / 2) + 84))));
        }
    }

    public int getAgeInformation() {
        return ageInformation;
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
        int time = 0;
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

    public void setSchedule() {
        this.schedule = new String[800];
    }

    public double getCancellationLikelihood() {
        return cancellationLikelihood;
    }

}
