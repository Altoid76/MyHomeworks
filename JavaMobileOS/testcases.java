import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class testcases{
	private Battery battery;
	private Antenna antenna;
	
	@Before
	public void invoke(){
		Battery battery=new True_Battery();
		Antenna antenna=new True_Antenna();
	}
	
	@test
	public void testsetlevel(){
		battery.setlevel(25);
		Assert.assertequals(25,battery.getLevel());
	}
	
	@test
	public void testsetsignalstrength(){
		antenna.setSignalStrength(5);
		Assert.aseertequals(5,antenna.getSignalStrength());
	}
	
	
	@test
	public void testexceedmaximumBa(){
		battery.setlevel(120);
		Assert.assertequals(25,battery.getLevel());
	}
	
	
	@test
	public void testexceedmaximumAn(){
		antenna.setSignalStrengthel(6);
		Assert.assertequals(0,antenna.getSignalStrength());
	}
	
	@test
	public void testisconnected(){
		antenna.setNetWork(true);
		Assert.asserequals(true, antenna.isConnected());
	}
	
	@test
	public void shutdowninternet(){
		antenna.setNetWork(false);
		Assert.assertequals(false,antenna.isConnected());
	}
	
	
	
}
