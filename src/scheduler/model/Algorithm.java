/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.util.ArrayList;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author adrian
 */
public class Algorithm {
    private static ObservableList<Schedule> Schedules = FXCollections.observableArrayList();
    private static ObservableList<Schedule> Elite = FXCollections.observableArrayList();
    private static Schedule bestSchedule;
    private static final int POPULATION = 1000;
    
    //testowalem czy dobrze generuje randomowy plan zajec i czy dobrze wyswietla, wiec TODO
    public static void start(){
        /*int iterations = 0;
    //inicjacja populacji poczatkowej
        for(int i = 0; i < POPULATION; i++){
            Schedules.add(new Schedule());
            Schedules.get(0).generateSchedule();
        }
    //ocena populacji poczatkowej
        calcFitness();
        if(selectBest().getFitness() == 1)
            return;
        
        while(getBestFitness() < 0.99) //dalem tyle, bo nie wiem czy te double sa wystarczajaco dokladne, zeby dac 1.0
        {
            
        }
        */
        Schedules.add(new Schedule());
        Schedules.get(0).generateSchedule();
        bestSchedule = Schedules.get(0);
        Data.setSchedule(bestSchedule);
    }
    

    public static Schedule crossover(Schedule sch1, Schedule sch2){
    	Schedule child = new Schedule();
    	Random rand = new Random();
    	int crossoverBegin =  rand.nextInt(Data.getHours() - sch1.getCrossoverSize());
    	int crossoverDay = rand.nextInt(Data.getDays());
    	int day = 0, hour = 0, group = 0;
    	ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
    	for(int i = 0; i < sch1.getSchedule().length; i++){					//przepisywanie
    		if(day == crossoverDay && (hour >= crossoverBegin && hour <= crossoverBegin + child.getCrossoverSize())){
    			child.setSchedule(day, hour, group, sch2.getSchedule()[day][hour][group]);
    			lessonList.add(child.getSchedule()[day][hour][group]);
    		}
    		child.setSchedule(day, hour, group, sch1.getSchedule()[day][hour][group]);
    		++hour;
    		if(hour == Data.getHours() - 1){
    			hour = 0;
    			++day;
    		}
    		if(day == Data.getDays() - 1){
    			day = 0;
    			++group;
    		}    			
    	}
    	day = 0; hour =0; group = 0;
    	for(int j = 0; j < lessonList.size(); j++){			//usuwanie duplikatów - cos lepszego by sie przydalo
    		
    		for(int i = 0; i < child.getSchedule().length; i++){
    			if(day == crossoverDay && (hour >= crossoverBegin && hour <= crossoverBegin + child.getCrossoverSize())){
    				continue;								// jesli miejsce gdzie byl crossover to nie sprawdzam
    			}
    			if(lessonList.get(j) == child.getSchedule()[day][hour][group] )
    				child.setSchedule(day, hour, group, null);	//jesli poza to pole ustawiam na null
	    		++hour;
	    		if(hour == Data.getHours() - 1){
	    			hour = 0;
	    			++day;
	    		}
	    		if(day == Data.getDays() - 1){
	    			day = 0;
	    			++group;
	    		}  
	    	}
    	}
	    return child;
    }
    
    private static void selection(){
        calcFitness();
        selectElite();
    }
    
    private static void calcFitness(){
        for(Schedule s : Schedules){
            s.calcFitness();
            s.calcSoftReqValue();
        }
    }
    
    private static void selectElite(){
        Elite.clear();
        for(int i = 0; i < Data.getSelectionParam(); i++){
            Elite.add(selectBest());
        }
    }
    
    private static Schedule selectBest(){
        Schedule best = new Schedule();
        for(Schedule s : Schedules)
        {
            if(best.getFitness() < s.getFitness())
                best = s;
            if(best.getFitness() == s.getFitness())
                if(best.getSoftReqValue() < s.getSoftReqValue())
                    best = s;
        }
        return best;
    }
    
    private static double getBestFitness(){
        return selectBest().getFitness();
    }
    
    public static ObservableList<Schedule> getSchedules(){
        return Schedules;
    }
    
    public static Schedule getBestSchedule(){
        return bestSchedule;
    }
    
}