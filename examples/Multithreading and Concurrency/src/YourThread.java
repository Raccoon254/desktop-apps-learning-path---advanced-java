public class YourThread implements Runnable{
    @Override
    public void run(){
        for (int i = 0; i < 5; i++) {
            System.out.println("Thread :" + Thread.currentThread().getId() + " Current iteration " + i);
            try {
                Thread.sleep(1000);
            }catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
