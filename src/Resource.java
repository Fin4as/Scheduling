
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
public class Resource {

    private String resourceID;
    private int capacity;
    private String name;
    private String time[];
    private ArrayList<Integer> diagramValues;

    public Resource(String resourceID, int capacity, String name) {
        this.capacity = capacity;
        this.resourceID = resourceID;
        this.name = name;
        time = new String[800];
        diagramValues = new ArrayList<>();
    }

    public ArrayList<Integer> getDiagramValues() {
        return diagramValues;
    }

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

    public boolean isEmptyStringArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the resourceID
     */
    public String getResourceID() {
        return resourceID;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time
     */
    public String[] getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int start, int avTime, String taskID) {
        for (int i = start; i < start + avTime; i++) {
            time[i] = taskID;
        }
    }

    public void setZero() {
        this.time = new String[800];
    }

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
