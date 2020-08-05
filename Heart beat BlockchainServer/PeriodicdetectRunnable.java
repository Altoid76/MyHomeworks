import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodicdetectRunnable implements Runnable {
	private ConcurrentHashMap<ServerInfo, Date> serverStatus;
	private volatile boolean isRunning;
    public PeriodicdetectRunnable(ConcurrentHashMap<ServerInfo, Date> serverStatus) {
    	this.isRunning = true;
        this.serverStatus = serverStatus;
    }
    public void setRunning(Boolean running) {
		 isRunning=running;
	 }
    @Override
    public void run() {
        while(isRunning) {
        	synchronized (serverStatus) {
        		 for (Entry<ServerInfo, Date> entry : serverStatus.entrySet()) {
                     // if the time  greater than 4 seconds, remove
                     if (new Date().getTime() - entry.getValue().getTime() > 4000) {
                     		serverStatus.remove(entry.getKey());
                     }else {
                    	 System.out.println("From detect");
                         System.out.print(entry.getKey().getHost()+"-"+entry.getKey().getPort() + "-" + entry.getValue() + " ");
                     }
                 }
			}
        
            System.out.println();
            // sleep for two seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }
}
