# Student Management System

A simple Java application for managing student records with a graphical user interface using the terminal, this will be later updated to a GUI in the first GUI class in a week;

## Features

* Add new students with ID, name, and grade
* Update student grades
* Remove students from the system
* Display all student records
* Data validation for all inputs
* User-friendly GUI interface
* Error handling and user feedback

## Requirements

* Java Development Kit (JDK) 8 or higher
* Java Runtime Environment (JRE)

## How to Run

1. Open the project in intellij IDEA or any other IDE that supports Java

2. Click on the green play button to run the program on the main class

## Features in Detail

### Add Student
* Validates student ID for uniqueness
* Ensures grade is between 0 and 100
* Prevents empty name entries

### Update Grade
* Verifies student existence by ID
* Validates new grade range
* Provides success/failure feedback

### Remove Student
* Confirms student existence before removal
* Provides feedback on operation success

### Display Students
* Shows formatted list of all students
* Displays ID, name, and grade for each student
* Handles empty list scenario

## Error Handling

The application includes comprehensive error handling for:
* Invalid numeric inputs
* Out-of-range grades
* Duplicate student IDs
* Empty student list operations
* Cancel operations

## Future Improvements

Potential enhancements could include:
* Graphical user interface (GUI) implementation
* Data persistence (file/database storage)
* More detailed student information
* Sorting and filtering capabilities
* Advanced search functionality
* Student grade statistics
* Export functionality

## Contributing

This is a simple educational project, but suggestions for improvements are welcome!