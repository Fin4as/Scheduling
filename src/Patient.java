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

    public Patient(String id, String processID) {
        this.patientID = id;
        schedule = new String[800];
        this.processID = processID;
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
            time=-1;
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
}
