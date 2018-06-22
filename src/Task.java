/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;

/**
 *
 * @author Hayat
 */
public class Task {

    private String processID;
    private String taskID;
    private int opMode;
    private int avTime;
    private int stdDev;
    private int maxWait;

    private ArrayList<String> prevTaskID;
    private ArrayList<String> nextTaskID;
    private Skill skill; 

    public Task(String processID, String taskID, int opMode, int avTime, int stdDev, int maxWait) {
        this.processID = processID;
        this.taskID = taskID;
        this.opMode = opMode;
        this.avTime = avTime;
        this.stdDev = stdDev;
        this.maxWait = maxWait;
        nextTaskID = new ArrayList();
        prevTaskID = new ArrayList();

    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void addNextTask(String nextTask) {
        nextTaskID.add(nextTask);
    }

    public void addPrevTask(String previousTask) {
        prevTaskID.add(previousTask);
    }

    /**
     * @return the prevTaskID
     */
    public ArrayList<String> getPrevTaskIDList() {
        return prevTaskID;
    }

    /**
     * @return the nextTaskID
     */
    public ArrayList<String> getNextTaskIDList() {
        return nextTaskID;
    }

    /**
     * @param prevTaskID the prevTaskID to set
     */
    public void setPrevTaskID(ArrayList<String> prevTaskID) {
        this.prevTaskID = prevTaskID;
    }

    /**
     * @param nextTaskID the nextTaskID to set
     */
    public void setNextTaskID(ArrayList<String> nextTaskID) {
        this.nextTaskID = nextTaskID;
    }

    /**
     * @return the taskID
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * @return the opMode
     */
    public int getOpMode() {
        return opMode;
    }

    /**
     * @return the avTime
     */
    public int getAvTime() {
        return avTime;
    }

    /**
     * @return the stdDev
     */
    public int getStdDev() {
        return stdDev;
    }

    /**
     * @return the maxWait
     */
    public int getMaxWait() {
        return maxWait;
    }

}
