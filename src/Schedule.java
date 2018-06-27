/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hayat
 */
public class Schedule {

    ResultSet rs;
    String driver = "com.mysql.jdbc.Driver";
    Statement st;
    Connection conn;

    private List<Process> listProcess;

    public Schedule(List<Patient> listPatient) {
        List<String> listP = this.getProcess(listPatient);
        List<Process> listPro = new ArrayList();
        getConnectDB(); // connect to DataBAase
        for (int i = 0; i < listP.size(); i++) {
            String namePro = listP.get(i);
            List<Resource> allResources = new ArrayList<Resource>();
            List<Task> listTask = new ArrayList();
            listTask = this.getTaskData(namePro);
            this.getSkillData(listTask);
            allResources = this.dataAllResources(namePro);
            this.getResourceData(listTask, allResources);

            Process pro = new Process(namePro, listTask, allResources);
            listPro.add(pro);
        }
        this.listProcess = listPro;

    }

    public List<String> getProcess(List<Patient> listPatient) {
        List<String> idProcess = new ArrayList();

        for (int k = 0; k < listPatient.size(); k++) {

            if (!idProcess.contains(listPatient.get(k).getProcessID())) {
                idProcess.add(listPatient.get(k).getProcessID());

            }

        }
        return idProcess;
    }

    public List<Process> getListProcess() {
        return listProcess;
    }

    public void getConnectDB() {
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection("jdbc:mysql://mysql-healthview.alwaysdata.net/healthview_test", "152416_sir", "projetsir2018");

            st = conn.createStatement();
//            System.out.println("You are connected ! ");

        } catch (Exception ex) {
            System.out.println("Error : " + ex);

        }
    }

    public List<Task> getTaskData(String processID) {
        List<Task> listTask = new ArrayList<Task>();
        try {
            String query = "SELECT * FROM Task WHERE ProcessID ='" + processID + "'";
            rs = st.executeQuery(query);

            while (rs.next()) {
                String process_id = rs.getString("ProcessID");
                String task_id = rs.getString("TaskID");

                int opMode = rs.getInt("OpMode");
                int avTime = rs.getInt("AvTime");
                int stdDev = rs.getInt("StdDev");
                int maxWait = rs.getInt("MaxWait");

                Task task = new Task(process_id, task_id, opMode, avTime, stdDev, maxWait);
                listTask.add(task);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return listTask;
    }

    public void getSkillData(List<Task> listTask) {

        for (int i = 0; i < listTask.size(); i++) {
            try {

                String query = "SELECT SkillID, Description, PrevTask FROM Skill NATURAL JOIN TaskSkill JOIN Task ON TaskSkill.IDcouple=Task.ID WHERE TaskID = '" + listTask.get(i).getTaskID() + "'";
                rs = st.executeQuery(query);
                while (rs.next()) {

                    String skillID = rs.getString("SkillID");
                    String description = rs.getString("Description");
                    String prevTask = rs.getString("PrevTask");
                    listTask.get(i).setSkill(new Skill(skillID, description, prevTask));

                }

            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    public void getResourceData(List<Task> listTask, List<Resource> allResources) {

        try {
            for (int i = 0; i < listTask.size(); i++) {
                String query = "SELECT ResourceID, Capacity, Name FROM Resource NATURAL JOIN ResourceSkill JOIN Task ON Task.ID = ResourceSkill.IDcouple WHERE TaskID = '" + listTask.get(i).getTaskID() + "' AND SkillID ='" + listTask.get(i).getSkill().getSkillID() + "'";
                rs = st.executeQuery(query);
                while (rs.next()) {
                    String resourceID = rs.getString("ResourceID");
                    int k = 0;
                    boolean found = false;
                    while (k < allResources.size() && !found) {

                        if (resourceID.equals(allResources.get(k).getResourceID())) {
                            found = true;
                            listTask.get(i).getSkill().getListResource().add(allResources.get(k));

                        }
                        k++;

                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);

        }
    }

    public List<Resource> dataAllResources(String processID) {
        List<Resource> allResources = new ArrayList<Resource>();
        try {
            String query = "SELECT DISTINCT ResourceID, Name, Capacity FROM Resource NATURAL JOIN ResourceSkill JOIN Task ON Task.ID = ResourceSkill.IDcouple WHERE Task.ProcessID ='" + processID + "'";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String resourceID = rs.getString("ResourceID");
                int capacity = rs.getInt("Capacity");
                String name = rs.getString("Name");
                Resource res = new Resource(resourceID, capacity, name);
                allResources.add(res);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return allResources;
    }

//    public void getPrevTask() {
//        try {
//            String query = "SELECT TaskID, PrevTaskID FROM PreviousTask JOIN Task ON Task.ID = PreviousTask.IDcouple WHERE Task.ProcessID ='" + processID + "'";
//            rs = st.executeQuery(query);
//            while (rs.next()) {
//
//                String prevTask = rs.getString("PrevTaskID");
//                String taskID = rs.getString("TaskID");
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).getTaskID().equals(taskID)) {
//                        listTask.get(i).addPrevTask(prevTask);
//                    }
//                }
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//    }
//
//    public void getNextTask() {
//        try {
//            String query = "SELECT TaskID, NextTaskID FROM NextTask JOIN Task ON Task.ID = NextTask.IDcouple WHERE Task.ProcessID ='" + processID + "'";
//            rs = st.executeQuery(query);
//            while (rs.next()) {
//                String nextTask = rs.getString("NextTaskID");
//                String taskID = rs.getString("TaskID");
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).getTaskID().equals(taskID)) {
//                        listTask.get(i).addNextTask(nextTask);
//                    }
//                }
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//    }
//
//    public Statement getStatement() {
//        return st;
//    }
//
//    public ResultSet getResultSet() {
//        return rs;
//    }

