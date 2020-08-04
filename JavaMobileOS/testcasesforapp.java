import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class testcasedforapps{
	
	@Before
	public void invoke(){
		Notifyapp notify=new Notifyapp("notice!!");
		BackgroundApp bg=new BackgroundApp("back");
		EspressOSMobile mobile=new EspressOSMobile();
	}
	@test
	public void testgetname(){
		Assert.assertequals("notice!!",notify.getname());
	}
	@test
	public void testnotification(){
		notify.notifyOS(mobile,"xixi");
		Assert.assertequals("xixi",mobile.getNotifications().get(0));
	}


	
}
