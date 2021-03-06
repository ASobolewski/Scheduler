package scheduler.model;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.chart.XYChart;

public class Algorithm {
    private static ArrayList<Schedule> Schedules;
    private static ArrayList<Schedule> Elite;
    private static Schedule bestSchedule;
    private static final int MI = 20;
    private static final int LAMBDA = 10;
    private static double totalFitness;
    private static boolean b;

    public static boolean start(){
        if(!checkSolutionExistence())
            return false;
        int iterations = 0;
        ArrayList<Schedule> tempSchedules;
        ArrayList<Schedule> offspring;
        Schedules = new ArrayList<>();
        Elite = new ArrayList<>();
        
    //inicjacja populacji poczatkowej
        for(int i = 0; i < MI; i++){
            Schedule s = new Schedule();
            s.generateSchedule();
            Schedules.add(s);
        }

    //ocena populacji poczatkowej
        calcFitness(Schedules);
        System.out.println(getBestFitness());
        while(getBestFitness() < 1.0)
        {
            //System.out.println("reproduction()");
            tempSchedules = reproduction();
            checkB(tempSchedules);
            //System.out.println("crossover()");
            if(!b){
            tempSchedules = crossover(tempSchedules);
            //System.out.println("mutation()");
            offspring = mutation(tempSchedules);
            //System.out.println("selection()");
            }
            else{
            offspring = mutationB(tempSchedules);
            }
            selection(offspring);
            Schedules = Elite;
            iterations++;
            
            Data.series.getData().add(new XYChart.Data(iterations, bestSchedule.getFitness()));
            System.out.println(bestSchedule.getFitness());
            System.out.println(iterations);
            System.out.println(b);
        }
        Data.setSchedule(selectBest(Schedules));
        return true;
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
    
    public static ArrayList<Schedule> mutationB(ArrayList<Schedule> sList){
       Random rand = new Random();
        for(Schedule s : sList)
            for(int group = 0; group < Data.getGroupCount(); group++)
                for(int day = 0; day < Data.getDays(); day++)
                    for(int hour = 0; hour < Data.getHours(); hour++)
                        if(s.getSchedule()[day][hour][group] != null)
                            s.mutation(s.getSchedule()[day][hour][group], rand, day, hour);
        return sList;
    }
    
    private static void checkB(ArrayList<Schedule> sList){
        for(Schedule s : sList)
            if(s.b == false){
                b = false;
                return;
            }
        b = true;
    }
    
    private static void selection(ArrayList<Schedule> sList){
        calcFitness(sList);
        selectElite(sList);
    }
    
    public static Schedule crossover(Schedule sch1, Schedule sch2){
    	Schedule child = new Schedule();
    	Random rand = new Random();
    	int crossoverBegin =  rand.nextInt(Data.getHours() - sch1.getCrossoverSize());
    	int crossoverDay = rand.nextInt(Data.getDays() + 1);
    	int size = rand.nextInt(Data.getGroupCount());
    	int crossoverGroup = rand.nextInt(Data.getGroupCount() - size);
    	int day = 0, hour = 0, group = 0;
    	ArrayList<Pair> lessonList = new ArrayList<Pair>();
    	ArrayList<Pair> sch1List = new ArrayList<Pair>();
    	ArrayList<Pair> overWritten = new ArrayList<Pair>();
    	for(int i = 0; i < (Data.getDays() ) * (Data.getHours()) * Data.getGroupCount() ; i++){		
    		if(day == crossoverDay - 1  && (hour >= crossoverBegin && hour < (crossoverBegin + child.getCrossoverSize()) && (group >= crossoverGroup && group < (crossoverGroup + size)))){	
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
    
    
    private static void calcFitness(ArrayList<Schedule> sList){
        for(Schedule s : sList){
            s.calcFitness();
            s.calcSoftReqValue();
        }
    }
    
    private static void selectElite(ArrayList<Schedule> sList){
        Elite.clear();
        bestSchedule = selectBest(sList);
        for(int i = 0; i < LAMBDA; i++){
            Elite.add(selectBest(sList));
            sList.remove(selectBest(sList));
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
    
    private static boolean checkSolutionExistence(){
        if(!checkGroupLessons())
            return false;
        if(!checkRoomLessons())
            return false;
        if(!checkProfessorLessons())
            return false;
        return true;
    }
    
    private static boolean checkGroupLessons(){
        int check = 0;
        for(Group g : Data.getGroups()){
            for(Lesson l : Data.getLessons()){
                if(l.getGroup().getId().get() == g.getId().get())
                    check++;
            }
        if(check > Data.getDays()*Data.getHours())
            return false;
        check =0;
        }
        return true;
    }
    
    private static boolean checkRoomLessons(){
        if(Data.getRooms().size() * Data.getDays() * Data.getHours() < Data.getLessons().size())
            return false;
        return true;
    }
    
    private static boolean checkProfessorLessons(){
        int check = 0;
        for(Professor p: Data.getProfessors()){
            for(Lesson l : Data.getLessons()){
                if(l.getProfessor() == p)
                    check++;
            }
        if(check > Data.getDays()*Data.getHours())
            return false;
        check =0;
        }
        return true;
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