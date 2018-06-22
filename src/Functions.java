
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.util.List;

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

    public double fO(List<Patient> scur) {
        double result;
        
        
        Test t = new Test(scur);
        t.addTask();
        
        result = t.getMakespan()+wait(t.getTotalWaitingTime())+late(t.getLateness());
       
        
        return result;
    }
    
     public List<Patient> annealingMin(double temperature,int itermax ,  List<Patient> scur) {
         List<Patient> snew;
        double tempmin = 0.1;
        int numiter = 0;
        double coolingRate = 0.01;
        double dif;
        double rd;
        List<Patient> minb = scur;
        while (temperature >= tempmin) {
            //System.out.println("hey");
            if (numiter < itermax) {
                snew = SwappableSequence.deterministicSwap(scur,numiter%(scur.size()),(numiter+1)%(scur.size()));
                //System.out.println(snew.toString());
                dif = fO(snew) - fO(scur);
                //System.out.println(f(snew)+ "-" + f(scur)+" = "+dif);

                if (dif >= 0) {
                    rd = Math.random();
                    if (rd < exp(-dif / (1.38064852 * pow(10, -23)) * temperature)) {
                        scur = snew;
                    }
                } else {
                    scur = snew;
                    if (fO(scur) < fO(minb)) {
                        minb = scur;

                    }
                }
                numiter++;
//                    System.out.println("");
//                    System.out.println(temperature);
//                    System.out.println("iteration n°"+numiter);
//                    System.out.println("Current x min : "+ minb);
//                    System.out.println("Current minimum:"+ f(minb));
//                    System.out.println("Position of the scur: "+ scur);
//                    System.out.println("Outcome of the f(scur): "+ f(scur));
//                    System.out.println("Position of snew: "+snew);
//                    System.out.println("Outcome of the f(snew):" + f(snew));

            } else {
                temperature = coolingRate * temperature;
                numiter = 0;
            }

        }
        System.out.print("The minimum is located  ");
        return minb;
    }

}
