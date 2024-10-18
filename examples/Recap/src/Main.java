import javax.swing.*; // Importing Swing components for GUI
import java.util.Scanner; // Importing Scanner for console input

class Main {
    // Main class containing the entry point of the application

    /**
     * Entry point of the application
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Creating a Scanner object for console input
        register("Juma", "password"); // Registering a user with predefined credentials
        // Prompting the user for their first name using a dialog box
        String firstName = JOptionPane.showInputDialog(null, "Enter your first name");
        String lastName = "Victor"; // Defining a last name
        String fullName = firstName + " " + lastName; // Concatenating first and last names

        // Printing the full name to the console
        System.out.println(fullName);
    }

    /**
     * Registers a user with the given name and password
     * @param name the name of the user
     * @param password the password of the user
     */
    static void register(String name, String password) {
        User registeringUser = new User(); // Creating a new User object
        registeringUser.name = name; // Setting the user's name
        registeringUser.setPassword(password); // Setting the user's password

        // Printing the user details to the console
        System.out.println(registeringUser);
    }
}

class User {
    // User class representing a user with a name and password

    public String name; // Public field for the user's name
    private String password; // Private field for the user's password

    /**
     * Sets the user's password
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password; // Assigning the password to the private field
    }

    /**
     * Provides a custom string representation of the User object
     * @return a string representation of the User object
     */
    @Override
    public String toString() {
        return "User: name = " + name + ", password = " + password; // Returning a formatted string
    }
}