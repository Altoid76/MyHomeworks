import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodiclatestblockRunnable implements Runnable {
	private Boolean isRunning;
	private ConcurrentHashMap<ServerInfo, Date> serverStatus;
	private Blockchain blockchain;
	private int port;
	public PeriodiclatestblockRunnable(ConcurrentHashMap<ServerInfo, Date> serverStatus,Blockchain blockchain,int port) {
		this.serverStatus=serverStatus;
		this.blockchain=blockchain;
		this.port=port;
		this.isRunning=true;
	}
	public void setRunning(Boolean running) {
		 isRunning=running;
	 }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isRunning) {
			Random random=new Random();
			Object[] valuesObjects=serverStatus.keySet().toArray();
			ArrayList<Thread> threadArrayList = new ArrayList<>();
			for(int i=0;i<5;i++) {
				if(blockchain.getHead()!=null) {
				ServerInfo peer=(ServerInfo)valuesObjects[random.nextInt(valuesObjects.length)];
				String lastblockhash=Base64.getEncoder().encodeToString(blockchain.getHead().calculateHash());
				String message=String.format("lb|%d|%d|%s",port,blockchain.getLength(),lastblockhash);
				Thread thread=new Thread(new BlockchainClientRunnable(peer.getHost(), peer.getPort(), message));
				threadArrayList.add(thread);
				thread.start();
				}
			}
			for (Thread thread : threadArrayList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                }
            }
			try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
            }
		}
		
	}

}
