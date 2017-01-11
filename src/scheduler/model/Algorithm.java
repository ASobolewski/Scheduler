/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author adrian
 */
public class Algorithm {
    private static ArrayList<Schedule> Schedules;
    private static ArrayList<Schedule> Elite;
    private static Schedule bestSchedule;
    private static final int MI = 1000;
    private static final int LAMBDA = 500;
    private static double totalFitness;
    

    public static void start(){
        int iterations = 0;
        ArrayList<Schedule> tempSchedules;
        ArrayList<Schedule> offspring;
        Schedules = new ArrayList<>();
        System.out.println(iterations);
    //inicjacja populacji poczatkowej
        for(int i = 0; i < MI; i++){
            Schedule s = new Schedule();
            s.generateSchedule();
            Schedules.add(s);
        }
        System.out.println(iterations);
    //ocena populacji poczatkowej
        calcFitness(Schedules);
        System.out.println(iterations);
        while(getBestFitness() < 0.99) //dalem tyle, bo nie wiem czy te double sa wystarczajaco dokladne, zeby dac 1.0
        {
            iterations++;
            tempSchedules = reproduction();
            tempSchedules = crossover(tempSchedules);
            offspring = mutation(tempSchedules);
            selection(offspring);
            Schedules = Elite;
            System.out.println(iterations);
        }
        
        /*Data.setSchedule(selectBest());
        Schedules.add(new Schedule());
        Schedules.get(0).generateSchedule();
        Schedules.add(new Schedule());
        Schedules.get(1).generateSchedule();
        bestSchedule = Schedules.get(0);
        Data.setSchedule(Schedules.get(0));
*/
    }
    
    public static ArrayList<Schedule> crossover(ArrayList<Schedule> sList){
        ArrayList<Schedule> crossovered = new ArrayList<>();
        for(int i = 0; i < sList.size()-1; i++)
            crossovered.add(crossover(sList.get(i), sList.get(i + 1)));
        crossovered.add(crossover(sList.get(0), sList.get(sList.size() - 1)));
        return crossovered;
    }
    
    public static ArrayList<Schedule> mutation(ArrayList<Schedule> sList){
        for(Schedule s : sList)
            s.mutation();
        return sList;
    }
    
    public static Schedule crossover(Schedule sch1, Schedule sch2){
    	Schedule child = new Schedule();
    	Random rand = new Random();
    	int crossoverBegin =  rand.nextInt(Data.getHours() - sch1.getCrossoverSize());
    	int crossoverDay = rand.nextInt(Data.getDays() + 1);
    	int day = 0, hour = 0, group = 0;
    	ArrayList<Pair> lessonList = new ArrayList<Pair>();
    	ArrayList<Pair> sch1List = new ArrayList<Pair>();
    	ArrayList<Pair> overWritten = new ArrayList<Pair>();
    	for(int i = 0; i < (Data.getDays() ) * (Data.getHours()) * Data.getGroupCount() ; i++){		
    		if(day == crossoverDay - 1  && (hour >= crossoverBegin && hour < (crossoverBegin + child.getCrossoverSize()))){	
    			child.setUsed(day, hour, group, sch2.getIsUsed()[day][hour][group]);
    			child.setSchedule(day, hour, group, sch2.getSchedule()[day][hour][group]);
    			if(child.getSchedule()[day][hour][group] != null){
    				lessonList.add(new Pair(child.getSchedule()[day][hour][group], group));
    				sch1List.add(new Pair(sch1.getSchedule()[day][hour][group], group, sch1.getUsedAt(day, hour, group)));
    			}
    			if(sch1.getUsedAt(day, hour, group) == true && sch2.getUsedAt(day, hour, group) == false){
    				Pair p = new Pair(sch1.getSchedule()[day][hour][group], group);
    				overWritten.add(p);
    				for(int j = 0; j < lessonList.size();j++)
    					if(lessonList.get(j).getLesson() == p.getLesson()){
    						overWritten.remove(p);
    						break;
    					}
    			}	
    		} 	
    		else{
	    		child.setSchedule(day, hour, group, sch1.getSchedule()[day][hour][group]);
	    		child.setUsed(day, hour, group, sch1.getIsUsed()[day][hour][group]);
    		}
    		++hour;
    		if(day == Data.getDays() - 1 && hour == Data.getHours()){
    			day = 0;
    			hour = 0;
    			++group;
    		}    			
    		if(hour == Data.getHours()){
    			hour = 0;
    			++day;
    		}
    	}
    	hour = day = group = 0;
    	for(int j = 0; j < lessonList.size(); j++){
    		for(int i = 0; i < Data.getDays() * Data.getHours(); i++){
    			if(day == crossoverDay - 1  && (hour >= crossoverBegin && hour < (crossoverBegin + child.getCrossoverSize()))){
    				
    			}
    			else{
    				if(lessonList.get(j) != null || child.getSchedule()[day][hour][lessonList.get(j).getGroup()] != null){
    					if(lessonList.get(j).getLesson() == child.getSchedule()[day][hour][lessonList.get(j).getGroup()]){
	    					child.setUsed(day, hour, lessonList.get(j).getGroup(), sch1List.get(j).isUsed());
	    					child.setSchedule(day, hour, lessonList.get(j).getGroup(), sch1List.get(j).getLesson());
	    				}
    				}   				
    			}
    			++hour;
    			if(day == Data.getDays() - 1 && hour == Data.getHours()){
        			day = 0;
        			hour = 0;
        		}    			
        		if(hour == Data.getHours()){
        			hour = 0;
        			++day;
        		}
    		}
    		day = hour = 0;
    	}
    	for(int j = 0; j < overWritten.size(); j++){
    		while(true){
    			day = rand.nextInt(Data.getDays());
    			hour = rand.nextInt(Data.getHours());
    			if(child.getUsedAt(day, hour, overWritten.get(j).getGroup()) == false){
    				child.setUsed(day, hour, overWritten.get(j).getGroup(), true);
    				child.setSchedule(day, hour, overWritten.get(j).getGroup(), overWritten.get(j).getLesson());
    				break;
    			}
    		}	
    	}
	return child;
    }
    
    private static ArrayList<Schedule> reproduction(){
        ArrayList<Schedule> tempSchedules = new ArrayList<>();
        calcTotalFitness();
        while(tempSchedules.size() < MI){
            for(Schedule s : Schedules)
                if(new Random().nextDouble() <= s.getFitness()/totalFitness)
                    tempSchedules.add(s);
        }
        return tempSchedules;
    }
    
    private static void selection(ArrayList<Schedule> sList){
        calcFitness(sList);
        selectElite(sList);
    }
    
    private static void calcFitness(ArrayList<Schedule> sList){
        for(Schedule s : sList){
            s.calcFitness();
            s.calcSoftReqValue();
        }
    }
    
    private static void selectElite(ArrayList<Schedule> sList){
        Elite.clear();
        for(int i = 0; i < Data.getSelectionParam(); i++){
            Elite.add(selectBest(sList));
        }
    }
    
    private static Schedule selectBest(ArrayList<Schedule> sList){
        Schedule best = new Schedule();
        for(Schedule s : sList)
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
        return selectBest(Schedules).getFitness();
    }
    
    private static void calcTotalFitness(){
        totalFitness = 0;
        for(Schedule s : Schedules)
            totalFitness += s.getFitness();
    }
    
    private static double calcTotalFitness(ArrayList<Schedule> sList){
        double fitness = 0;
        for(Schedule s : sList)
            fitness += s.getFitness();
        return fitness;
    }
    
    public static ArrayList<Schedule> getSchedules(){
        return Schedules;
    }
    
    public static Schedule getBestSchedule(){
        return bestSchedule;
    }
    
}