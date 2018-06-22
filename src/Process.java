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

/**
 *
 * @author Hayat
 */
public class Process {

    Connection conn;
    Statement st;
    ResultSet rs;
    String driver = "com.mysql.jdbc.Driver";

    String processID;
    String processType;
    String ProcessName;
    private ArrayList<Task> listTask;
    private ArrayList<Resource> allResources;

    public Process(String processID, Statement s) {
        this.processID = processID;
        st = s;
        listTask = new ArrayList();
        allResources = new ArrayList();
//        getConnectDB();
        this.getTaskData();
        this.getAllResources();
        this.getPrevTask();
        this.getNextTask();
        this.getSkillData();
        this.getResourceData();
    }

    public String getID() {
        return processID;
    }

    /**
     * @return the listTask
     */
    public ArrayList<Task> getListTask() {
        return listTask;
    }

//    public void getConnectDB() {
//        try {
//            Class.forName(driver);
//            conn = DriverManager.getConnection("jdbc:mysql://mysql-healthview.alwaysdata.net/healthview_test", "152416_sir", "projetsir2018");
//            st = conn.createStatement();
//            System.out.println("You are connected ! ");
//        } catch (Exception ex) {
//            System.out.println("Error : " + ex);
//        }
//    }
    public void getTaskData() {
        try {
            String query = "SELECT * FROM Task WHERE ProcessID ='" + processID + "'";
            rs = st.executeQuery(query);
            System.out.println("Records from DataBase");
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

    }

    public void getAllResources() {
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
    }

    public void getPrevTask() {
        try {
            String query = "SELECT TaskID, PrevTaskID FROM PreviousTask JOIN Task ON Task.ID = PreviousTask.IDcouple WHERE Task.ProcessID ='" + processID + "'";
            rs = st.executeQuery(query);
            while (rs.next()) {

                String prevTask = rs.getString("PrevTaskID");
                String taskID = rs.getString("TaskID");
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).getTaskID().equals(taskID)) {
                        listTask.get(i).addPrevTask(prevTask);
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void getNextTask() {
        try {
            String query = "SELECT TaskID, NextTaskID FROM NextTask JOIN Task ON Task.ID = NextTask.IDcouple WHERE Task.ProcessID ='" + processID + "'";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String nextTask = rs.getString("NextTaskID");
                String taskID = rs.getString("TaskID");
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).getTaskID().equals(taskID)) {
                        listTask.get(i).addNextTask(nextTask);
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void getSkillData() {

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

    public void getResourceData() {

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

}
