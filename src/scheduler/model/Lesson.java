
package scheduler.model;

public class Lesson {
    private Course course;
    private Group group;
    private Professor professor;
    private Room room;
    
    public Lesson(){
    this(null, null, null, null);
    }
    
    public Lesson(Course course, Group group, Professor professor, Room room){
        this.course = course;
        this.group = group;
        this.professor = professor;
        this.room = room;
    }
    
    public Lesson(int courseId, int groupId, int professorId, String roomId){
        this.course = new Course(courseId);
        this.group = new Group(groupId);
        this.professor = new Professor(professorId);
        this.room = new Room(roomId);
    }
    
    public Course getCourse(){
        return this.course;
    }
    
    public Group getGroup(){
        return this.group;
    }
    
    public Professor getProfessor(){
        return this.professor;
    }
    
    public Room getRoom(){
        return this.room;
    }
    
    public void setRoom(Room room){
        this.room = room;
    }
    
    public String getLesson(){
        StringBuilder lesson = new StringBuilder();
        lesson.append(course.getName().get());
        lesson.append(" ");
        lesson.append(professor.getName().get());
        lesson.append(" ");
        lesson.append(professor.getSurname().get());
        lesson.append(" ");
        lesson.append(room.getId().get());
        return lesson.toString();
    }
    
    public void loadCourse(Course course){
        this.course.setName(course.getName());
    }
    
    public void loadGroup(Group group){
        this.group.setSemester(group.getSemester());
        this.group.setSize(group.getSize());
    }
    
    public void loadProfessor(Professor professor){
        this.professor.setName(professor.getName());
        this.professor.setSurname(professor.getSurname());
    }
    
    public void loadRoom(Room room){
        this.room.setSize(room.getSize());
    }
}
