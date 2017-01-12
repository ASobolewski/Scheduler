package scheduler.model;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.filereadUtil;


public class Data {
    private static final ObservableList<Lesson> Lessons  = FXCollections.observableArrayList();
    private static final ObservableList<Course> Courses = FXCollections.observableArrayList();
    private static final ObservableList<Group> Groups = FXCollections.observableArrayList();
    private static final ObservableList<Professor> Professors = FXCollections.observableArrayList();
    private static final ObservableList<Room> Rooms = FXCollections.observableArrayList();
    private static Schedule schedule;
    private static int groupCount;

    private static final int DAYS = 5;
    private static final int HOURS = 6; 
    
    
    public static void initData(){
        Professors.addAll(filereadUtil.readProfessors());
        Courses.addAll(filereadUtil.readCourses());
        Groups.addAll(filereadUtil.readGroups());
        Rooms.addAll(filereadUtil.readRooms());
        Lessons.addAll(filereadUtil.readLessons());
       
        filereadUtil.loadLessons(Lessons, Courses, Groups, Professors, Rooms);
        
        groupCount = filereadUtil.getGroupCount();
    }
    
    public static ObservableList<Lesson> getLessons(){
        return Lessons;
    }
    
    public static ObservableList<Course> getCourses(){
        return Courses;
    }
    
    public static ObservableList<Group> getGroups(){
        return Groups;
    }
    
    public static ObservableList<Professor> getProfessors(){
        return Professors;
    }
    
    public static ObservableList<Room> getRooms(){
        return Rooms;
    }
    
    public static int getGroupCount(){
        return groupCount;
    }
    
    public static int getDays(){
        return DAYS;
    }
    
    public static int getHours(){
        return HOURS;
    }
    

    public static ObservableList<String> getGroupList(){
        ObservableList<String> groupList  = FXCollections.observableArrayList();
        for(int i = 1; i <= getGroupCount(); i++)
            groupList.add(Integer.toString(i));
        return groupList;
    }
    
    public static void setSchedule(Schedule schedule){
        Data.schedule = schedule;
    }
    
    public static Schedule getSchedule(){
        return schedule;
    }
}