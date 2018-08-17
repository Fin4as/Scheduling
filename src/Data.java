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
public class Data {

    ResultSet rs;
    String driver = "com.mysql.jdbc.Driver";
    Statement st;
    Connection conn;
    private List<Process> listProcess;
    private List<Resource> allResources;
    private List<String> nameResource;
    private List<Patient> listPatients; //patientData
    private List<Integer> numberPatientsPerSurgery;
    int stochasticDuration;
    int presenceP;

    public Data() {
        listPatients = new ArrayList(); //PatientData
        allResources = new ArrayList();
        nameResource = new ArrayList();
        getConnectDB(); // connect to DataBase
        numberPatientsPerSurgery = new ArrayList();
        this.getNumberPatientsPerSurgery();
        this.getPatientData();

        List<String> listP = this.getProcess(listPatients); //PatientData
        listProcess = new ArrayList();

        for (int i = 0; i < listP.size(); i++) {

            String processID = listP.get(i);
            Process pro = new Process(processID);
            listProcess.add(pro);
            this.getTaskData(pro);
            this.getSurgeryDuration(pro.getListTask(), this.listPatients.get(i));
            this.getPrevTask(pro.getListTask(), processID);
            this.getNextTask(pro.getListTask(), processID);
            this.getSkillData(pro.getListTask(), processID);
            this.getResourceData(pro.getListTask());

        }
    }

    public List<String> getProcess(List<Patient> listPatient) { //PatientData
        List<String> idProcess = new ArrayList();

        for (Patient e : listPatient) {
//            if (!idProcess.contains(e.getProcessID())) {
            idProcess.add(e.getProcessID());
//            }
        }
        return idProcess;
    }

    public void getPatientData() { //PatientData

        try {
            String query = "SELECT * FROM Patient";
            rs = st.executeQuery(query);

            while (rs.next()) {
                String patient_id = rs.getString("PatientID");
                String process_id = rs.getString("ProcessID");
                int ageInformation = rs.getInt("ageInformation");
                String typeSurgery = rs.getString("typeSurgery");

                Patient patient = new Patient(patient_id, process_id, ageInformation, typeSurgery, numberPatientsPerSurgery);
                listPatients.add(patient);

            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    } //PatientData

    public void getNumberPatientsPerSurgery() {

        try {
            String query = "SELECT IDchar, typeSurgery, COUNT(*) FROM (SELECT * FROM Patient NATURAL JOIN SurgeryTypes ORDER BY SurgeryTypes.IDchar) AS numberPatientsPerSurgery GROUP BY typeSurgery ORDER BY numberPatientsPerSurgery.IDchar";
            rs = st.executeQuery(query);

            while (rs.next()) {
                int numberOfPatientPersurgery = rs.getInt("COUNT(*)");
                numberPatientsPerSurgery.add(numberOfPatientPersurgery);

            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public List<Resource> getAllResources() {
        return allResources;
    }

    public List<Process> getListProcess() {
        return listProcess;
    }

    public List<Patient> getListPatients() { //PatientData
        return listPatients;
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
            String query = "SELECT * FROM Task WHERE ProcessID ='" + pro.getID() + "'" + "ORDER BY Task.`ID` ASC";
            rs = st.executeQuery(query);

            while (rs.next()) {
                String process_id = rs.getString("ProcessID");
                String task_id = rs.getString("TaskID");
//                int patient = rs.getInt("patient");
                int opMode = rs.getInt("OpMode");
                int avTime = rs.getInt("AvTime");
                int stdDev = rs.getInt("StdDev");
                int maxWait = rs.getInt("MaxWait");

                stochasticDuration = (avTime - stdDev) + (int) (Math.random() * ((avTime - stdDev) + 1)); // stcohastic values for tasks duration
//                boolean zero = false;
//                while (!zero && stochasticDuration == 0) {
//                    stochasticDuration = (avTime - stdDev) + (int) (Math.random() * ((avTime - stdDev) + 1));
//                    if (stochasticDuration != 0) {
//                        zero = true;
//                    }
//                }
                if (stochasticDuration < 0) {
                    stochasticDuration = Math.abs(stochasticDuration);
                }
                Task task = new Task(process_id, task_id, this.presenceP, opMode, stochasticDuration, stdDev, maxWait);
                pro.addListTask(task);

            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public void getSurgeryDuration(List<Task> listTask, Patient p) {
        for (int i = 0; i < listTask.size(); i++) {
            if (listTask.get(i).getTaskID().equals("Surgery")) {

                try {
                    String query = "SELECT Patient.typeSurgery, SurgeryTypes.avTime, SurgeryTypes.stdDev FROM SurgeryTypes JOIN Patient ON Patient.typeSurgery = SurgeryTypes.typeSurgery where Patient.PatientID = '" + p.getPatientID() + "'";
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        int stdDev = rs.getInt("stdDev");
                        int avTime = rs.getInt("AvTime");
                        listTask.get(i).setStdDev(stdDev);

                        stochasticDuration = (avTime - stdDev) + (int) (Math.random() * ((avTime - stdDev) + 1)); // stcohastic values for tasks duration
//                        boolean zero = false;
//                        while (!zero && stochasticDuration == 0) {
//                            stochasticDuration = (avTime - stdDev) + (int) (Math.random() * ((avTime - stdDev) + 1));
//                            if (stochasticDuration != 0) {
//                                zero = true;
//                            }
//                        }

                        if (stochasticDuration < 0) {
                            stochasticDuration = Math.abs(stochasticDuration);
                        }
                        listTask.get(i).setAvTime(stochasticDuration);

                    }

                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

        }
    }

    public void getSkillData(List<Task> listTask, String processID) {

        for (int i = 0; i < listTask.size(); i++) {
            try {

                String query = "SELECT SkillID, Description, PrevTask FROM Skill NATURAL JOIN TaskSkill JOIN Task ON TaskSkill.IDcouple=Task.ID WHERE TaskID = '" + listTask.get(i).getTaskID() + "' AND Task.ProcessID = '" + processID + "'" + " ORDER BY TaskSkill.`IDchar` ASC";
                rs = st.executeQuery(query);
                while (rs.next()) {

                    String skillID = rs.getString("SkillID");
                    String description = rs.getString("Description");
                    String prevTask = rs.getString("PrevTask");

                    if (skillID.equals("SP")) {
                        listTask.get(i).setPatientPresence(1);
                    } else if (!skillID.equals("SP")) {
                        listTask.get(i).setListSkill(new Skill(skillID, description, prevTask));
                    }

                }

            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    public void getResourceData(List<Task> listTask) {

        for (int i = 0; i < listTask.size(); i++) {
            for (int j = 0; j < listTask.get(i).getListSkill().size(); j++) {
                try {
                    String query = "SELECT ResourceID, Capacity, Name FROM Resource NATURAL JOIN ResourceSkill WHERE SkillID =" + "'" + listTask.get(i).getListSkill().get(j).getSkillID() + "'" + "ORDER BY ResourceSkill.`IDchar` ASC";
                    rs = st.executeQuery(query);
                    while (rs.next()) {

                        String resourceID = rs.getString("ResourceID");
                        int capacity = rs.getInt("Capacity");
                        String name = rs.getString("Name");

                        if (!name.equals("P")) {
                            if (!nameResource.contains(resourceID)) {
                                Resource res = new Resource(resourceID, capacity, name);

                                listTask.get(i).getListSkill().get(j).addResource(res);
                                allResources.add(res);
                                nameResource.add(resourceID);
                            } else {
                                int index = nameResource.indexOf(resourceID);
                                Resource res = allResources.get(index);
                                listTask.get(i).getListSkill().get(j).addResource(res);
                            }
                        }

                    }

                } catch (Exception ex) {

                    System.out.println(ex);
                }
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
            String query = "SELECT TaskID, PrevTaskID FROM PreviousTask JOIN Task ON Task.ID = PreviousTask.IDcouple WHERE Task.ProcessID ='" + processID + "'" + "ORDER BY PreviousTask.`IDchar` ASC";
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
            String query = "SELECT TaskID, NextTaskID FROM NextTask JOIN Task ON Task.ID = NextTask.IDcouple WHERE Task.ProcessID ='" + processID + "'" + "ORDER BY NextTask.`IDchar` ASC";
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
                        listTask.get(i + 2).setAvTime(listTask.get(i + 1).getAvTime());

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