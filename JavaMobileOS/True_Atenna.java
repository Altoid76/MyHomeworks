public class True_Antenna extends Antenna {
	public boolean Connected;
	public int signal;
	
	public True_Antenna(){
		this.Connected=false;
		this.signal=0;
	}
	public boolean isConnected(){
		return this.Connected;
	}
	
	public void setNetWork(boolean isConnected){
		this.Connected=isConnected;
	}
	
	public int getSignalStrength(){
		return this.signal;
	}
	
	public void setSignalStrength(int n){
		if(n>0&&n<5){
			this.signal=n;
		}
	}
}
