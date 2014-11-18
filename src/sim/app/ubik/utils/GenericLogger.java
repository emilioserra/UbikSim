/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Botía , juanbot[at] um.es
 * Pablo Campillo, pablocampillo[at] um.es
 * Francisco Campuzano, fjcampuzano[at] um.es
 * Emilio Serrano, emilioserra [at] dit.upm.es
 * 
 * This file is part of UbikSimIDE.
 * 
 *     UbikSimIDE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     UbikSimIDE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with UbikSimIDE.  If not, see <http://www.gnu.org/licenses/>
 */

package sim.app.ubik.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
/**
 * Generic logger  allows developers to store and perform statistic operations over data from different experiments.
 * A GenericLogger must be creared in each experiment/execution of simulation. 
 * In each step, its method addStep is called with data to be stored (only doubles).
 * A method getStatisticOperation can perform a generic statistic operation given a list of GenericLogger (for each experiment) and the operation.
 * There are methods to calculate mean and standard deviation which call getStatisticOperation with the specific operations. 
 */
public class GenericLogger {

    
    public ArrayList<double[]> log= new ArrayList<double[]>();
    public String[] headings;
    /**
     * headings of the data store
     * @param headings 
     */
    public GenericLogger(String[] headings){
        this.headings=headings;
    }
    /**
     * Store data for one step of a experiment. X double propperties are stored.
     * @param data 
     */
    public void addStep(double[] data){      
        log.add(data);        
    }
    
    /**
     * Given several GenericLogger, calculates an abstract statistic operation of each field registered.
     * Results are in the field log of the GenericLogger returned.
     */
    public static GenericLogger getStatisticOperation(List<GenericLogger> listOfResults, AbstractStorelessUnivariateStatistic aus){

        GenericLogger firstExperiment= listOfResults.get(0);
        GenericLogger r= new  GenericLogger(firstExperiment.headings);
        int steps=firstExperiment.log.size();
        for(int i=0;i<steps;i++){//for each step            
          double resultForOneStep[]= new double[firstExperiment.log.get(0).length];
            for(int j=0;j<resultForOneStep.length;j++){//for each property stored in each step
                double[] values= new double[listOfResults.size()];
                for(int z=0;z< listOfResults.size();z++){//for each experiment          
                    values[z]= listOfResults.get(z).log.get(i)[j];
                }
                resultForOneStep[j]= aus.evaluate(values);                                
            }
            r.log.add(resultForOneStep);                        
        }
        return r;
    }
    
    
    /**
     * Get mean, it uses getStatisticOperation
     * @param listOfResults
     * @return 
     */
    public static GenericLogger getMean(List<GenericLogger> listOfResults){
        return getStatisticOperation(listOfResults, new Mean());
    }
    
    
       /**
     * Get standar deviation, it uses getStatisticOperation
     * @param listOfResults
     * @return 
     */
    
   public static GenericLogger getStandardDeviation(List<GenericLogger> listOfResults){
        return getStatisticOperation(listOfResults, new StandardDeviation());
    }
   
   
   /**
    * It gives data in a GenericLogger as a String
    * @param gl  A generic Logger
    */
    @Override
   public String toString(){
        DecimalFormat df = new DecimalFormat("0.00");     
        String s="Step \t";
        for(int i=0;i<headings.length;i++){
            s+= headings[i] + "\t";
        }              
        s+="\n";
        for(int i=0;i<log.size();i++){
            s+= (i+1) + "\t";
            for(int j=0;j<log.get(i).length;j++){                               
                s+= df.format(log.get(i)[j]) + "\t";
            }
            s+="\n";
        }
        return s;
   }
    
    
    /**
     * Return only a row, data for one step
     * @param row row or step to get data
     * @return 
     */
   public String toString(int row){
        DecimalFormat df = new DecimalFormat("0.00");   
        String s="";
        for(int j=0;j<log.get(row).length;j++){                               
                s+= df.format(log.get(row)[j]) + "\t";
            }         
        
        return s;
   }
   
   public int getRows(){
       return log.size();
   }
   
   public String getHeadings(String prefix, String subfix){
       String s="";
       for(int i=0;i<headings.length;i++){
           if(prefix!=null) s+= prefix + " ";
           s+= headings[i];
           if(subfix!=null) s+= " " + subfix;
           s+="\t";
        }         
       return s;
   }
    
}

