import java.util.ArrayList;
import java.util.Scanner;

public class StudentManagement {
    private ArrayList<Student> studentList;
    private Scanner scanner;
    
    public StudentManagement(){
        studentList = new ArrayList<>();
        scanner = new Scanner(System.in);
    }
    
    public boolean addStudent(Student student){
        studentList.add(student);
        System.out.println("Student added successfully: " + student);
        return true;
    }
    
    public boolean addStudent(){
        System.out.println("Enter the student name: ");
        String name = scanner.nextLine();
        
        int id = getValidIntInput("Enter the student ID: ");
        
        if (findStudent(id) != null) {
            System.out.println("Error: a student with id "+ id + " already exists");
            return false;
        }
        
        double grade = getValidDoubleInput("Enter the student grade: ");
        
        if (grade < 0 || grade > 100){
            System.out.println("Error: Grade must be between 0 and 100");
            return false;
        }
        
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
    
    public boolean updateGrade(){
        int id  = getValidIntInput("Enter student ID: ");
        Student student = findStudent(id);
        if (student != null) {
            double newGrade = getValidDoubleInput("Enter the new grade: ");
            double previousGrade = student.getGrade();
            if (newGrade < 0 || newGrade > 100){
                System.out.println("Error: Grade must be between 0 and 100");
                return false;
            }
            student.setGrade(newGrade);
            System.out.println("Grade updated successfully for " + student + " from "+ previousGrade + " to " + newGrade);
            return true;
        }
       return false; 
    }
    
    public boolean removeStudent(){
        int id  = getValidIntInput("Enter student ID: ");
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
    
    //Helpers
    private int getValidIntInput(String prompt){
        while (true) {
            try {
                System.out.println(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid integer");
            }
        }
    }
    private double getValidDoubleInput(String prompt){
        while (true) {
            try {
                System.out.println(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid integer");
            }
        }
    }
    
    //display menu
    public void displayMenu(){
        System.out.println("Welcome to the student management system");
        System.out.println("1. Add a student");
        System.out.println("2. Update student grade");
        System.out.println("3. Remove a student");
        System.out.println("4. Display all students");
        System.out.println("5. Exit");
    }
    
    //run method
    public void run(){
        while (true) {
            displayMenu();
            int choice = getValidIntInput("Enter your choice: \n");
            
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    updateGrade();
                    break;
                case 3:
                    removeStudent();
                    break;
                case 4:
                    displayAllStudents();
                    break;
                case 5:
                    System.out.println("Exiting the system...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5");
            }     
        }
    }
}