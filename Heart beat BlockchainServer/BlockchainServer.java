import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 3) {
            return;
        }

        int localPort = 0;
        int remotePort = 0;
        String remoteHost = null;

        try {
            localPort = Integer.parseInt(args[0]);
            remoteHost = args[1];
            remotePort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return;
        }

        Blockchain blockchain = new Blockchain();

        ConcurrentHashMap<ServerInfo, Date> serverStatus = new ConcurrentHashMap<ServerInfo, Date>();
        serverStatus.put(new ServerInfo(remoteHost, remotePort), new Date());
        
        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();
        
        PeriodicHeartbeatRunnable phr=new PeriodicHeartbeatRunnable(serverStatus,localPort);
        Thread pht=new Thread(phr);
        pht.start();
        
        PeriodicdetectRunnable pdr =new PeriodicdetectRunnable(serverStatus);
        Thread pdt=new Thread(pdr);
        pdt.start();
        
        PeriodiclatestblockRunnable pbr =new PeriodiclatestblockRunnable(serverStatus, blockchain, localPort);
        Thread pbt=new Thread(pbr);
        pbt.start();
   
//        Thread catchupThread=new Thread(new CatchupRunnable(remoteHost, remotePort, blockchain));
//        catchupThread.start();
//        try {
//			catchupThread.join();
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new BlockchainServerRunnable(clientSocket, blockchain, serverStatus,remotePort,remoteHost)).start();
            }
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        } finally {
            try {
                pcr.setRunning(false);
                pct.join();
                phr.setRunning(false);
                pht.join();
                pdr.setRunning(false);
                pdt.join();
                pbr.setRunning(false);
                pbt.join();
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    }
}
