/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import scheduler.model.Data;
import scheduler.model.Schedule;

/**
 *
 * @author Administrator
 */
public class filewriteUtil {
    
    public static void writeToFile(Schedule s) throws FileNotFoundException{
        PrintWriter writer =  null;
        try{
        writer = new PrintWriter(new File("schedule.csv"));
        } 
        catch (FileNotFoundException e){
        e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        
        sb.append("Day");
        sb.append(',');
        sb.append("Hour");
        sb.append(',');
        sb.append("Group");
        sb.append(',');
        sb.append("Professor");
        sb.append(',');
        sb.append("Room Id");
        sb.append(',');
        sb.append("Course");
        sb.append(',');
        sb.append("Room size");
        sb.append(',');
        sb.append("Group size");
        sb.append('\n');
        
       for(int day = 0; day < Data.getDays(); day++)
            for(int hour = 0; hour < Data.getHours(); hour++)
                for(int group = 0; group < Data.getGroupCount(); group++){
                    if(s.getSchedule()[day][hour][group] != null){
                        sb.append(Integer.toString(day+1));
                        sb.append(',');
                        sb.append(Integer.toString(hour+1));
                        sb.append(',');
                        sb.append(Integer.toString(group+1));
                        sb.append(',');
                        sb.append(Integer.toString(s.getSchedule()[day][hour][group].getProfessor().getId().get()));
                        sb.append(',');
                        sb.append(Integer.toString(s.getSchedule()[day][hour][group].getRoom().getId().get())); 
                        sb.append(',');
                        sb.append(s.getSchedule()[day][hour][group].getCourse().getName().get());
                        sb.append(',');
                        sb.append(Integer.toString(s.getSchedule()[day][hour][group].getRoom().getSize().get())); 
                        sb.append(',');
                        sb.append(Integer.toString(s.getSchedule()[day][hour][group].getGroup().getSize().get()));
                        sb.append('\n');
                    }

                }
        writer.write(sb.toString());
        writer.close();
                    
    }
}
