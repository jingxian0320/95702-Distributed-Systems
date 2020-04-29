/**
 * Author: Jingxian Bao
 * Last Modified: Feb 6, 2020
 *
 * This model records the data
 *  * necessary for the Clicker Webapp.
 *  * It contains the function to clear
 *  * all the data and record option to
 *  * the model
 */


package cmu.edu.jingxiab;

import java.util.HashMap;

public class ClickerModel {
    HashMap<String, Integer> result = new HashMap<String, Integer> ();

    // the function to clear all the data in the model
    void clear(){
        result.clear();
    }

    // record the selected option to the result hashmap
    void add(String option){
        if (result.containsKey(option)){
            result.put(option, result.get(option) + 1);
        }
        else{
            result.put(option, 1);
        }
    }
}
