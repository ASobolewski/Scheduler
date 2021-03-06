package scheduler.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Room {
    private static int idCounter;
    private IntegerProperty  id;
    private IntegerProperty size;
    
    public Room(){
        this(0);
    }
    public Room(int size){
        this.id = new SimpleIntegerProperty(++idCounter);
        this.size = new SimpleIntegerProperty(size);
    }
    
    public Room(String id){
        this.id = new SimpleIntegerProperty(Integer.parseInt(id));
        this.size = new SimpleIntegerProperty(0);
    }
    
    public IntegerProperty getId(){
        return id;
    }
    
    public IntegerProperty getSize(){
        return size;
    }
    
    public void setSize(IntegerProperty size){
        this.size = size;
    }
    
}
