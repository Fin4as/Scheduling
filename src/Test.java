/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package Scheduling_First_Try;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates values : makespan, lateness and total waiting times
 * According to calculations realized by AddTask method
 * It contains the main algorithm to scheudle patient appointment and resource allocation
 *
 * @author Hayat
 */
public class Test {

    List<Patient> listPatient; // list of patient 
    List<Process> listProcess; // list of process of the patients
    List<Resource> listResource; // list of resources needed to schedule the list of process
    int totalWaitingTime; // variable to calculate the total waiting time of the patients
    int lateness; // variable to calculate the lateness once the scheduling is done
    
    int updateStart;// variable used to changed parameter value in researchResources

    /**
     * Constructor initializes variables
     *
     * @param sequence is a list of Patients
     * @param s is the data of process needed, obtained from Database
     */
    public Test(List<Patient> sequence, Data s) {
        listPatient = sequence;
        listResource = s.getAllResources();
        totalWaitingTime = 0;
        lateness = 0;
        listProcess = s.getListProcess();
    }

    /**
     * This method returns the lateness value
     * @return lateness value
     */
    public int getLateness() {
        lateness = this.lateness();
        return lateness;
    }

    /**
     * This method returns the total waiting time value
     * @return total waiting time value
     */
    public int getTotalWaitingTime() {
        return totalWaitingTime;
    }

    /**
     * This method returns the list of Resource
     * @return list of Resource
     */
    public List<Resource> getListResource() {
        return listResource;
    }

    /**
     * This method returns list of processes
     * @return list of process
     */
    public List<Process> getListProcess() {
        return listProcess;
    }

    /**
     * Method to get a Process, used in addTask method
     *
     * @param processID id of the process
     * @return p a Process
     */
    public Process getProcess(String processID) {

        Process p = null;
        boolean found = false;
        int i = 0;
        //this loop aims to return process objects corresponding to process ID patients have
        while ((i < listProcess.size()) && (!found)) {
            if (listProcess.get(i).getID().equals(processID)) {
                p = listProcess.get(i);
                found = true;
            } else {
                i++;

            }
        }

        return p;
    }

    /**
     * Method to calculate lateness
     *
     * @return lateness
     */
    public int lateness() {

        int maxLateness = 0;
        for (int j = 0; j < listPatient.size(); j++) {
            //variable end indicates the list of time of a patient
            int end = 799;
            //this loop browses list of times of patients and return the one ending the latest (which is maxLateness)
            while (end >= 0) {
                
                if (listPatient.get(j).getParallelSchedules().get(0)[end] == null) {
                   //the list is browsed from the end that why it is end--
                    end--;
                } else {
                    if (maxLateness < end) {
                        // +1 is needed because index different from time
                        maxLateness = end + 1; 
                    }
                    break;
                }
            }
        }
        //Soutraction by 480 minutes is realized as it corresponds to 8 hours
        return maxLateness - 480;
    }

    /**
     * Method to calculate Makespan, the longest process of the scheduling
     *
     * @return mskp  coresponds to makespan
     */
    public int calculateMakespan() {
        int max = 0;
        int min = 0;
        int mksp;
        //the first list of time is browsed to calculate makespan, the starting and the endinf times of the first patient's process is store
        //to be then compared to th other patients, with the goal to pick the patient who has the difference end-start the longest
        String[] schedule = listPatient.get(0).getParallelSchedules().get(0);
        //start indicates we start counting from 0 for every patient' time list (called schedule) to have the start of the process
        int start = 0;
        //end indicated we cound from the end to have the end of patient's list of time
        int end = 799;
        while (start < schedule.length) { 
            //in case a value is null, that means there is no process, we increment
            if (schedule[start] == null) {
                start++;
            } else {
                min = start;
                break;
            }
        }
        //this loop compares patients' total process time to each other in order to get the longest one, which is the value returned
        for (int j = 0; j < listPatient.size(); j++) {
            end = 799;
            while (end > min) {
                if (listPatient.get(j).getParallelSchedules().get(0)[end] == null) {
                    end--;
                } else {
                    if (max < end) {
                        //+1 as index end =799
                        max = end + 1;
                    }
                    break;
                }
            }
        }
        mksp = max - min;
        return mksp;
    }
    


    /**
     * Method to get Resources needed for a task, called by AddTask method
     *
     * @param time is the time from which the task is supposed to start, in case resources are not available at this time, there is waiting time
     * @param t is Task currently processed
     * @return resourcesToUse is a list of Resources needed to perform a task which lasts the value "time"
     *
     * @see Skill#getFastestAvailable(int, int) 
     * @see Resource#getNextAvailableTime(int, int) 
     */
    public ArrayList<Resource> researchResources(int time, Task t) {
        int start = time;
        updateStart = time;
        ArrayList<Resource> resourcesToUse = new ArrayList();
        
        
        // processing skill by skill and look for the resources available 
        //This loop is only for the first skill needed for a task
        for (int f = 0; f < t.getListSkill().size(); f++) {
            if (f == 0) {
                Skill s = t.getListSkill().get(f);
                // call of getFastestAvailable method to pick the fastest available resource 
                int r = s.getFastestAvailable(time, t.getAvTime());
                //if r!= -1 that means there is a resource available
                if (r != -1) {
                    Resource res = s.getListResource().get(r);
                    //method to know when a resource is available for the start "tim" and the duration "t.getAvTime"
                    start = res.getNextAvailableTime(time, t.getAvTime());
                    updateStart = start;
                    resourcesToUse.add(res);
                //case there is no resource available, value "null" is add is the list "ResourceToUse" 
                } else {
                    resourcesToUse.add(null);
                }
                
            //this loop is for the next skills, it does exactyl the samme as before. But as it is needed to have all the skills 
            //available to perform the task, in case a resource is not found for a given skill, the first parameter of this current method
            //is updated, that adds waiting time, and look again for resources available. It is a recursive method.
            } else {
                Skill s = t.getListSkill().get(f);
                int r = s.getStrictestAvailable(start, t.getAvTime());
                if (r != -1) {
                    Resource res = s.getListResource().get(r);
                    resourcesToUse.add(res);
                } else {
                    r = s.getFastestAvailable(start, t.getAvTime());
                    if (r != -1) {
                        Resource res = s.getListResource().get(r);
                        int newStart = res.getNextAvailableTime(start, t.getAvTime());
                        // recursive call of this current method
                        resourcesToUse = researchResources(newStart, t); 
                        updateStart = newStart;
                        return resourcesToUse;
                    } else {
                        resourcesToUse.add(null);
                    }
                }
            }
        }
        return resourcesToUse;
    }

    //**************************************************************************************************************************************
    //**************************************************************************************************************************************
    //                              ALGORITHM FOR PATIENT APPOINTMENT AND RESOURCE ALLOCATION
    //**************************************************************************************************************************************
    //**************************************************************************************************************************************
    /**
     *
     * Method that scheduldes patient appoitment and allocate resources
     *
     * @param giveDetails is a boolean used in case it is wished or not to
     * display results in output (they are processID, PatientID, taskID,
     * Durations, starting and ending times, waiting times and resources used)
     *
     * @see #researchResources(int, Task)
     * @see #getProcess(java.lang.String)
     * @see Patient#setZeroSchedule() 
     * @see Resource#getDiagramValues()
     * @see Patient#addArrayDiagram() 
     * @see Resource#setZero()
     * @see Patient#getNextAvailableTime()
     * @see Resource#setTime(int start, int avTime, String taskID)
     * @see Patient#addParallelSchedule(String[] s)
     * @see Skill#getStrictestAvailable(int startTime, int avTime)
     * @see Patient#addDiagramValues(int i, int value)
     * @see Resource#timeToDiagramValues()
     *  
     *
     */
    public void addTask(boolean giveDetails) {

        //Empty the table of time of each patient 
        for (int p = 0; p < listPatient.size(); p++) {

            listPatient.get(p).setZeroSchedule();
            listPatient.get(p).getDiagramValues().clear();
            listPatient.get(p).addArrayDiagram();

        }

        //Empty the table of time of each resource 
        for (int r = 0; r < listResource.size(); r++) {
            listResource.get(r).setZero();
            listResource.get(r).getDiagramValues().clear();
        }
        // display for each task : the processID, the patientID, TaskID, Duration, presence of patient, starting and ending times, resources used
        if (giveDetails == true) {
            System.out.println("");
            System.out.print("Process ID");
            System.out.print("\t");
            System.out.print("Patient ID");
            System.out.print("\t");
            System.out.print("Task ID");
            System.out.print("\t");
            System.out.print("Duration");
            System.out.print("\t");
            System.out.print("Presence patient");
            System.out.print("\t");
            System.out.print("Starting Time");
            System.out.print("\t");
            System.out.print("Ending Time");
            System.out.print("\t");
            System.out.print("Waiting Time");
            System.out.print("\t");
            System.out.println("Resource used");
        }

        totalWaitingTime = 0;

        // variable used to ensure the First In First served discipline
        int prevStart = 0;

        // Algorithm starts taking a list of patient and process patient per patient
        for (int j = 0; j < listPatient.size(); j++) {
            Patient pat = listPatient.get(j);

            //variable endLast to calculate the end of a task
            int endLastTask = 0;

            //get Process of the patient
            Process process = this.getProcess(pat.getProcessID());

            //for a given task, check its operation mode, if equals to 0 --> non waiting, 1 --> waiting.
            //If 0 : tasks to schedulde together are added in TasksToSchedule list, to ensure there is no waiting times beteween tasks involved
            for (int k = 0; k < process.getListTask().size(); k++) {

                boolean waiting = false;
                //"TasksToSchedule" is a list containg tasks to schedule at the same time to avoid waiting time between the tasK. List used in case of non waiting operation mode
                ArrayList<Task> tasksToSchedule = new ArrayList();
                //ind is the index of the next tasks (of the current task) which are non waiting operation mode
                int ind = k + 1;
                while (!waiting && ind < process.getListTask().size()) {
                    int opMode = process.getListTask().get(ind).getOpMode();
                    //case operation mode equals zero --> add task in tasksToSchedule
                    if (opMode == 0) {
                        tasksToSchedule.add(process.getListTask().get(ind));
                    //case operation mode quals 1 --> no task to add in tasksToSchedule
                    } else if (opMode == 1) {
                        waiting = true;
                    }
                    ind++;
                }

                Task t = process.getListTask().get(k);
                int time = pat.getNextAvailableTime();

                //In case the cureent task is different from first one : to ensure end of task is at least different of 1 minute to the next task's starting
                //'prevTask' is to ensure the first in first served discipline is respected
                
                if (k != 0) {
                    time++;
                } else if (prevStart > time) {
                    time = prevStart;
                }

                //**********************************************************
                // CASE FOR TASK WAITING OPERATION MODE AND NO PARALLEL TASK 
                
                
                if (tasksToSchedule.size() == 0) {
                    if (time != -1 && time + t.getAvTime() < pat.getSchedule().length) {
                        int start = time;
                        // to obtain resources to use
                        ArrayList<Resource> resourcesToUse = researchResources(time, t); 
                        start = updateStart;
                        if (!resourcesToUse.contains(null)) {

                            //*****************************************************************
                            // CASE NO PARALLEL TASK
                            
                            
                            if (t.getParallelTask() == null) {
                                // String to display in output the resources used for a task
                                String displayResTask = "";
                                if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                    for (int p = 0; p < resourcesToUse.size(); p++) {
                                        //update a Resource's list of time
                                        resourcesToUse.get(p).setTime(start, t.getAvTime(), t.getTaskID());
                                        displayResTask += resourcesToUse.get(p).getResourceID() + ", ";
                                    }
                                    // update of table a patient's list of time
                                    pat.setSchedule(0, start, t.getAvTime(), t.getTaskID());
                                    // case patient involved  
                                    if (t.getPatientPresence() == 1) {

                                        if (start != 0) {
                                            // add the waiting time first in the diagram
                                            pat.addDiagramValues(0, (start - endLastTask) - 1);
                                            //  then add the duration
                                            pat.addDiagramValues(0, t.getAvTime());

                                            // value zero added for waiting time, in case the patient starts at time =0
                                            // Because In Excel writer first column is "Waiting" and then "Duration"
                                        } else {

                                            // add the waiting time first
                                            pat.addDiagramValues(0, 0);
                                            //  then add the duration
                                            pat.addDiagramValues(0, t.getAvTime());
                                        }
                                        //case patient NOT involved
                                    } else {
                                        if (start != 0) {
                                            // add the waiting time first
                                            pat.addDiagramValues(0, (start - endLastTask) - 1 + t.getAvTime());
                                            //  then add the duration
                                            pat.addDiagramValues(0, 0);

                                            // value zero added for waiting time, in case the patient starts at time =0
                                            // Because In Excel writer first column is "Waiting" and then "Duration"
                                        } else {

                                            // add the waiting time first
                                            pat.addDiagramValues(0, t.getAvTime());
                                            //  then add the duration
                                            pat.addDiagramValues(0, 0);
                                        }
                                    }
                                }

                                // before the first time, it's not waiting time, exculded in totalwaitingTime sum
                                if (k != 0) {
                                    totalWaitingTime += (start - endLastTask);
                                }

                                //displays values of a task :duration,  starting end endings times, waiting time. Patient ID, process ID and resources used
                                if (giveDetails == true) {
                                    System.out.print(process.getID());
                                    System.out.print("\t\t");
                                    System.out.print(pat.getPatientID());
                                    System.out.print("\t\t");
                                    System.out.print(t.getTaskID());
                                    System.out.print("\t\t");
                                    System.out.print((t.getAvTime() + start) - start + "");
                                    System.out.print("\t\t");
                                    System.out.print(t.getPatientPresence());
                                    System.out.print("\t\t");
                                    System.out.print(start + "");
                                    System.out.print("\t\t");
                                    System.out.print(t.getAvTime() + start + "");
                                    System.out.print("\t\t");
                                    System.out.print(start - endLastTask + "");
                                    System.out.print("\t\t");
                                    System.out.println(displayResTask);
                                }
                                // calculates ends of the current task
                                endLastTask = start + t.getAvTime();

                                //*****************************************************************
                                // CASE PARALLEL TASK
                                
                            } else {

                                Task pT = t.getParallelTask();
                                for (int m = 0; m < pT.getListSkill().size(); m++) {
                                    Skill s = pT.getListSkill().get(m);
                                    int re = s.getStrictestAvailable(start, t.getAvTime());
                                    if (re != -1) {
                                        Resource res = s.getListResource().get(re);
                                        resourcesToUse.add(res);
                                    } else {
                                        // if there is null in list ResourceToUse, not possible to scedule a task
                                        resourcesToUse.add(null);
                                    }
                                }
                                //case there resources needed to schedule a task
                                if (!resourcesToUse.contains(null)) {
                                    //this string is to display resources used in the output
                                    String displayResTask = "";
                                    String displayResParaTask = "";
                                    if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                        //loop to allocate resources from resourceToUse list
                                        for (int p = 0; p < resourcesToUse.size(); p++) {
                                            if (p <= t.getListSkill().size() - 1) {
                                                resourcesToUse.get(p).setTime(start, t.getAvTime(), t.getTaskID());
                                                displayResTask += resourcesToUse.get(p).getResourceID() + ", ";
                                            } else {
                                                resourcesToUse.get(p).setTime(start, pT.getAvTime(), pT.getTaskID());
                                                displayResParaTask += resourcesToUse.get(p).getResourceID() + ", ";
                                            }
                                        }
                                        // 0 = index of first table schedule in parallelSchedules
                                        pat.setSchedule(0, start, t.getAvTime(), t.getTaskID());

                                        // patient presence test
                                        if (t.getPatientPresence() == 1) {
                                            //add the waiting time first
                                            pat.addDiagramValues(0, start - endLastTask);
                                            // then add the duration
                                            pat.addDiagramValues(0, t.getAvTime());

                                            //patient not present
                                        } else {
                                            //add the waiting time first
                                            pat.addDiagramValues(0, start - endLastTask + t.getAvTime());
                                            // then add the duration
                                            pat.addDiagramValues(0, 0);
                                        }

                                        String[] schedule = new String[800];
                                        pat.addParallelSchedule(schedule);
                                        // 1 = index of 2e tableau schedule in parallelSchedules
                                        pat.setSchedule(1, start, pT.getAvTime(), pT.getTaskID());

                                        // patient presence test
                                        if (pT.getPatientPresence() == 1) {

                                            pat.addArrayDiagram();
                                            //add the waiting time first
                                            pat.addDiagramValues(1, start);
                                            // then add the duration
                                            pat.addDiagramValues(1, pT.getAvTime());

                                        } else { //patient not present
                                            pat.addArrayDiagram();
                                            //add the waiting time first
                                            pat.addDiagramValues(1, start + pT.getAvTime());
                                            // then add the duration
                                            pat.addDiagramValues(1, 0);
                                        }
                                        k++;
                                    }

                                    if (k != 0) {
                                        totalWaitingTime += (start - endLastTask);
                                    }

                                    if (giveDetails == true) {
                                        System.out.print(process.getID());
                                        System.out.print("\t\t");
                                        System.out.print(pat.getPatientID());
                                        System.out.print("\t\t");
                                        System.out.print(t.getTaskID());
                                        System.out.print("\t\t");
                                        System.out.print((t.getAvTime() + start) - start + "");
                                        System.out.print("\t\t");
                                        System.out.print(t.getPatientPresence());
                                        System.out.print("\t\t");
                                        System.out.print(start + "");
                                        System.out.print("\t\t");
                                        System.out.print(t.getAvTime() + start + "");
                                        System.out.print("\t\t");
                                        System.out.print(start - endLastTask + "");
                                        System.out.print("\t\t");
                                        System.out.println(displayResTask);

                                        //parallel task values
                                        System.out.print(process.getID());
                                        System.out.print("\t\t");
                                        System.out.print(pat.getPatientID());
                                        System.out.print("\t\t");
                                        System.out.print(pT.getTaskID());
                                        System.out.print("\t\t");
                                        System.out.print((pT.getAvTime() + start) - start + "");
                                        System.out.print("\t\t");
                                        System.out.print(pT.getPatientPresence());
                                        System.out.print("\t\t");
                                        System.out.print(start + "");
                                        System.out.print("\t\t");
                                        System.out.print(pT.getAvTime() + start + "");
                                        System.out.print("\t\t");
                                        System.out.print(start - endLastTask + "");
                                        System.out.print("\t\t");
                                        System.out.println(displayResParaTask);
                                    }
                                    int avTime = Math.max(t.getAvTime(), pT.getAvTime());
                                    // calculates ends of the current task
                                    endLastTask = start + avTime;

                                } else {
                                    throw new IllegalArgumentException("Not enough ressources to create a full schedule");
                                }

                            } //parallelism ****** end *********

                        } else {
                            throw new IllegalArgumentException("Not enough ressources to create a full schedule");
                        }
                    }
                } else {

                    //**********************************************************
                    // CASE FOR NON WAITING TASK
                    
                    tasksToSchedule.add(0, t);
                    int avTimeTotal = 0;
                    int start = time;
                    for (int iv = 0; iv < tasksToSchedule.size(); iv++) {
                        avTimeTotal += tasksToSchedule.get(iv).getAvTime();
                    }
                    if (time != -1 && time + avTimeTotal < pat.getSchedule().length) {
                        ArrayList<Resource> resourcesToUse = researchResources(time, t);
                        start = updateStart;
                        ArrayList<String> tasksResourceToUse = new ArrayList();
                        ArrayList<Integer> durationResourceToUse = new ArrayList();

                        if (!resourcesToUse.contains(null)) {

                            for (int v = 0; v < resourcesToUse.size(); v++) {
                                tasksResourceToUse.add(t.getTaskID());
                                durationResourceToUse.add(t.getAvTime());
                            }

                            int currentStart = start + t.getAvTime() + 1;
                            for (int iz = 1; iz < tasksToSchedule.size(); iz++) {
                                for (int v = 0; v < tasksToSchedule.get(iz).getListSkill().size(); v++) {
                                    Skill sk = tasksToSchedule.get(iz).getListSkill().get(v);
                                    int re = sk.getStrictestAvailable(currentStart, tasksToSchedule.get(iz).getAvTime());
                                    if (re != -1) {
                                        resourcesToUse.add(sk.getListResource().get(re));
                                        tasksResourceToUse.add(tasksToSchedule.get(iz).getTaskID());
                                        durationResourceToUse.add(tasksToSchedule.get(iz).getAvTime());
                                    } else {
                                        resourcesToUse.add(null);
                                        tasksResourceToUse.add(null);
                                        durationResourceToUse.add(0);

                                    }
                                }
                                currentStart += tasksToSchedule.get(iz).getAvTime() + 1;
                            }
                            if (!resourcesToUse.contains(null)) {

                                currentStart = start;
                                ArrayList<String> displayRes = new ArrayList<>();
                                String prevTask = t.getTaskID();
                                String resourceId = "";
                                for (int ip = 0; ip < resourcesToUse.size(); ip++) {

                                    String taskID = tasksResourceToUse.get(ip);
                                    int currentAvTime = durationResourceToUse.get(ip);
                                    resourcesToUse.get(ip).setTime(currentStart, currentAvTime, taskID);

                                    if (prevTask.equals(taskID)) {
                                        resourceId += resourcesToUse.get(ip).getResourceID() + ", ";
                                        if (ip == resourcesToUse.size() - 1) {
                                            displayRes.add(resourceId);
                                        }

                                    } else {
                                        displayRes.add(resourceId);
                                        resourceId = resourcesToUse.get(ip).getResourceID() + ", ";
                                        currentStart += currentAvTime + 1;
                                        if (ip == resourcesToUse.size() - 1) {
                                            displayRes.add(resourceId);
                                        }
                                    }

                                    prevTask = taskID;
                                }
                                currentStart = start;
                                int currentEnd = endLastTask;
                                for (int x = 0; x < tasksToSchedule.size(); x++) {

                                    String taskID = tasksToSchedule.get(x).getTaskID();
                                    int currentAvTime = tasksToSchedule.get(x).getAvTime();

                                    pat.setSchedule(0, currentStart, currentAvTime, taskID);
                                    if (tasksToSchedule.get(x).getPatientPresence() == 1) {

                                        if (x == 0) {
                                            pat.addDiagramValues(0, currentStart - endLastTask);
                                            pat.addDiagramValues(0, currentAvTime);
                                        } else {
                                            pat.addDiagramValues(0, 0); 
                                            pat.addDiagramValues(0, currentAvTime);
                                        }
                                    } else {
                                        if (x == 0) {
                                            pat.addDiagramValues(0, (currentStart - endLastTask) + currentAvTime);
                                            pat.addDiagramValues(0, 0);
                                        } else {
                                            pat.addDiagramValues(0, currentAvTime);
                                            pat.addDiagramValues(0, 0);
                                        }
                                    }

                                    if (giveDetails == true) {
                                        System.out.print(process.getID());
                                        System.out.print("\t\t");
                                        System.out.print(pat.getPatientID());
                                        System.out.print("\t\t");
                                        System.out.print(taskID);
                                        System.out.print("\t\t");
                                        System.out.print((currentStart + currentAvTime) - currentStart + "");
                                        System.out.print("\t\t");
                                        System.out.print(tasksToSchedule.get(x).getPatientPresence());
                                        System.out.print("\t\t");
                                        System.out.print(currentStart + "");
                                        System.out.print("\t\t");
                                        System.out.print(currentStart + currentAvTime + "");
                                        System.out.print("\t\t");
                                        System.out.print(currentStart - currentEnd + "");
                                        System.out.print("\t\t");
                                        System.out.println(displayRes.get(x));
                                    }

                                    currentEnd = currentStart + currentAvTime;
                                    currentStart += currentAvTime + 1;
                                }

                                k += tasksToSchedule.size() - 1;

                                if (k != 0) {
                                    totalWaitingTime += (start - endLastTask);
                                }
                                endLastTask = start + avTimeTotal;

                            } else {

                                throw new IllegalArgumentException("Not enough ressources to create a full schedule");
                            }

                        } else {
                            throw new IllegalArgumentException("Not enough ressources to create a full schedule");
                        }
                    }
                }
                //case when it's first task, ensure first in first served discipline. A patient can't an appointment before the previous patient  
                if (k == 0) {
                    prevStart = updateStart;
                }
            }
        }
        // from table of time of resources, generation of DiagramValues for excel writer
        for (int q = 0; q < listResource.size(); q++) {
            listResource.get(q).timeToDiagramValues();
        }
    }
}
