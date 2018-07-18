
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
        for (int i = 0; i < time.length; i++) {
            if (time[i] == null) {
                boolean free = true;
                int waiting = 0;
                int j = i;
                while (free && j < time.length) {
                    if (time[j] == null) {
                        waiting++;
                    } else {
                        free = false;
                    }

                    j++;
                }
                diagramValues.add(waiting);
                i = j - 1;
            } else {
                if (i == 0) {
                    diagramValues.add(0);
                }
                boolean busy = true;
                int committed = 0;
                int j = i;
                while (busy && j < time.length) {
                    if (time[j] == null) {
                        committed++;
                    } else {
                        busy = false;
                    }

                    j++;
                }
                diagramValues.add(committed);
                i = j - 1;
            }
        }
    }

}
