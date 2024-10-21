import java.util.ArrayList;

public class StudentManagement {
    private ArrayList<Student> studentList;
    
    public StudentManagement(){
        studentList = new ArrayList<>();
    }
    
    public boolean addStudent(Student student){
        studentList.add(student);
        return true;
    }
    
    public boolean addStudent(String name, int id, double grade){
        Student currentStudent = new Student(name, id, grade);
        return addStudent(currentStudent);
    }
    
    public Student findStudent(int id){
        for (Student student : studentList) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
    
    public boolean updateGrade(int id, double newGrade){
        Student student = findStudent(id);
        if (student != null) {
            double previousGrade = student.getGrade();
            student.setGrade(newGrade);
            System.out.println("Grade updated successfully for " + student + " from "+ previousGrade + " to " + newGrade);
            return true;
        }
       return false; 
    }
    
    public boolean removeStudent(int id){
        Student student = findStudent(id);
        if (student != null) {
            studentList.remove(student);
            return true;
        }
        System.out.println("Student with ID: " + id + " not found");
        return false;
    }
    
    public void displayAllStudents(){
        if (studentList.isEmpty()) {
            System.out.println("No students yet in the system");
        }else {
            for (Student student : studentList) {
                System.out.println(student);
            }
        }
    }
}