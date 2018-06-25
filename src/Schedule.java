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

    List<Process> listProcess;

    public Schedule(List<Patient> listPatient) {
        listProcess = new ArrayList();
        getConnectDB(); // connect to DataBAase
        getProcessData(listPatient); // get list of process from DB
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

    public void getProcessData(List<Patient> listPatient) {
        List<String> idProcess = new ArrayList();
        String ty = "";
        for (int k = 0; k < listPatient.size(); k++) {
            if (!idProcess.contains(listPatient.get(k).getProcessID())) {
                idProcess.add(listPatient.get(k).getProcessID());
                ty+="ProcessID ='"+""
                System.out.println(listPatient.get(k).getProcessID());
            }
        }
        try {
            for (int i = 0; i < idProcess.size(); i++) {
                String query = "SELECT * FROM Process WHERE ProcessID ='" + idProcess.get(i) + "'";
                System.out.println(query);
                rs = st.executeQuery(query);
//            System.out.println("Records from DataBase");
                while (rs.next()) {
                    String processID = rs.getString("ProcessID");
                    Process process = new Process(processID, st);
                    listProcess.add(process);
                }
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
