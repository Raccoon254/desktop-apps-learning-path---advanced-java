import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class PhoneNumberProcessorComparison {
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    public static void main(String[] args) {
        String inputFile = "phone_numbers.csv";
        String multiThreadOutput = "multi_threaded_results.csv";
        String singleThreadOutput = "single_threaded_results.csv";

        try {
            // Read data once for both processes
            List<String> allLines = readCSV(inputFile);
            List<String> dataLines = allLines.subList(1, allLines.size());

            // Run and time single-threaded process
            SingleThreadProcessor singleProcessor = new SingleThreadProcessor();
            long singleThreadTime = singleProcessor.processData(dataLines, singleThreadOutput);

            // Run and time multithreaded process
            MultiThreadProcessor multiProcessor = new MultiThreadProcessor();
            long multiThreadTime = multiProcessor.processData(dataLines, multiThreadOutput);

            // Print comparison
            System.out.println("\nProcessing Comparison:");
            System.out.println("Single Thread Time: " + singleThreadTime + " ms");
            System.out.println("Multi Thread Time: " + multiThreadTime + " ms");
            System.out.println("Speed Improvement: " +
                    String.format("%.2f", (float)singleThreadTime/multiThreadTime) + "x");

        } catch (Exception e) {
            System.err.println("Error in main process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Single-threaded processor
    static class SingleThreadProcessor {
        private int validCount = 0;
        private int invalidCount = 0;

        public long processData(List<String> lines, String outputFile) {
            System.out.println("\nStarting Single-threaded Processing...");
            long startTime = System.currentTimeMillis();

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                // Write header
                writer.println("id,name,phone_number,is_valid");

                // Process each line
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String phoneNumber = parts[2].trim();
                        boolean isValid = PHONE_PATTERN.matcher(phoneNumber).matches();

                        if (isValid) validCount++;
                        else invalidCount++;

                        String result = String.format("%s,%s,%s,%s",
                                parts[0], parts[1], phoneNumber, isValid);
                        writer.println(result);

//                        System.out.printf("Single thread processed: %s -> %s%n", phoneNumber, isValid);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error in single thread process: " + e.getMessage());
            }

            long duration = System.currentTimeMillis() - startTime;

            System.out.println("\nSingle Thread Processing Complete!");
            System.out.println("Valid numbers: " + validCount);
            System.out.println("Invalid numbers: " + invalidCount);
            System.out.println("Total processed: " + (validCount + invalidCount));
            System.out.println("Time taken: " + duration + " ms");

            return duration;
        }
    }

    // Multi-threaded processor
    static class MultiThreadProcessor {
        private final BlockingQueue<String> processedResults = new LinkedBlockingQueue<>();
        private final AtomicInteger validNumbers = new AtomicInteger(0);
        private final AtomicInteger invalidNumbers = new AtomicInteger(0);

        public long processData(List<String> lines, String outputFile) throws Exception {
            System.out.println("\nStarting Multi-threaded Processing...");
            long startTime = System.currentTimeMillis();

            int numThreads = 2;
            int totalNumberOfLines = 1_000_000;
            System.out.println("Number of threads: " + numThreads);
            int linesPerThread = (int) Math.ceil(totalNumberOfLines / (double) numThreads);

            // Create thread pool
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<?>> futures = new ArrayList<>();

            // Start writer thread
            Thread writerThread = new Thread(new ResultWriter(outputFile));
            writerThread.start();

            // Submit tasks
            for (int i = 0; i < numThreads; i++) {
                int startIndex = i * linesPerThread;
                int endIndex = Math.min((i + 1) * linesPerThread, lines.size());

                if (startIndex < lines.size()) {
                    List<String> threadLines = lines.subList(startIndex, endIndex);
                    futures.add(executor.submit(new PhoneNumberValidator(threadLines, i + 1)));
                }
            }

            // Wait for all tasks
            for (Future<?> future : futures) {
                future.get();
            }

            // Shutdown
            executor.shutdown();
            processedResults.put("END");
            writerThread.join();

            long duration = System.currentTimeMillis() - startTime;

            System.out.println("\nMulti Thread Processing Complete!");
            System.out.println("Valid numbers: " + validNumbers.get());
            System.out.println("Invalid numbers: " + invalidNumbers.get());
            System.out.println("Total processed: " +
                    (validNumbers.get() + invalidNumbers.get()));
            System.out.println("Time taken: " + duration + " ms");

            return duration;
        }

        // Validator task
        class PhoneNumberValidator implements Runnable {
            private final List<String> lines;
            private final int threadId;

            public PhoneNumberValidator(List<String> lines, int threadId) {
                this.lines = lines;
                this.threadId = threadId;
            }

            @Override
            public void run() {
                for (String line : lines) {
                    try {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            String phoneNumber = parts[2].trim();
                            boolean isValid = PHONE_PATTERN.matcher(phoneNumber).matches();

                            if (isValid) validNumbers.incrementAndGet();
                            else invalidNumbers.incrementAndGet();

                            String result = String.format("%s,%s,%s,%s",
                                    parts[0], parts[1], phoneNumber, isValid);
                            processedResults.put(result);

//                            System.out.printf("Thread %d processed: %s -> %s%n", threadId, phoneNumber, isValid);
                        }
                    } catch (Exception e) {
                        System.err.printf("Thread %d error processing line: %s%n",
                                threadId, line);
                    }
                }
            }
        }

        // Writer task
        class ResultWriter implements Runnable {
            private final String outputFile;

            public ResultWriter(String outputFile) {
                this.outputFile = outputFile;
            }

            @Override
            public void run() {
                try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                    writer.println("id,name,phone_number,is_valid");

                    while (true) {
                        String result = processedResults.take();
                        if (result.equals("END")) break;
                        writer.println(result);
                    }
                } catch (Exception e) {
                    System.err.println("Error writing results: " + e.getMessage());
                }
            }
        }
    }

    // Utility method to read CSV
    private static List<String> readCSV(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}