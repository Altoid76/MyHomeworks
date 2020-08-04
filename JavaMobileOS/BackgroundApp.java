public class BackgroundApp implements APP{
	
	private boolean running;
	private String data;
	private String name;
	
	public BackgroundApp(String name){
		this.name=name;
		this.running=false;
		this.data="";	
	}
		
	public void backgroundStart(){
		start();
	}
	
	
	public void start(){
		this.running=true;
	}
	public void exit(){
		this.running=false;
	}
	public String getData(){
		return "";
	}
	
	public Boolean isrunning(){
		return this.running;
	}
	public String getname(){
		return this.name;
	}
}
