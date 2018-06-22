
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
    
    public double late(double l) {
        double result = 0.0;
        result = 0.125 * l;
        return result;
    }

    public double wait(double w) {
        double result = 0.0;
        result = (1 / 6) * w;
        return result;
    }

    public double fO(ArrayList<Patient> scur) {
        double result;
        
        //adding the scheduling part of Hayat
        
        result = scur.getMakespan() + wait() + late();
       
        
        return result;
    }
}
