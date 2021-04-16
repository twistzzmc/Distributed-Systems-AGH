package zoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Executor implements Watcher, Runnable {
    private final String znode;
    private final String[] exec;
    public final DataMonitor dm;
    private final ZooKeeper zk;
    private Process child;

    private boolean nodeExists;
    public External external;

    public Executor(String host, String znode, String[] exec) throws IOException {
        this.znode = znode;
        this.exec = exec;
        zk = new ZooKeeper(host, 3000, this);
        dm = new DataMonitor(zk, znode, this);
        external = null;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Executor hostPort znode program [args ...]");
            System.exit(2);
        }
        String host = args[0];
        String znode = args[1];
        String[] exec = new String[args.length - 2];
        System.arraycopy(args, 2, exec, 0, exec.length);
//        Executor executor = null;
        try {
//            executor = new Executor(host, znode, exec);
            new Executor(host, znode, exec);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Thread.currentThread().join();
    }

    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException ignored) { }
    }

    public void exists(boolean exists) {
        if (exists) {
            if (external == null) {
                external = new External(this);
                external.startGUI();
            }
        } else {
            external.stopGUI();
            external = null;
        }
    }

    public void closing() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void printTree() {
        System.out.println("Tree structure:");
        printTreeInner(znode, 0);
    }

    private void printTreeInner(String path, int indent) {
        try {
            List<String> children = zk.getChildren(path, false);
            System.out.println("\t".repeat(indent) + "* " + path);
            for (String child : children) {
                printTreeInner(path + "/" + child, indent+1);
            }
        } catch (KeeperException e) {
            // node not found
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}