public class Main {
    
    private volatile int age = 10;
    
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        MyThread yourThread = new MyThread();
        thread.start();
        yourThread.start();
        
        Thread threadTest = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread :" + Thread.currentThread().getId() + " Current iteration " + i);
                try {
                    Thread.sleep(1000);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        
        threadTest.start();
    }
}