/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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

    public List<Resource> getAllResources() {
        return allResources;
    }
  
}
