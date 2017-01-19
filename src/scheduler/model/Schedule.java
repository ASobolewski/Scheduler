package scheduler.model;

import java.util.Random;

public class Schedule {
    private final Lesson[][][] schedule;
    private final boolean[][][] isUsed;
    private final boolean[][][] isRoomUsed;
    private double fitness;
    private int softRequirementsValue;
    private final int mutationSize;
    private final int crossoverSize;
    private final int mutationProbability;
    private final int crossoverProbability;
    public boolean b = false;
    
    public Schedule(){
        this.schedule = new Lesson[Data.getDays()][Data.getHours()][Data.getGroupCount()];
        this.isUsed = new boolean[Data.getDays()][Data.getHours()][Data.getGroupCount()];
        this.isRoomUsed = new boolean[Data.getDays()][Data.getHours()][Data.getRooms().size()];
        this.mutationSize = 50;
        this.crossoverSize = 3;
        this.mutationProbability = 20;
        this.crossoverProbability = 20;
        this.fitness = 0;
        this.softRequirementsValue = 0;
    }
    
    public void generateSchedule(){
        int day;
        int hour;
        Random rand = new Random();
        for(Lesson lesson : Data.getLessons()){
            do{
                day = rand.nextInt(Data.getDays());
                hour = rand.nextInt(Data.getHours());
            }while(isUsed[day][hour][lesson.getGroup().getId().get() - 1]);
            schedule[day][hour][lesson.getGroup().getId().get()-1] = lesson;
            isUsed[day][hour][lesson.getGroup().getId().get()-1] = true;
        }
        /*int day = 0;
        int hour = 0;
        for(Lesson lesson : Data.getLessons()){
            day = 0;
            hour = 0;
            do{
                if(hour < 5)
                    hour++;
                else{
                    hour = 0;
                    if(day < 4)
                        day++;
                }
            }while(isUsed[day][hour][lesson.getGroup().getId().get() - 1]);
            schedule[day][hour][lesson.getGroup().getId().get()-1] = lesson;
            isUsed[day][hour][lesson.getGroup().getId().get()-1] = true;
        }*/
    }
    
    public void calcFitness(){
        int fitness = 0;
        int professors = 0;
        int rooms1 = 0;
        int rooms2 = 0;
        for(int i = 0; i < Data.getDays(); i++)
            for(int j = 0; j < Data.getHours(); j++){
                int professor = checkProfessorAvailability(i, j);
                fitness += professor;
                professors += professor;
                //System.out.print("professor = "+professor);
                int room1 = checkRoomAvailability(i, j);
                fitness += room1;
                rooms1 += room1;
                //System.out.print(" room1 = "+room1+"\n");
                int room2 = checkRoomSize(i, j);
                fitness += room2;
                rooms2 += room2;
                //System.out.print(" room2 = "+room2+"\n");
            }
        if(professors == Data.getDays()*Data.getHours())
            b = true;
        else
            b = false;
        System.out.println("professors = "+professors+" rooms1 = "+rooms1+" rooms2 = "+rooms2);
        this.fitness = (double)fitness / (Data.getDays() * Data.getHours() * 3 );
    }

     private int checkProfessorAvailability(int day, int hour){
        int check = 0;
        for(Professor professor : Data.getProfessors()){
            for(int group = 0; group < Data.getGroupCount(); group++){
                if(getIsUsed()[day][hour][group] && professor.getId().get() == schedule[day][hour][group].getProfessor().getId().get())
                {
                    check++;
                    //if(check>=2)
                        //System.out.println("professor 1 = "+professor.getId().get()+"professor 2 = "+schedule[day][hour][group].getProfessor().getId().get()+"\n");
                }
            }
            if(check > 1)
                return 0;
            check = 0;
        }
        return 1;
    }
    
    private int checkRoomAvailability(int day, int hour){
        int check = 0;
        for(Room room : Data.getRooms()){
            for(int group = 0; group < Data.getGroupCount(); group++){
                if(getIsUsed()[day][hour][group] && room.getId().get() == schedule[day][hour][group].getRoom().getId().get()){
                    check++;
                    //System.out.print("room id = "+room.getId().get()+" room2 id = "+schedule[day][hour][group].getRoom().getId().get()+" check = "+check+"\n");
                    //System.out.print("room id = "+room.getId().get()+" day = "+day+" hour = "+hour+"\n");
                }
        
            }
            if(check > 1){
                return 0;
            }
                
            check = 0;
        }
        return 1;
    }
    
    private int checkRoomSize(int day, int hour){
        int check = 0;
        for(int group = 0; group < Data.getGroupCount(); group++){
            //if(schedule[day][hour][group] != null)System.out.print(" group.size() = "+schedule[day][hour][group].getGroup().getSize().get()+" room.size() = "+schedule[day][hour][group].getRoom().getSize().get()+"\n");
            if(getIsUsed()[day][hour][group] && (schedule[day][hour][group].getGroup().getSize().get() > schedule[day][hour][group].getRoom().getSize().get()))
            {
                return 0;
            }
        }
        return 1;
    }
    
    public void calcSoftReqValue(){
        int value = 0;
        value += calcSoftStudentsReq();
        value+= calcSoftEveningReq();
        this.softRequirementsValue = value;     
    }
    
    //Grupy studenckie chcialyby konczyc w piatki  najpozniej o 14
    private int calcSoftStudentsReq(){
        int value = 0;
        for(int i = 0; i < Data.getGroupCount(); i++)
            if(schedule[4][3][i] == null)
                if(schedule[4][4][i] == null)
                    if(schedule[4][5][i] == null)
                        value++;
        return value;
    }
    
    //Zajecia najpozniej do godziny 18
    private int calcSoftEveningReq(){
        int value = 0;
        for(int i = 0; i < Data.getGroupCount(); i++)
            for(int j = 0; j < Data.getDays(); j++)
                if(schedule[j][5][i] == null)
                    value++;
        return value;
    }
    
   

    public double getFitness(){
        return fitness;
    }
    
    public int getSoftReqValue(){
        return softRequirementsValue;
    }
        
    public void mutation(){
    	Random rand = new Random();   
    	if(rand.nextInt(100) < this.mutationProbability){
	        for(int i = 0; i < this.mutationSize; i++){
	        	int a = rand.nextInt(Data.getDays());
	        	int b = rand.nextInt(Data.getHours());
	        	int c = rand.nextInt(Data.getDays());
	        	int d = rand.nextInt(Data.getHours());
	        	int k = rand.nextInt(Data.getGroupCount());
	        	if(isUsed[a][b][k] == true || isUsed[c][d][k] == true){
		        	Lesson tmp = schedule[a][b][k];
		        	schedule[a][b][k] = schedule[c][d][k];
		        	schedule[c][d][k] = tmp;
		        	if(schedule[c][d][k] != null){
		        		mutation(schedule[c][d][k], rand, c, d);
		        		isUsed[c][d][k] = true;
		        	}
		        	else
		        		isUsed[c][d][k] = false;
		        	if(schedule[a][b][k] != null){
		        		mutation(schedule[a][b][k], rand, a , b);
		        		isUsed[a][b][k] = true;
		        	}
		        	else
		        		isUsed[a][b][k] = false;
	        	}   
	        }
    	}
    }
    
    public void mutation(Lesson l, Random rand, int day, int hour){
    	int id = Data.getRooms().get(rand.nextInt(10)).getId().get();
    	if(this.isRoomUsed(day, hour, id) == false && l.getGroup().getSize().get() <= Data.getRooms().get(id - 1).getSize().get()){
    		this.setRoomUsed(day, hour, id - 1, true);
    		l.setRoom(Data.getRooms().get(id - 1));
    	}
    	else{

    		while(this.isRoomUsed(day, hour, id) == true){
    			id = Data.getRooms().get(rand.nextInt(10)).getId().get();
    		}
    		for(int i = 0; i < Data.getGroupCount(); i++){
    			if(l.getGroup().getSize().get() <= Data.getRooms().get((id + i- 1) % Data.getRooms().size() ).getSize().get() && schedule[day][hour][(l.getGroup().getId().get() + 1) % (Data.getGroupCount() - 1)] != null &&
                                schedule[day][hour][(l.getGroup().getId().get() + 1) % (Data.getGroupCount() - 1)].getGroup().getSize().get() <= Data.getRooms().get(id - 1).getSize().get()){
    				Room tmp = l.getRoom();
    				l.setRoom(Data.getRooms().get((id + i - 1) % Data.getRooms().size()));
    				schedule[day][hour][(l.getGroup().getId().get() + 1) % (Data.getGroupCount() - 1)].setRoom(tmp);
                                return;
    			}	
    		}
    	}
    }
    
    public void checkUsedRooms(){
        for(int day = 0; day < Data.getDays(); day++)
            for(int hour = 0; hour < Data.getHours(); hour++)
                for(int room = 0; room < Data.getRooms().size(); room++)
                    isRoomUsed[day][hour][room] = false;
        for(int day = 0; day < Data.getDays(); day++)
            for(int hour = 0; hour < Data.getHours(); hour++)
                for(int group = 0; group < Data.getGroupCount(); group++)
                    if(schedule[day][hour][group] != null)
                        isRoomUsed[day][hour][schedule[day][hour][group].getRoom().getId().get()-1] = true;
    }
    
    public boolean isRoomUsed(int day, int hour, int roomId){
        checkUsedRooms();
        return isRoomUsed[day][hour][roomId-1];
    }
    
    public void setRoomUsed(int day, int hour, int id, boolean b){
        isRoomUsed[day][hour][id] = b;
    }
    
    public Lesson[][][] getSchedule(){
        return schedule;
    }
    public int getCrossoverSize(){
    	return this.crossoverSize;
    }
    public void setSchedule(int day, int hour, int group, Lesson l){
    	this.schedule[day][hour][group] = l;
    }
    public boolean[][][] getIsUsed(){
        return isUsed;
    }
    public boolean getUsedAt(int day, int hour, int group){
    	return isUsed[day][hour][group];
    }
    public void setUsed(int day, int hour, int group, boolean value){
    	isUsed[day][hour][group] = value;
    }
}