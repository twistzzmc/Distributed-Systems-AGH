package zoo;


import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class DataMonitor implements Watcher, StatCallback {
    ZooKeeper zk;
    String znode;
    boolean dead;
    Executor listener;

    public DataMonitor(ZooKeeper zk, String znode, Executor listener) {
        this.zk = zk;
        this.znode = znode;
        this.listener = listener;
        watch();
    }

    public void watch() {
        zk.exists(znode, true, this, null);
        int below = this.watchChildren(znode);

        if (below > 0 && listener.external != null) {
            listener.external.children(below);
        }
    }

    public void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    // In this particular example we don't need to do anything
                    // here - watches are automatically re-registered with
                    // server and any watches triggered while the client was
                    // disconnected will be delivered (in order of course)
                    break;
                case Expired:
                    // It's all over
                    dead = true;
                    listener.closing();
                    break;
            }
        } else {
            if (path != null) {
                // Something has changed on the node, let's find out
                watch();
            }
        }
    }

    private int watchChildren(String path) {
        int below = 0;
        try {
            List<String> children = zk.getChildren(path, true);
            for (String child : children) {
                below += 1 + this.watchChildren(path + "/" + child);
            }
        } catch (KeeperException e) {
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return below;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;
        switch (rc) {
            case 0 -> exists = true;
            case -101 -> exists = false;
            case -112, -102 -> {
                dead = true;
                listener.closing();
                return;
            }
            default -> {
                zk.exists(znode, true, this, null);
                return;
            }
        }

        listener.exists(exists);
    }
}