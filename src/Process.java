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
public class Process {

//    Connection conn;
//    Statement st;
//    ResultSet rs;
//    String driver = "com.mysql.jdbc.Driver";

    String processID;
    String processType;
    String ProcessName;
    private List<Task> listTask;
 
    public Process(String processID) {
        this.processID = processID;
        this.listTask = new ArrayList();
//        st = s;
//        this.listTask = listTask;
//        this.allResources = allResources;
//        getConnectDB();

    }

    public String getID() {
        return processID;
    }

    /**
     * @return the listTask
     */
    public List<Task> getListTask() {
        return listTask;
    }
    
    public void addListTask(Task t){
        listTask.add(t);
    }
    
//    public List<Resource> getListResource() {
//        return allResources;
//    }
//
//
//    public List<Resource> getAllResources() {
//        return allResources;
//    }
  
}
