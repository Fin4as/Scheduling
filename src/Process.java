/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



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
    private List<Resource> allResources;

    public Process(String processID,List<Task> listTask,List<Resource> allResources) {
        this.processID = processID;
//        st = s;
        this.listTask = listTask;
        this.allResources = allResources;
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
    
    public List<Resource> getListResource() {
        return allResources;
    }


    public List<Resource> getAllResources() {
        return allResources;
    }
  
}
