/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hayat
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    List<Patient> listPatient;
    List<Process> listProcess;
    List<Resource> listResource;
    int totalWaitingTime;
    int lateness;

    public Test(List<Patient> sequence, Schedule s) {
        listPatient = sequence;
        listResource = s.getAllResources();
//        List<Resource> listResource = new ArrayList();
        totalWaitingTime = 0;
        lateness = 0;

        listProcess = s.getListProcess();
//        for(int l=0; l<listProcess.size();l++){
//            Process pl = s.getListProcess().get(l);
//            for (int length= 0 ; length<pl.getListResource().size();length++){
//                if (!this.listResource.contains(pl.getListResource().get(length))){
//                this.listResource.add(pl.getListResource().get(length));
//                }
//            }
//            
//        }

    }

    public int getLateness() {
        lateness = this.lateness();
        return lateness;
    }

    public int getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public List<Resource> getListResource() {
        return listResource;
    }

    public List<Process> getListProcess() {
        return listProcess;
    }

    public Process getProcess(String processID) {

        Process p = null;
        boolean found = false;
        int i = 0;
        while ((i < listProcess.size()) && (!found)) {
            if (listProcess.get(i).getID().equals(processID)) {
                p = listProcess.get(i);
                found = true;
            } else {
                i++;

            }
        }
//        for (int i = 0; i < listProcess.size(); i++) {
//            if (!listProcess.get(i).getID().equals(processID)) {
//                i++;
//            } else {
//                p = listProcess.get(i);
//            }
//        }

        return p;
    }

    public int lateness() {

        int maxLateness = 0;
        for (int j = 0; j < listPatient.size(); j++) {
            int end = 799;
            while (end >= 0) {
                if (listPatient.get(j).getSchedule()[end] == null) {
                    end--;
                } else {
                    if (maxLateness < end) {
                        maxLateness = end + 1;
                    }
                    break;
                }
            }
        }
//        for (int j = 0; j < listResource.size(); j++) {
//            int end = 799;
//            while (end >= 0) {
//                if (listResource.get(j).getTime()[end] == null) {
//                    end--;
//                } else {
//                    if (maxLateness < end) {
//                        maxLateness = end;
//                    }
//                    break;
//                }
//            }
//        }

        return maxLateness - 480;
    }

    public int calculateMakespan() {
        int max = 0;
        int min = 0;
        int mksp;
        String[] schedule = listPatient.get(0).getSchedule();
        int start = 0;
        int end = 799;
        while (start < schedule.length) {
            if (schedule[start] == null) {
                start++;
            } else {
                min = start;
                break;
            }
        }
        for (int j = 0; j < listPatient.size(); j++) {
            end = 799;
            while (end > min) {
                if (listPatient.get(j).getSchedule()[end] == null) {
                    end--;
                } else {
                    if (max < end) {
                        max = end + 1;
                    }
                    break;
                }
            }
        }
        mksp = max - min;
        return mksp;
    }

    public void addTask() {

        System.out.println("");
        System.out.print("Process ID");
        System.out.print("\t");
        System.out.print("Patient ID");
        System.out.print("\t");
        System.out.print("Task ID");
        System.out.print("\t");
        System.out.print("Starting Time");
        System.out.print("\t");
        System.out.print("Ending Time");
        System.out.print("\t");
        System.out.println("Waiting Time");

        //Empty the table of time of each patient 
        for (int p = 0; p < listPatient.size(); p++) {
            listPatient.get(p).resetSchedule(); // change this method's name
        }

        //Empty the table of time of each resource 
        for (int r = 0; r < listResource.size(); r++) {
            listResource.get(r).setZero();
        }
        totalWaitingTime = 0;

        for (int j = 0; j < listPatient.size(); j++) {
            Patient pat = listPatient.get(j);
            int endLastTask = 0;
            Process process = this.getProcess(pat.getProcessID());
            for (int k = 0; k < process.getListTask().size(); k++) {

                boolean waiting = false;
                ArrayList<Task> tasksToSchedule = new ArrayList();
                int ind = k + 1;
                while (!waiting && ind < process.getListTask().size()) {
                    int opMode = process.getListTask().get(ind).getOpMode();
                    if (opMode == 0) {
                        tasksToSchedule.add(process.getListTask().get(ind));
                    } else if (opMode == 1) {
                        waiting = true;
                    }
                    ind++;
                }

                Task t = process.getListTask().get(k);
                int time = pat.getNextAvailableTime();
                if (tasksToSchedule.size() == 0) {

                    if (time != -1 && time + t.getAvTime() < pat.getSchedule().length) {
                        Skill s = t.getSkill();
                        int r = s.getFastestAvailable(time, t.getAvTime());
                        if (r != -1) {
                            Resource res = t.getSkill().getListResource().get(r);
                            int start = res.getNextAvailableTime(time, t.getAvTime());
                            if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                res.setTime(start, t.getAvTime(), t.getTaskID());

                                pat.setSchedule(start, t.getAvTime(), t.getTaskID());
                                totalWaitingTime += (start - endLastTask);

                                System.out.print(process.getID());
                                System.out.print("\t\t");
                                System.out.print(pat.getPatientID());
                                System.out.print("\t\t");
                                System.out.print(t.getTaskID());
                                System.out.print("\t\t");
                                System.out.print(start + "");
                                System.out.print("\t\t");
                                System.out.print(t.getAvTime() + start + "");
                                System.out.print("\t\t");
                                System.out.print(start - endLastTask + "");
                                System.out.println("");

                                endLastTask = start + t.getAvTime();

                            }

                        }
                        else{
                            System.out.println("Not resource available for patient "+ pat.getPatientID() + ". Moving to the next List order");
                            return; // to get out the addTask Method
                        }

                    }
                } else {

                    tasksToSchedule.add(0, t);
                    int avTimeTotal = 0;
                    for (int iv = 0; iv < tasksToSchedule.size(); iv++) {
                        avTimeTotal += tasksToSchedule.get(iv).getAvTime();
                    }
                    if (time != -1 && time + avTimeTotal < pat.getSchedule().length) {
                        Skill s = t.getSkill();
                        int r = s.getFastestAvailable(time, t.getAvTime());
                        if (r != -1) {
                            Resource res = t.getSkill().getListResource().get(r);
                            int start = res.getNextAvailableTime(time, t.getAvTime());
                            ArrayList<Resource> resourcesToUse = new ArrayList();
                            resourcesToUse.add(res);
                            int currentStart = start;
                            for (int iz = 1; iz < tasksToSchedule.size(); iz++) {
                                Skill sk = tasksToSchedule.get(iz).getSkill();
                                int re = sk.getStrictestAvailable(currentStart + t.getAvTime() + 1, tasksToSchedule.get(iz).getAvTime());
                                if (re != -1) {
                                    resourcesToUse.add(sk.getListResource().get(re));
                                } else {
                                    resourcesToUse.add(null);
                                }
                                currentStart += tasksToSchedule.get(iz).getAvTime() +1;
                            }
                            if (!resourcesToUse.contains(null)) {
                                currentStart = start;
                                
                                int currentEnd = endLastTask;
                                
                                for (int ip = 0; ip < resourcesToUse.size(); ip++) {
                                    //order is important
                                    int currentAvTime = tasksToSchedule.get(ip).getAvTime();
                                    String taskID = tasksToSchedule.get(ip).getTaskID();
                                    resourcesToUse.get(ip).setTime(currentStart, currentAvTime, taskID);
                                    pat.setSchedule(currentStart, currentAvTime, taskID);
                                    
                                    System.out.print(process.getID());
                                    System.out.print("\t\t");
                                    System.out.print(pat.getPatientID());
                                    System.out.print("\t\t");
                                    System.out.print(taskID);
                                    System.out.print("\t\t");
                                    System.out.print(currentStart + "");
                                    System.out.print("\t\t");
                                    System.out.print(currentAvTime + currentStart + "");
                                    System.out.print("\t\t");
                                    System.out.print(currentStart - currentEnd  + "");
                                    System.out.println("");
                                    
                                    currentEnd = currentStart + currentAvTime;
                                    currentStart += tasksToSchedule.get(ip).getAvTime() + 1;

                                }
                                k += tasksToSchedule.size() - 1;
                                totalWaitingTime += (start - endLastTask);
                                endLastTask = start + avTimeTotal;

                            }
                            else {
                            System.out.println("Not resource available for patient "+ pat.getPatientID() + ". Moving to the next List order");
                            return; // to get out the addTask Method
                            }
                        }
                        else{
                            System.out.println("Not resource available for patient "+ pat.getPatientID() + ". Moving to the next List order");
                            return; // to get out the addTask Method
                        }
                    }

                }

            }

        }
    }

}
