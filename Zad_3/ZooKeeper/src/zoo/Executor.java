package zoo;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Executor implements Watcher {
    private final String znode;
    public final DataMonitor dm;
    private final ZooKeeper zk;
    public External external;

    public Executor(String host, String znode) throws IOException {
        this.znode = znode;
        zk = new ZooKeeper(host, 3000, this);
        dm = new DataMonitor(zk, znode, this);
        external = null;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.err.println("Usage: Executor hostPort znode");
            System.exit(2);
        }

        String host = args[0];
        String znode = args[1];
        try {
            new Executor(host, znode);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Thread.currentThread().join();
    }

    public void process(WatchedEvent event) {
        dm.process(event);
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
            String[] childArr = path.split("/");
            System.out.println("  ".repeat(indent) + "/" + childArr[childArr.length - 1]);
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