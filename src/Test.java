/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package Scheduling_First_Try;
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
                if (listPatient.get(j).getParallelSchedules().get(0)[end] == null) {
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
        String[] schedule = listPatient.get(0).getParallelSchedules().get(0);
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
                if (listPatient.get(j).getParallelSchedules().get(0)[end] == null) {
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

    public void addTask(boolean giveDetails) {

        //Empty the table of time of each patient 
        for (int p = 0; p < listPatient.size(); p++) {

            listPatient.get(p).setZeroSchedule();
            listPatient.get(p).getDiagramValues().clear();
            listPatient.get(p).addArrayDiagram(0);

        }

        //Empty the table of time of each resource 
        for (int r = 0; r < listResource.size(); r++) {
            listResource.get(r).setZero();
            listResource.get(r).getDiagramValues().clear();
        }

        if (giveDetails == true) {
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
            System.out.print("Waiting Time");
            System.out.print("\t");
            System.out.println("Resource used");
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
                        int start = time;
                        ArrayList<Resource> resourcesToUse = new ArrayList();
                        for (int f = 0; f < t.getListSkill().size(); f++) {
                            if (f == 0) {
                                Skill s = t.getListSkill().get(f);
                                int r = s.getFastestAvailable(time, t.getAvTime());
                                if (r != -1) {
                                    Resource res = s.getListResource().get(r);
                                    start = res.getNextAvailableTime(time, t.getAvTime());
                                    resourcesToUse.add(res);
                                } else {
                                    resourcesToUse.add(null);
                                }
                            } else {
                                Skill s = t.getListSkill().get(f);
                                int r = s.getStrictestAvailable(start, t.getAvTime());
                                if (r != -1) {
                                    Resource res = s.getListResource().get(r);
                                    resourcesToUse.add(res);
                                } else {
                                    resourcesToUse.add(null);
                                }
                            }
                        }
                        if (!resourcesToUse.contains(null)) {
                            if (t.getParallelTask() == null) {
                                String displayResTask = "";
                                if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                    for (int p = 0; p < resourcesToUse.size(); p++) {
                                        resourcesToUse.get(p).setTime(start, t.getAvTime(), t.getTaskID());
                                        displayResTask += resourcesToUse.get(p).getResourceID() + ", ";
                                    }
                                    pat.setSchedule(0, start, t.getAvTime(), t.getTaskID());
                                    pat.addDiagramValues(0, start - endLastTask); //add the waiting time first
                                    pat.addDiagramValues(0, t.getAvTime()); // then add the duration
//                               
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
                                    System.out.print(start + "");
                                    System.out.print("\t\t");
                                    System.out.print(t.getAvTime() + start + "");
                                    System.out.print("\t\t");
                                    System.out.print(start - endLastTask + "");
                                    System.out.print("\t\t");
                                    System.out.println(displayResTask);
                                }
                                endLastTask = start + t.getAvTime();

                            } else { //parallelism ****** start *********

                                Task pT = t.getParallelTask();
                                for (int m = 0; m < pT.getListSkill().size(); m++) {
                                    Skill s = pT.getListSkill().get(m);
                                    int re = s.getStrictestAvailable(start, t.getAvTime());
                                    if (re != -1) {
                                        Resource res = s.getListResource().get(re);
                                        resourcesToUse.add(res);
                                    } else {
                                        resourcesToUse.add(null);
                                    }
                                }
                                if (!resourcesToUse.contains(null)) {
                                    String displayResTask = "";
                                    String displayResParaTask = "";
                                    if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {

                                        for (int p = 0; p < resourcesToUse.size(); p++) {
                                            if (p <= t.getListSkill().size() - 1) {
                                                resourcesToUse.get(p).setTime(start, t.getAvTime(), t.getTaskID());
                                                displayResTask += resourcesToUse.get(p).getResourceID() + ", ";
                                            } else {
                                                resourcesToUse.get(p).setTime(start, pT.getAvTime(), pT.getTaskID());
                                                displayResParaTask += resourcesToUse.get(p).getResourceID() + ", ";
                                            }
                                        }

                                        pat.setSchedule(0, start, t.getAvTime(), t.getTaskID()); // 0 = indice du 1er tableau schedule dans parallelSchedules
                                        String[] schedule = new String[800];
                                        pat.addParallelSchedule(schedule);
                                        pat.setSchedule(1, start, pT.getAvTime(), pT.getTaskID());// 1 = indice du 2e tableau schedule dans parallelSchedules

                                        pat.addDiagramValues(0, start - endLastTask); //add the waiting time first
                                        pat.addDiagramValues(0, t.getAvTime()); // then add the duration

                                        pat.addArrayDiagram(1);
                                        pat.addDiagramValues(1, start); //add the waiting time first
                                        pat.addDiagramValues(1, pT.getAvTime()); // then add the duration

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
                                        System.out.print(start + "");
                                        System.out.print("\t\t");
                                        System.out.print(pT.getAvTime() + start + "");
                                        System.out.print("\t\t");
                                        System.out.print(start - endLastTask + "");
                                        System.out.print("\t\t");
                                        System.out.println(displayResParaTask);
                                    }
                                    int avTime = Math.max(t.getAvTime(), pT.getAvTime());
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

                    tasksToSchedule.add(0, t);
                    int avTimeTotal = 0;
                    int start = time;
                    for (int iv = 0; iv < tasksToSchedule.size(); iv++) {
                        avTimeTotal += tasksToSchedule.get(iv).getAvTime();
                    }
                    if (time != -1 && time + avTimeTotal < pat.getSchedule().length) {
                        ArrayList<Resource> resourcesToUse = new ArrayList();
                        ArrayList<String> tasksResourceToUse = new ArrayList();
                        ArrayList<Integer> durationResourceToUse = new ArrayList();
                        for (int d = 0; d < t.getListSkill().size(); d++) {
                            if (d == 0) {
                                Skill s = t.getListSkill().get(d);
                                int r = s.getFastestAvailable(time, t.getAvTime());
                                if (r != -1) {
                                    Resource res = s.getListResource().get(r);
                                    start = res.getNextAvailableTime(time, t.getAvTime());
                                    resourcesToUse.add(res);
                                    tasksResourceToUse.add(t.getTaskID());
                                    durationResourceToUse.add(t.getAvTime());
                                } else {
                                    resourcesToUse.add(null);
                                    tasksResourceToUse.add(null);
                                    durationResourceToUse.add(0);
                                }
                            } else {
                                Skill s = t.getListSkill().get(d);
                                int r = s.getStrictestAvailable(start, t.getAvTime());
                                if (r != -1) {
                                    Resource res = s.getListResource().get(r);
                                    resourcesToUse.add(res);
                                    tasksResourceToUse.add(t.getTaskID());
                                    durationResourceToUse.add(t.getAvTime());
                                } else {
                                    resourcesToUse.add(null);
                                    tasksResourceToUse.add(null);
                                    durationResourceToUse.add(0);
                                }
                            }
                        }
                        if (!resourcesToUse.contains(null)) {
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
                                        if (ip == resourcesToUse.size() - 1) {
                                            displayRes.add(resourceId);
                                        }
                                    }

                                    currentStart += currentAvTime + 1;
                                    prevTask = taskID;
                                }
                                currentStart = start;
                                int currentEnd = endLastTask;
                                for (int x = 0; x < tasksToSchedule.size(); x++) {

                                    String taskID = tasksToSchedule.get(x).getTaskID();
                                    int currentAvTime = tasksToSchedule.get(x).getAvTime();

                                    pat.setSchedule(0, currentStart, currentAvTime, taskID);
                                    if (x == 0) {
                                        pat.addDiagramValues(0, currentStart - endLastTask);
                                        pat.addDiagramValues(0, currentAvTime);
                                    } else {
                                        pat.addDiagramValues(0, 0); // NON WAITING
                                        pat.addDiagramValues(0, currentAvTime);
                                    }

                                    if (giveDetails == true) {
                                        System.out.print(process.getID());
                                        System.out.print("\t\t");
                                        System.out.print(pat.getPatientID());
                                        System.out.print("\t\t");
                                        System.out.print(taskID);
                                        System.out.print("\t\t");
                                        System.out.print(currentStart + "");
                                        System.out.print("\t\t");
                                        System.out.print(currentStart + currentAvTime + "");
                                        System.out.print("\t\t");
                                        System.out.print(currentStart - currentEnd + "");
                                        System.out.print("\t\t");
                                         System.out.println(displayRes.get(x));
                                    }
                                    //order is important
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

            }

        }
        for (int q = 0; q < listResource.size(); q++) {
            listResource.get(q).timeToDiagramValues();
        }
    }
}
