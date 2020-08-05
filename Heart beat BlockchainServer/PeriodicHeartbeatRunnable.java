import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodicHeartbeatRunnable implements Runnable {
	
	 private ConcurrentHashMap<ServerInfo, Date> serverStatus;
	private int sequenceNumber;
	private volatile boolean isRunning;
	private int localPort;
	public PeriodicHeartbeatRunnable(ConcurrentHashMap<ServerInfo, Date> serverStatus,int localPort) {
		this.isRunning = true;
	        this.serverStatus = serverStatus;
	        this.sequenceNumber = 0;
	        this.localPort=localPort;
	    }
	 public void setRunning(Boolean running) {
		 isRunning=running;
	 }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 while(isRunning) {
	            // broadcast HeartBeat message to all peers
	            ArrayList<Thread> threadArrayList = new ArrayList<>();
	            for (ServerInfo si : serverStatus.keySet()){
	                Thread thread = new Thread(new BlockchainClientRunnable(si.getHost(),si.getPort(),
	                		String.format("hb|%d|%d\n",localPort,sequenceNumber)));
	                threadArrayList.add(thread);
	                thread.start();
	            }

	            for (Thread thread : threadArrayList) {
	                try {
	                    thread.join();
	                } catch (InterruptedException e) {
	                }
	            }

	            // increment the sequenceNumber
	            sequenceNumber += 1;

	            // sleep for two seconds
	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	            }
	        }
	}

}
