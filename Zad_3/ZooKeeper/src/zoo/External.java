package zoo;

import java.io.BufferedReader;
import java.io.IOException;
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
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (!stop) {
            try {
                if (br.ready()) {
                    br.readLine();
                    executor.printTree();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
