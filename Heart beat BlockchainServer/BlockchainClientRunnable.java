import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BlockchainClientRunnable implements Runnable{
	private String host;

	private int port;

	private String message;

    public BlockchainClientRunnable(String host, int port, String message) {
       this.host=host;
       this.port=port;
       this.message=message;
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
            // create socket with a timeout of 2 seconds
            Socket toServer = new Socket();
            toServer.connect(new InetSocketAddress(host,port), 2000);
            PrintWriter printWriter = new PrintWriter(toServer.getOutputStream(), true);
            // send the message forward
            printWriter.println(message);
            printWriter.flush();
            
            // close printWriter and socket
            
            printWriter.close();
            toServer.close();
        } catch (IOException e) {
        }
	}

}
