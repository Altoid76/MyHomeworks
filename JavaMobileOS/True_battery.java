public class True_battery extends Battery{
	
	private int level;
	
	public True_battery(){
		this.level=25;
	}
	
	public void setLevel(int level){
		if(level>0&&level<100){
			this.level=level;
		}
	}
	
	public int getLevel(){
		return this.level;
	}
}
