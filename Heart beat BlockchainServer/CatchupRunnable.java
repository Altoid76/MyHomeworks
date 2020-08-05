import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

public class CatchupRunnable implements Runnable{
	private String host;
	private int port;
	private Blockchain blockchain;
	public CatchupRunnable(String host,int port,Blockchain blockchain) {
		this.host=host;
		this.port=port;
		this.blockchain=blockchain;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			Socket toServer = new Socket();
            toServer.connect(new InetSocketAddress(host,port),2000);
            PrintWriter printWriter = new PrintWriter(toServer.getOutputStream(), true);
            ObjectInputStream objectInput=new ObjectInputStream(toServer.getInputStream());
            printWriter.println("cu");
            System.out.println("sent");
            Block head=(Block)objectInput.readObject();
            Block headBlock=head;
            int length=0;
            while(headBlock!=null) {
                String message=String.format("cu|%s",
                		Base64.getEncoder().encodeToString(headBlock.getPreviousHash()));
                printWriter.println(message);
                Block nextBlock=(Block)objectInput.readObject();
                headBlock.setPreviousBlock(nextBlock);
                headBlock=nextBlock;
                length++;
            }
            blockchain.setHead(head);
            blockchain.setLength(length);
            printWriter.close();
            objectInput.close();
            toServer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
