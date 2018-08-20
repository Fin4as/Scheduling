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
 * This class aims to get data from Database : process and patients
 *
 * @author Hayat
 *
 */
public class Data {

    ResultSet rs;
    String driver = "com.mysql.jdbc.Driver";
    Statement st;
    Connection conn;
    private List<Process> listProcess; // list of process assigned to patients
    private List<Resource> allResources; // list of resources needed for all process 
    private List<String> nameResource; // list of Resources' ID
    private List<Patient> listPatients; // list of Patients
    private List<Integer> numberPatientsPerSurgery; // list of nulber of patients per surgery in data base
    int stochasticDuration; // variable to generate stochastic values of tasks and surgery task
    int presenceP; // value indicating if a patient is involved in a task or not . 1 = involved, 0 = not involved

    /**
     *Constructor of class Data
     * Initializes variables 
     * Ensure a single connection
     * @see #getConnectDB() 
     * @see #getAllResources() 
     * @see #getListPatients() 
     * @see #getNextTask(java.util.List, java.lang.String) 
     * @see #getNumberPatientsPerSurgery() 
     * @see #getPatientData() 
     * @see #getListProcess() 
     * @see #getPrevTask(java.util.List, java.lang.String) 
     * @see #getProcess(java.util.List) 
     * @see #getResourceData(java.util.List) 
     * @see #getTaskData(Process) 
     * @see #getSurgeryDuration(java.util.List, Patient) 
     * @see #getSkillData(java.util.List, java.lang.String) 
     * 
     * 
     */
    public Data() {
        listPatients = new ArrayList();
        allResources = new ArrayList();
        nameResource = new ArrayList();
        getConnectDB();
        numberPatientsPerSurgery = new ArrayList();
        this.getNumberPatientsPerSurgery();
        this.getPatientData();

        List<String> listP = this.getProcess(listPatients);
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

    /**
     *
     * @param listPatient list of patient btained from data base
     * @return a list of String called idProcess. It corresponds to Patients'
     * processID
     *
     */
    public List<String> getProcess(List<Patient> listPatient) {
        List<String> idProcess = new ArrayList();

        for (Patient e : listPatient) {

            idProcess.add(e.getProcessID());
        }
        return idProcess;
    }

    /**
     * method to get Patient Data from DB
     * 
     */
    public void getPatientData() {

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

    }

    /**
     * method to get number of patients per surgery type and fills list
     */
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

    /**
     *
     * @return list of resources
     */
    public List<Resource> getAllResources() {
        return allResources;
    }

    /**
     *
     * @return list of Process
     */
    public List<Process> getListProcess() {
        return listProcess;
    }

    /**
     *
     * @return list of patients
     */
    public List<Patient> getListPatients() {
        return listPatients;
    }

    /**
     * method to connect to data base (only once)
     */
    public void getConnectDB() {
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection("jdbc:mysql://mysql-healthview.alwaysdata.net/healthview_copy_test", "152416_sir", "projetsir2018");

            st = conn.createStatement();
            System.out.println("You are connected !");

        } catch (Exception ex) {

            System.out.println("Error : " + ex);

        }
    }

    /**
     *
     * @param pro is a specific process method to get tasks' data of this
     * specific process stochastic durations are generated for each task
     */
    public void getTaskData(Process pro) {

        try {
            String query = "SELECT * FROM Task WHERE ProcessID ='" + pro.getID() + "'" + "ORDER BY Task.`ID` ASC";
            rs = st.executeQuery(query);

            while (rs.next()) {
                String process_id = rs.getString("ProcessID");
                String task_id = rs.getString("TaskID");
                int opMode = rs.getInt("OpMode");
                int avTime = rs.getInt("AvTime");
                int stdDev = rs.getInt("StdDev");
                int maxWait = rs.getInt("MaxWait");

                stochasticDuration = (avTime - stdDev) + (int) (Math.random() * ((avTime - stdDev) + 1)); // stcohastic values for tasks duration
//                the comment below had to make sure the stochastic value generated is different from zero
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

    /**
     * this method generates stochastic values of surgery task's duration for a given
     * petient
     *
     * @param listTask of a specific process
     * @param p is a patient who has a specific type of surgery
     *
     */
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
//                        the comment below had to make sure the stochastic value generated is different from zero 
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

    /**
     * Method to get Skill Data that a list of Tasks has
     * @param listTask
     * @param processID needed to spcify in sql query with process is involved
     */
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

    /**
     * Method to get Resources' data of list of tasks
     * 
     * @param listTask 
     */
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
                            if (!nameResource.contains(resourceID)) { // this condition is to make sure we get only once the resources from database. As each resource is unique
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

   /**
    * Method add list of Previous Tasks for a specific task. Based on a list of tasks and a processID
    * @param listTask 
    * @param processID 
    */
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
            System.out.println(ex);
        }
    }

    /**
     * Method add list of Next Tasks for a specific task. Based on a list of tasks and a processID
     * @param listTask 
     * @param processID 
     */
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
            System.out.println(ex);
        }
    }

}
