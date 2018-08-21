/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a task
 *
 * @author Hayat
 */
public class Task {

    private String processID; // a task has a processID, to know to which process it belongs
    private String taskID; // a task has an ID to be indentify
    private int opMode; // the opertaion Mode decribes if a task is waiting or non waiting
    private int avTime; // a task has an avTime which corresponds to its duration
    private int stdDev; // a task has a standard deviation, used in class Data to generate stochastic values
    private int maxWait; // variable maximm wait is not used in the code. It represents the maximum wait allowed betweeen this task and the previous one
    private Task parallelTask; // this object is for task which as parallel task otherwise it's null
    private int patientPresence; // this variable aims to indicate if a patient is involved in the task, 0 = non involved, 1 = involved

    private List<String> prevTaskID; // this list provides the previous task of the current Task
    private List<String> nextTaskID; // this task provides the next tasks of the current Task
    private ArrayList<Skill> listSkill; // this list represents ths skills needed by this Task

    /**
     * Constructor initilizes variables and lists of NextTask, PreviousTask and
     * Skill
     *
     * @param processID is the ID of the process the task belongs to
     * @param taskID is the ID of the task
     * @param patientP is an integer which indicates if the patient is involved
     * or not
     * @param opMode is the operation mode of task , 0 = non waiting task, 1 =
     * waiting task
     * @param avTime is the duration of the task
     * @param stdDev is the standard deviation of the task
     * @param maxWait is the maximum wait, not used in the code
     */
    public Task(String processID, String taskID, int patientP, int opMode, int avTime, int stdDev, int maxWait) {
        this.processID = processID;
        this.taskID = taskID;
        this.patientPresence = patientP;
        this.opMode = opMode;
        this.avTime = avTime;
        this.stdDev = stdDev;
        this.maxWait = maxWait;
        nextTaskID = new ArrayList();
        prevTaskID = new ArrayList();
        parallelTask = null;
        listSkill = new ArrayList<>();

    }

    /**
     *
     * @return bject parallelTask
     */
    public Task getParallelTask() {
        return parallelTask;
    }

    /**
     *
     * @return value of presence patient (0 = non involved in the task, 1 =
     * invloved)
     */
    public int getPatientPresence() {
        return this.patientPresence;
    }

    /**
     *
     * @param p is 0 or 1 (0 = non involved in the task, 1 = invloved)
     */
    public void setPatientPresence(int p) {
        this.patientPresence = p;
    }

    /**
     *
     * @param avT is duration of the task
     */
    public void setAvTime(int avT) {
        this.avTime = avT;
    }

    /**
     *
     *
     * @param pt is parallel Task
     */
    public void setParallelTask(Task pt) {
        parallelTask = pt;
    }

    /**
     *
     * @return processID of the task
     */
    public String getProcessID() {
        return processID;
    }

    /**
     *
     * @return list of skill of the task
     */
    public ArrayList<Skill> getListSkill() {
        return listSkill;
    }

    /**
     * Method to set standard deviation
     *
     * @param s is the standard deviation
     */
    public void setStdDev(int s) {
        this.stdDev = s;
    }

    /**
     * Method to ass a skill in list of skill
     *
     * @param skill
     */
    public void setListSkill(Skill skill) {
        this.listSkill.add(skill);
    }

    /**
     * Method to add a Next Task (which is a task ID) of the Task
     *
     * @param nextTask
     */
    public void addNextTask(String nextTask) {
        nextTaskID.add(nextTask);
    }

    /**
     *Method to add a Previous Task (which is a task ID) of the Task
     * @param previousTask
     */
    public void addPrevTask(String previousTask) {
        prevTaskID.add(previousTask);
    }

    /**
     * @return the prevTaskID list
     */
    public List<String> getPrevTaskIDList() {
        return prevTaskID;
    }

    /**
     * @return the nextTaskID list
     */
    public List<String> getNextTaskIDList() {
        return nextTaskID;
    }

    /**Method to set the set the list of previous task of the current task
     * @param prevTaskID 
     */
    public void setPrevTaskID(List<String> prevTaskID) {
        this.prevTaskID = prevTaskID;
    }

    /**
     * Method to set the list of next task of the current task
     * @param nextTaskID 
     */
    public void setNextTaskID(List<String> nextTaskID) {
        this.nextTaskID = nextTaskID;
    }

    /**
     * @return the taskID
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * @return the opMode (0= non waiting, 1 = waiting)
     */
    public int getOpMode() {
        return opMode;
    }

    /**
     * @return the avTime which is the duration 
     */
    public int getAvTime() {
        return avTime;
    }

    /**
     * @return the stdDev which is the standard vediation
     */
    public int getStdDev() {
        return stdDev;
    }


}
