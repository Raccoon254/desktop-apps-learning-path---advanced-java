import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class PhoneNumberProcessorComparison {
    private static Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");


    public static void main(String[] args) {
        String inputFile = "phone_numbers.csv";
        String multiThreadedOutput = "multi_threaded_output.csv";
        String singleThreadedOutput = "single_threaded_output.csv";
        
        try {
            List<String> allLines = readCSV(inputFile);
            List<String> dataLines = allLines.subList(1, allLines.size());
            
            //Run and time single threaded processing
            SingleThreadProcessor singleThreadProcessor = new SingleThreadProcessor();
            long singleThreadTime = singleThreadProcessor.processData(dataLines, singleThreadedOutput);
            
            //Run the multithreaded processing
            MultiThreadProcessor multiThreadProcessor = new MultiThreadProcessor();
            long multiThreadTime = multiThreadProcessor.processData(dataLines, multiThreadedOutput);
            
            //Process comparison
            System.out.println("\n\nProcessing comparison:");
            System.out.println("Single threaded time: " + singleThreadTime + "ms");
            System.out.println("Multi threaded time: " + multiThreadTime + "ms");
            System.out.printf("%.2f", (double) singleThreadTime / multiThreadTime);
                    
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<String> readCSV(String inputFile) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lines;
    }


    static class SingleThreadProcessor{
        private int invalidCount = 0;
        private int validCount = 0;
        
        public long processData(List<String> lines, String outputFile){
            System.out.println("\nStarting the single threaded processing....");
            long startTime = System.currentTimeMillis();
            
            try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                //Write the header names for columns
                writer.println("Id,name,phone_number,is_valid");
                
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String phoneNumber = parts[2].trim();
                        boolean isValid = PHONE_PATTERN.matcher(phoneNumber).matches();
                        
                        if (isValid) validCount ++;
                        else invalidCount ++;
                        
                        String res = String.format("%s,%s,%s,%s", parts[0], parts[1], phoneNumber, isValid);
                        writer.println(res);

                        System.out.printf("Single thread processed: %s -> %s%n", phoneNumber, isValid);
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Error processing single thread: " + e.getMessage());
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\nSingle threaded processing completed!");
            System.out.println("Valid phone numbers: " + validCount);
            System.out.println("Invalid phone numbers: " + invalidCount);
            System.out.println("Total number of lines processed: " + (validCount + invalidCount));
            System.out.println("Time taken: " + duration + "ms");
            
            return duration;
        }
    }
    
    static class MultiThreadProcessor{
        private BlockingDeque<String> processedResults = new LinkedBlockingDeque<>();
        private AtomicInteger validNumbers = new AtomicInteger(0);
        private AtomicInteger invalidNumbers = new AtomicInteger(0);

        public long processData(List<String> lines, String outputFile) {
            System.out.println("\nStarting multi-threaded processing....");
            long startTime = System.currentTimeMillis();
            
            int numThreads = 3;
            int linesPerThread = (int) Math.ceil((double) lines.size() /numThreads);

            //Create a thread pool for writer threads
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<?>> futures = new ArrayList<>();
            
            
            //Writer thread
            Thread writerThread = new Thread(new ResultWriter(outputFile));
            writerThread.start();

            for (int i = 0; i < numThreads; i++) {
                int startIndex = i * linesPerThread;
                int endIndex = Math.min((i + 1) * linesPerThread, lines.size());
                
                if (startIndex < lines.size()) {
                    List<String> threadLines = lines.subList(startIndex, endIndex);
                    futures.add(executor.submit(new NumberValidator(threadLines, i + 1)));
                }
            }
            
            //Wait for all threads to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            
            executor.shutdown();
            processedResults.add("END");
            try {
                writerThread.join();
            }catch (Exception e) {}
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\nMulti-threaded processing completed!");
            System.out.println("Valid phone numbers: " + validNumbers.get());
            System.out.println("Invalid phone numbers: " + invalidNumbers.get());
            System.out.println("Total number of lines processed: " + (validNumbers.get() + invalidNumbers.get()));
            
            return duration;
        }
        
        class NumberValidator implements Runnable{
            List<String> lines;
            int threadId;
            
            public NumberValidator(List<String> lines, int threadId) {
                this.lines = lines;
                this.threadId = threadId;
            }
            
            @Override
            public void run(){
                for (String line : lines) {
                    try {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            String phoneNumber = parts[2].trim();
                            boolean isValid = PHONE_PATTERN.matcher(phoneNumber).matches();

                            if (isValid) validNumbers.incrementAndGet();
                            else invalidNumbers.incrementAndGet();

                            String res = String.format("%s,%s,%s,%s", parts[0], parts[1], phoneNumber, isValid);
                            processedResults.add(res);

                            System.out.printf("Thread %d processed: %s -> %s%n", threadId, phoneNumber, isValid);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            
        }
        
        class ResultWriter implements Runnable {
            private String outPutFile;
            
            public ResultWriter(String outputFile) {
                this.outPutFile = outputFile;
            }
            
            @Override
            public void run(){
                try (PrintWriter writer = new PrintWriter(new FileWriter(outPutFile))){
                    writer.println("Id,name,phone_number,is_valid");
                    
                    while (true) {
                        String line = processedResults.take();
                        if (line.equals("END")) break;
                        writer.println(line);
                    }
                }catch (Exception e){}
            }
        }
        
    }
}
