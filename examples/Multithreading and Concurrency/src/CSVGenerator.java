import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class CSVGenerator {
    public static void main(String[] args) {
        try {
            PrintWriter writer = 
                    new PrintWriter(new FileWriter("phone_numbers.csv"));
            
            Random random = new Random();
            String[] firstNames = {"John", "Jane", "Alice", "Bob", "Charlie", "Steve"};
            String[] lastNames = {"Doe", "Smith", "Johnson", "Brown", "Lee", "Davis"};

            for (int i = 1; i <= 1_000_000 ; i++) {
                String name = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
                String phoneNumber;
                
                //chance
                if (random.nextDouble() > 0.8) {
                    phoneNumber = String.format("%03d-%03d-%04d",
                            random.nextInt(1000),
                            random.nextInt(1000),
                            random.nextInt(10000));
                }else {
                    phoneNumber = String.format("%d-%d-%d",
                            random.nextInt(10),
                            random.nextInt(10),
                            random.nextInt(10000));
                }
                
                writer.printf("%d,%s,%s%n", i, name, phoneNumber);
            }

            System.out.println("Generated users");
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
