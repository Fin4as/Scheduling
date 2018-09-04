
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *This class describes a Resource
 * @author Hayat 
 */
public class Resource {

    private String resourceID;// variable to indetify a resource
    private int capacity; // variable that indicates the capacity of a resource, variable not used in the code
    private String name; // name of a resource, not used in the code
    private String time[]; // table that represents the time of for a resource
    private ArrayList<Integer> diagramValues; // List used by excelWriter to display a diagram

    /**
     * Constructor initializes variables
     *
     * @param resourceID variable to indetify a resource
     * @param capacity variable that indicates the capacity of a resource, variable not used in the code
     * @param name name of a resource, not used in the code
     */
    public Resource(String resourceID, int capacity, String name) {
        this.capacity = capacity;
        this.resourceID = resourceID;
        this.name = name;
        // list of time represnts more than 8 hours --> 800 minutes, allows to calculate lateness in class Test
        time = new String[800];
        diagramValues = new ArrayList<>();
    }

    /**
     * This method returns the table of time in format usable to create an excel file
     * @return diagramValues
     */
    public ArrayList<Integer> getDiagramValues() {
        return diagramValues;
    }

    /**
     *Method that returns  the time when the resource is available, return an integer corresponding to an index
     * @param startTime  the time when the task starts
     * @param avTime the duration of the task
     * @return available the time when the resource is available
     */
    public int getNextAvailableTime(int startTime, int avTime) {
        int available = -1;
        boolean found = false;
        int i = startTime;
        while (!found && i + avTime < time.length) {
            int j = i;
            boolean free = true;

            while (free & j <= i + avTime) {
                if (time[j] != null) {
                    free = false;
                }
                j++;
            }
            if (j == i + avTime + 1) {
                found = true;
                available = i;
            }
            i++;
        }

        return available;
    }

    /**
     *Method that takes two parameters : a start time indcating when the task starts and an avTime which is the duration of this task
     * @param startTime the time when the task starts
     * @param avTime  the duration of the task
     * @return available  a boolean that indicates if a resource is
     * available or not
     */
    public boolean isAvailable(int startTime, int avTime) {
        boolean available = false;
        boolean free = true;
        int i = startTime;
        if (i + avTime < time.length) {
            while (free && i <= startTime + avTime) {
                if (time[i] != null) {
                    free = false;
                }
                i++;
            }
            if (i == startTime + avTime + 1) {
                available = true;
            }
        }
        return available;
    }

    /**
     *Method to check if the list of time is empty
     * @param array it is the list of time of a Resource
     * @return a bolean is the array is empty or not
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
     * This methof returns the resourceID
     * @return the resourceID
     */
    public String getResourceID() {
        return resourceID;
    }

    /**
     * This methof returns the capacity
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * This methof returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * This methof returns the time
     * @return the time
     */
    public String[] getTime() {
        return time;
    }

    /**
     * Method to update a resource's list of time
     * @param start is the time when a task starts
     * @param avTime is the duration  of a task
     * @param taskID  is the ID of a task
     */
    public void setTime(int start, int avTime, String taskID) {
        for (int i = start; i < start + avTime; i++) {
            time[i] = taskID;
        }
    }

    /**
     * method to put a resource's list of time to zero, method called at the begining of addTask method in class Test
     */
    public void setZero() {
        this.time = new String[800];
    }

    /**
     * Method to fill diagramValues list from the list of time, which is used in excelWriter class
     */
    public void timeToDiagramValues() {
        String previousCell = time[0];
        int waiting = 0;
        int duration = 0;
        for (int i = 0; i < time.length; i++) {
            if (time[i] == null && previousCell == null) {
                waiting++;
            } else if (time[i] != null && previousCell == null) {
                diagramValues.add(waiting);
                waiting = 0;
                duration++;
            } else if (time[i] == null && previousCell != null) {
                diagramValues.add(duration);
                duration = 0;
                waiting++;

            } else if (time[i] != null && previousCell != null) {
                if (time[i].equals(previousCell)) {
                    if (i == 0) {
                        diagramValues.add(0);
                    }
                    duration++;
                } else {
                    diagramValues.add(0);
                    diagramValues.add(duration);
                    duration = 0;
                    duration++;
                }

            }
            previousCell = time[i];
        }

    }

}
