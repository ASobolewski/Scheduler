package scheduler.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Course {
    private static int idCounter;
    private IntegerProperty id;
    private StringProperty name;
    
    public Course(){
        this(null);
    }
    
    public Course(String name){
        this.id = new SimpleIntegerProperty(++idCounter);
        this.name = new SimpleStringProperty(name);
    }
    
    public Course(int id){
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(null);
    }
    
    public StringProperty getName(){
        return name;
    }
    
    public IntegerProperty getId(){
        return id;
    }
    
    public void setName(StringProperty name){
        this.name = name;
    }
}
