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
    private static Schedule bestSchedule;
    
    //testowalem czy dobrze generuje randomowy plan zajec i czy dobrze wyswietla, wiec TODO
    public static void start(){
        Schedules.add(new Schedule());
        Schedules.get(0).generateSchedule();
        bestSchedule = Schedules.get(0);
        Data.setSchedule(bestSchedule);
    }
    
    public Schedule crossover(Schedule sch1, Schedule sch2){
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
    public static ObservableList<Schedule> getSchedules(){
        return Schedules;
    }
    
    public static Schedule getBestSchedule(){
        return bestSchedule;
    }
}