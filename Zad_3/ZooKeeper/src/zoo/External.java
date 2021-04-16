package zoo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class External extends Thread {
    private final Executor executor;
    private boolean stop;

    External(Executor executor) {
        this.executor = executor;
        this.stop = false;
    }

    public void run() {
        System.out.println("GUI STARTED...");

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (!stop) {
                if (br.ready()) {
                    String s = br.readLine();
                    if (s.equals("T")) {
                        executor.printTree();
                    }
                }
            }

            br.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("GUI STOPPED...");
    }

    public void stopGUI() {
        this.stop = true;
    }

    public void startGUI() {
        this.stop = false;
        this.start();
    }

    public void children(int below) {
        System.out.println("Current number of children: " + below);
    }
}
