public class Notifyapp implements APP{
	public String message;
	public String name;
	public boolean running;
	public Notifyapp(String name){
		this.message="";
		this.name=name;
		this.running=false;
	}
	
	public void notifyOS(EspressOSMobile mobile,String message){
		mobile.getNotifications().add(message);
	}
	public void start(){
		this.running=true;
	}
	public String getname(){
		return this.name;
	}
	public Boolean isrunning(){
		return this.running;
	}
	public void exit(){
		this.running=false;
	}
}
