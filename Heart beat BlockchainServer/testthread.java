
public class testthread implements Runnable{
	private ServerInfo server;
	public testthread(ServerInfo server) {
		// TODO Auto-generated constructor stub
		this.server=server;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		server.setHost("Bilibili");
		server.setPort(8882);
		System.out.println(server.getHost()+server.getPort());
	}

}
