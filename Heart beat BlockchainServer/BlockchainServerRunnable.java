import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
 
 
public class BlockchainServerRunnable implements Runnable{
 
    private Socket clientSocket;
    private Blockchain blockchain;
    private ConcurrentHashMap<ServerInfo, Date> serverStatus;
	private int remoteport;
	private String remotehost;
    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain, 
    		ConcurrentHashMap<ServerInfo, Date> serverStatus,int remoteport,String remotehost) {
        this.clientSocket = clientSocket;
        this.blockchain = blockchain;
        this.serverStatus = serverStatus;
        this.remoteport=remoteport;
        this.remotehost=remotehost;
    }
 
    public void run() {
        try {
        	Socket toServer=new Socket();
			toServer.connect(new InetSocketAddress(remotehost, remoteport), 2000);
			PrintWriter printWriter = new PrintWriter(toServer.getOutputStream(), true);
            //ObjectInputStream objcetInput=new ObjectInputStream(toServer.getInputStream());
            printWriter.println("cu");
            printWriter.flush();
            printWriter.close();
            //objcetInput.close();
            toServer.close();
            serverHandler(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e) {
        }
    }
    
    public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {
        BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(clientInputStream));
        PrintWriter outWriter = new PrintWriter(clientOutputStream, true);
        String localIP = (((InetSocketAddress) clientSocket.getLocalSocketAddress()).getAddress()).toString().replace("/", "");
        String remoteIP = (((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
        try {
            while (true) {
                String inputLine = inputReader.readLine();
                if (inputLine == null) {
                    break;
                }
                inputLine=inputLine.replace(" ","");
                String[] tokens = inputLine.split("\\|");
                switch (tokens[0]) {
                    case "tx":
                        if (blockchain.addTransaction(inputLine))
                            outWriter.print("Accepted\n\n");
                        else
                            outWriter.print("Rejected\n\n");
                        outWriter.flush();
                        break;
                    case "pb":
                        outWriter.print(blockchain.toString() + "\n");
                        outWriter.flush();
                        break;
                    case "cc":
                        return;
                    case "hb":
                    	try {
                    	int port=Integer.parseInt(tokens[1]);
                    	int sequenceNo=Integer.parseInt(tokens[2]);
                    	boolean contains=false;
                    	synchronized (serverStatus) {
                    		for(Entry<ServerInfo, Date> entry : serverStatus.entrySet()){
                           		// log the corresponding serverinfo who send heartbeat with new time
                            		if(localIP.equals(entry.getKey().getHost())
                            				&&port==entry.getKey().getPort()){
                           			serverStatus.put(entry.getKey(), new Date());
                           			contains=true;
                           		}                    	
                            		}
                            	//first time receive the heartbeat from a server
                            	if(!contains) {
                            		ArrayList<Thread> threadArrayList = new ArrayList<>();
                                    for (ServerInfo si : serverStatus.keySet()) {
                                        if ((si.getPort()==clientSocket.getLocalPort())
                                        		||(si.getPort()==port)) {
                                            continue;
                                        }
//                                        System.out.println("yes");
                                        Thread thread = new Thread(new BlockchainClientRunnable(si.getHost(),si.getPort()
                                        		,String.format("si|%d|%s|%d",clientSocket.getLocalPort(),localIP
                                        				,port)));
                                        threadArrayList.add(thread);
                                        thread.start();
                                    }
                                    for (Thread thread : threadArrayList){
                                        thread.join();
                                    }
                                    serverStatus.put(new ServerInfo(remoteIP, port),new Date());
                            	}
						}
                    	}catch(NumberFormatException e){
                    		
                    	}
                    	break;
                    case "si":
                    	boolean contains1=false;
                    	int sender=Integer.parseInt(tokens[1]);
                    	String host=tokens[2];
                    	int port1=Integer.parseInt(tokens[3]);
                    	synchronized (serverStatus) {
                    		//check if the serverstatus contains the serverinfo sent by si
                    		for(ServerInfo si:serverStatus.keySet()){
                        		if(si.getHost().equals(host)&&si.getPort()==port1) {
                        			contains1=true;
                        		}
                        	}
                        	if(!contains1){
                        		 // relay to other neighbors except for senders, originator server and myself
                                ArrayList<Thread> threadArrayList = new ArrayList<>();
                                for (ServerInfo si : serverStatus.keySet()) {
                                    if ((si.getPort()==port1)|| 
                                    		(si.getPort()==sender) || 
                                    		(si.getPort()==clientSocket.getLocalPort())) {
                                        continue;
                                    }
                                    String message=String.format("si|%d|%s|%d",clientSocket.getLocalPort(),host,port1);
                                    Thread thread = new Thread(new BlockchainClientRunnable(si.getHost(),si.getPort(),message));
                                    threadArrayList.add(thread);
                                    thread.start();
                                }
                                for (Thread thread : threadArrayList) {
                                    thread.join();
                                }
                                serverStatus.put(new ServerInfo(host,port1),new Date());
                        	}
						}
                    	break;
                    case "lb":
                    	int lbport=Integer.parseInt(tokens[1]);
                    	int length=Integer.parseInt(tokens[2]);
                    	String lastblockhash=tokens[3];
                    	if(blockchain.getLength()<length) {
                    		//create a client socket to neighbor 
                    		Socket toServer = new Socket();
                            toServer.connect(new InetSocketAddress(remoteIP,lbport), 2000);
                            PrintWriter printWriter = new PrintWriter(toServer.getOutputStream(), true);
                            //ObjectInputStream objcetInput=new ObjectInputStream(toServer.getInputStream());
                            printWriter.println("cu");
                            printWriter.flush();
                    	}else if(blockchain.getLength()==length) {
                    		//establish the connection and compare the hash
                    		Block peerhead=blockchain.getHead();
                    		Socket toServer = new Socket();
                    		toServer.connect(new InetSocketAddress(remoteIP,lbport), 2000);
                    		PrintWriter printWriter = new PrintWriter(toServer.getOutputStream(), true);
                    		printWriter.println("cu");
                    		printWriter.flush();
                    		Boolean smaller=false;
                    		ObjectInputStream objectInput=new ObjectInputStream(toServer.getInputStream());
//                    		Block neighborhead=(Block)objcetInput.readObject();
//                    			byte[] peerhash=peerhead.calculateHash();
//                    			byte[] neighborhash=neighborhead.calculateHash();
//                    			for(int j=0;j<peerhash.length;j++){
//                    				if(Byte.compare(peerhash[j], neighborhash[j])>0){
//                    					smaller=true;
//                    					break;
//                    				}
//                    		}
                    		if(smaller) {
                    			
                    		}else {
                    			break;
                    		}
                    	}else{
                    		break;
                    	}
                    	break;
                    case "cu":
                    	if(tokens.length==1) {
                    		ObjectOutputStream objectOutput=new ObjectOutputStream(clientOutputStream);
                    		objectOutput.writeObject(blockchain.getHead());
                    		objectOutput.flush();
                    		objectOutput.close();
                    	}else if(tokens.length==2) {
                    		String hashString= tokens[1];
                    		byte[] hashBytes=Base64.getDecoder().decode(hashString);
                    		ObjectOutputStream objectOutput=new ObjectOutputStream(clientOutputStream);
                    		Block currentBlock=blockchain.getHead();
                    		while(currentBlock!=null) {
                    			if(Arrays.equals(currentBlock.calculateHash(), hashBytes)){
                    				objectOutput.writeObject(currentBlock);
                    				objectOutput.flush();
                    				break;
                    			}
                    			currentBlock=currentBlock.getPreviousBlock();
                    		}
                    		objectOutput.close();
                    	}
                    	break;
                    default:
                        outWriter.print("Error\n\n");
                        outWriter.flush();
                        
                }
            }
        } catch (IOException e) {
        	      
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
}
}
 
 