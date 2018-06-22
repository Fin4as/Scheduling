
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author robin
 */
public class Functions {
    
    public double late(int l) {
        double result = 0.0;
        result = 0.125 * l;
        return result;
    }

    public double wait(int w) {
        double result = 0.0;
        result = (1 / 6) * w;
        return result;
    }

    public double fO(ArrayList<Patient> scur) {
        double result;
        
        //adding the scheduling part of Hayat
        Test t = new Test(scur);
        t.addTask();
        
        result = t.getMakespan()+wait(t.getTotalWaitingTime())+late(t.getLateness());
       
        
        return result;
    }
}
