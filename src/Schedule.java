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
public class Schedule {

    
    ResultSet rs;
    String driver = "com.mysql.jdbc.Driver";
    Statement st;
    Connection conn;
    

    ArrayList<Process> listProcess;

    public Schedule() {
        listProcess = new ArrayList();
        getConnectDB(); // connect to DataBAase
        getProcessData(); // get list of process from DB
    }

    public ArrayList<Process> getListProcess() {
        return listProcess;
    }

    public void getConnectDB() {
        try {
            
            Class.forName(driver);
            conn = DriverManager.getConnection("jdbc:mysql://mysql-healthview.alwaysdata.net/healthview_test", "152416_sir", "projetsir2018");

            st = conn.createStatement();
            System.out.println("You are connected ! ");
           
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
           
        }
    }

    public void getProcessData() {

        try {
            
            String query = "SELECT * FROM Process";
            rs = st.executeQuery(query);
            System.out.println("Records from DataBase");
            while (rs.next()) {
                String processID = rs.getString("ProcessID");
                Process process = new Process(processID, st);
                listProcess.add(process);
            }
        st.close();
        rs.close();
        conn.close();
        } catch (Exception ex) {
            System.out.println(ex);
            
        }

    }
//
//    public Statement getStatement() {
//        return st;
//    }
//
//    public ResultSet getResultSet() {
//        return rs;
//    }
}