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
//    int makespan;
    int lateness;

    public Test(List<Patient> sequence, Schedule s) {
        listPatient = sequence;
        listResource = new ArrayList();

        List<Resource> listResource = new ArrayList<>();
        totalWaitingTime = 0;
        lateness = 0;
//        makespan = 0;

        listProcess = s.getListProcess(); 
        for(int l=0; l<listProcess.size();l++){
            Process pl = s.getListProcess().get(l);
            for (int length= 0 ; length<pl.getListResource().size();length++){
                if (!this.listResource.contains(pl.getListResource().get(length))){
                this.listResource.add(pl.getListResource().get(length));
                }
            }
            
        }
        
        //  System.out.println(s.getListProcess().toString());

    }

//    public int getMakespan() {
//        makespan = this.calculateMakespan();
//        return makespan;
//    }
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

    public boolean addResourceInList(Resource r) {
        int i = 0;
        boolean isPresent = false;
        while (!isPresent && i < listResource.size()) {
            if (!r.getResourceID().equals(listResource.get(i).getResourceID())) {
                i++;
            } else {
                isPresent = true;
            }
        }
        return isPresent;
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
        //Empty the schedule of each patient to make sure tasks don't add the end of their schedule
        for (int p = 0; p < listPatient.size(); p++) {
            listPatient.get(p).setSchedule();
        }
        for (int p = 0; p < listResource.size(); p++) {
            listResource.get(p).setZero();
        }
        totalWaitingTime = 0;

        //
        for (int j = 0; j < listPatient.size(); j++) {
            Patient pat = listPatient.get(j);
            int endLastTask = 0;
            Process process = this.getProcess(pat.getProcessID());
            for (int k = 0; k < process.getListTask().size(); k++) {
                Task t = process.getListTask().get(k);
                int time = pat.getNextAvailableTime();
                int opMode = t.getOpMode();

                switch (opMode) {
                    case 0:

                        if (time != -1 && time + t.getAvTime() < pat.getSchedule().length) {
                            Skill s = t.getSkill();
                            int r = s.getFastestAvailable(time, t.getAvTime());
                            if (r != -1) {
                                Resource res = t.getSkill().getListResource().get(r);
                                int start = res.getNextAvailableTime(time, t.getAvTime());
                                if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                    res.setTime(start, t.getAvTime(), t.getTaskID());
                                    if (addResourceInList(res) == false) {
                                        listResource.add(res);
                                    }
                                    pat.setSchedule(start, t.getAvTime(), t.getTaskID());

                                    totalWaitingTime += (start - endLastTask);
                                    endLastTask = start + t.getAvTime();

                                }

                            }

                        }
                        break;

                    case 1:

                        if (time != -1 && time + t.getAvTime() < pat.getSchedule().length) {
                            Skill s = t.getSkill();
                            int i = 0;
                            boolean found = false;
                            List<Resource> lr = s.getListResource();
                            int start = lr.get(i).getNextAvailableTime(time, t.getAvTime());
                            while (!found && i < lr.size()) {
                                if (start != -1 && start + t.getAvTime() < pat.getSchedule().length) {
                                    found = true;
                                    lr.get(i).setTime(start, t.getAvTime(), t.getTaskID());
                                    if (addResourceInList(lr.get(i)) == false) {
                                        listResource.add(lr.get(i));
                                    }
                                    pat.setSchedule(start, t.getAvTime(), t.getTaskID());

                                    totalWaitingTime += (start - endLastTask);
                                    endLastTask = start + t.getAvTime();
                                } else {
                                    i++;
                                }
                            }

                        }
                        break;

                }
            }

        }

    }

}
