/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package scheduling_First_Try;
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
    private List<Resource> allResources;
    private List<String> nameResource;


    public Schedule(List<Patient> listPatient) {
        allResources = new ArrayList();
        nameResource = new ArrayList();
        List<String> listP = this.getProcess(listPatient);
        listProcess = new ArrayList();
        getConnectDB(); // connect to DataBAase
        for (int i = 0; i < listP.size(); i++) {
            String processID = listP.get(i);
            Process pro = new Process(processID);
            listProcess.add(pro);
//            List<Resource> allResources = new ArrayList<Resource>();
//            List<Task> listTask = new ArrayList();
            this.getTaskData(pro);
            List<Task> listTask = pro.getListTask();
            this.getPrevTask(pro.getListTask(), processID);
            this.getNextTask(pro.getListTask(), processID);
            this.getSkillData(pro.getListTask(), processID);
            this.getResourceData(pro.getListTask());
           
//            this.getResourceData(listTask, allResources);
//              Process pro = new Process(namePro, listTask, allResources);
//            listPro.add(pro);
        }
//        this.listProcess = listPro;

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

    public List<Resource> getAllResources(){
        return allResources;
    }
    public List<Process> getListProcess() {
        return listProcess;
    }

    public void getConnectDB() {
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection("jdbc:mysql://mysql-healthview.alwaysdata.net/healthview_copy_test", "152416_sir", "projetsir2018");

            st = conn.createStatement();
            System.out.println("You are connected ! ");

        } catch (Exception ex) {
            
            System.out.println("Error : " + ex);

        }
    }

    public void getTaskData(Process pro) {
        
        try {
            String query = "SELECT * FROM Task WHERE ProcessID ='" + pro.getID()+"'";
            rs = st.executeQuery(query);

            while (rs.next()) {
                String process_id = rs.getString("ProcessID");
                String task_id = rs.getString("TaskID");

                int opMode = rs.getInt("OpMode");
                int avTime = rs.getInt("AvTime");
                int stdDev = rs.getInt("StdDev");
                int maxWait = rs.getInt("MaxWait");

                Task task = new Task(process_id, task_id, opMode, avTime, stdDev, maxWait);
                pro.addListTask(task);
            }

        } catch (Exception ex) {
            System.out.println("Hey 1");
            System.out.println(ex);
        }
        
    }

    public void getSkillData(List<Task> listTask, String processID) {

        for (int i = 0; i < listTask.size(); i++) {
            try {

                String query = "SELECT SkillID, Description, PrevTask FROM Skill NATURAL JOIN TaskSkill JOIN Task ON TaskSkill.IDcouple=Task.ID WHERE TaskID = '" + listTask.get(i).getTaskID() + "' AND Task.ProcessID = '" + processID+"'";
                rs = st.executeQuery(query);
                while (rs.next()) {

                    String skillID = rs.getString("SkillID");
                    String description = rs.getString("Description");
                    String prevTask = rs.getString("PrevTask");
                    listTask.get(i).setSkill(new Skill(skillID, description, prevTask));

                }

            } catch (Exception ex) {
                System.out.println("Hey 2");
                System.out.println(ex);
            }
        }
    }

    public void getResourceData(List<Task> listTask) {
        
        for (int i = 0; i < listTask.size(); i++) {
            try {
                String query = "SELECT ResourceID, Capacity, Name FROM Resource NATURAL JOIN ResourceSkill WHERE SkillID =" + "'" + listTask.get(i).getSkill().getSkillID() + "'";
                rs = st.executeQuery(query);
                while (rs.next()) {

                    String resourceID = rs.getString("ResourceID");
                    int capacity = rs.getInt("Capacity");
                    String name = rs.getString("Name");
                    if (!nameResource.contains(resourceID)) {
                        Resource res = new Resource(resourceID, capacity, name);
                        listTask.get(i).getSkill().addResource(res);
                        allResources.add(res);
                        nameResource.add(resourceID);
                    } else {
                        int index = nameResource.indexOf(resourceID);
                        Resource res = allResources.get(index);
                        listTask.get(i).getSkill().addResource(res);
                    }

                }
               
            } catch (Exception ex) {
               
                System.out.println(ex);

            }
        }


    }


//    public void getResourceData(List<Task> listTask, List<Resource> allResources) {
//
//        try {
//            for (int i = 0; i < listTask.size(); i++) {
//                String query = "SELECT ResourceID, Capacity, Name FROM Resource NATURAL JOIN ResourceSkill JOIN Task ON Task.ID = ResourceSkill.IDcouple WHERE TaskID = '" + listTask.get(i).getTaskID() + "' AND SkillID ='" + listTask.get(i).getSkill().getSkillID() + "'";
//                rs = st.executeQuery(query);
//                while (rs.next()) {
//                    String resourceID = rs.getString("ResourceID");
//                    int k = 0;
//                    boolean found = false;
//                    while (k < allResources.size() && !found) {
//
//                        if (resourceID.equals(allResources.get(k).getResourceID())) {
//                            found = true;
//                            listTask.get(i).getSkill().getListResource().add(allResources.get(k));
//
//                        }
//                        k++;
//
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            System.out.println(ex);
//
//        }
//    }
//
//    public List<Resource> dataAllResources(String processID) {
//        List<Resource> allResources = new ArrayList<Resource>();
//        try {
//            String query = "SELECT DISTINCT ResourceID, Name, Capacity FROM Resource NATURAL JOIN ResourceSkill JOIN Task ON Task.ID = ResourceSkill.IDcouple WHERE Task.ProcessID ='" + processID + "'";
//            rs = st.executeQuery(query);
//            while (rs.next()) {
//                String resourceID = rs.getString("ResourceID");
//                int capacity = rs.getInt("Capacity");
//                String name = rs.getString("Name");
//                Resource res = new Resource(resourceID, capacity, name);
//                allResources.add(res);
//            }
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//        return allResources;
//    }
//

    public void getPrevTask(List<Task> listTask, String processID) {

        try {
            String query = "SELECT TaskID, PrevTaskID FROM PreviousTask JOIN Task ON Task.ID = PreviousTask.IDcouple WHERE Task.ProcessID ='" + processID + "'"+ "ORDER BY `PreviousTask`.`IDchar` ASC";
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
            System.out.println("Hey 4");
            System.out.println(ex);
        }
    }

    public void getNextTask(List<Task> listTask, String processID) {
        try {
            String query = "SELECT TaskID, NextTaskID FROM NextTask JOIN Task ON Task.ID = NextTask.IDcouple WHERE Task.ProcessID ='" + processID + "'" + "ORDER BY `NextTask`.`IDchar` ASC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String nextTask = rs.getString("NextTaskID");
                String taskID = rs.getString("TaskID");
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).getTaskID().equals(taskID)) {
                        listTask.get(i).addNextTask(nextTask);
                    }
                    if (listTask.get(i).getNextTaskIDList().size() > 1) { 
                        listTask.get(i + 1).setParallelTask(listTask.get(i + 2)); 
                        listTask.get(i + 2).setParallelTask(listTask.get(i + 1)); 
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Hey 5");
            System.out.println(ex);
        }
    }

//    public Statement getStatement() {
//        return st;
//    }
//
//    public ResultSet getResultSet() {
//        return rs;
//    }
    
}