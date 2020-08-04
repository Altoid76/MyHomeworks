   /**
 * EspressOS Mobile Phone Class.
 *
 *
 * EspressOSMobile
 * In this assignment you will be creating an EspressOS Mobile Phone as part of a simulation.
 * The Mobile phone includes several attributes unique to the phone and has simple functionality.
 * You are to complete 2 classes. EspressOSMobile and EspressOSContact
 *
 * The phone has data
 *  Information about the phone state. 
 *    If it is On/Off
 *    Battery level 
 *    If it is connected to network. 
 *    Signal strength when connected to network
 *  Information about the current owner saved as contact information. 
 *    First name
 *    Last name
 *    Phone number
 *  A list of 10 possible contacts.
 *    Each contact stores first name, last name, phone number and chat history up to 20 messages
 *
 * The phone has functionality
 *  Turning on the phone
 *  Charging the phone. Increase battery level
 *  Change battery (set battery level)
 *  Use phone for k units of battery (decreases battery level by k)
 *  Search/add/remove contacts
 *
 * Attribute features
 *  if the phone is off. It is not connected. 
 *  if the phone is not connected there is no signal strength
 *  the attribute for battery life has valid range [0,100]. 0 is flat, 100 is full.
 *  the attribute for signal strength has a valid range [0, 5]. 0 is no signal, 5 is best signal.
 * 
 * Please implement the methods provided, as some of the marking is
 * making sure that these methods work as specified.
 *
 *
 */
import java.util.*;

public class EspressOSMobile
{
	public static final int MAXIMUM_CONTACTS = 10;
	

	/* Use this to store contacts. Do not modify. */
	protected EspressOSContact[] contacts;
	private Boolean status;
	private Boolean connected;
	private int signal;
	private int life;
	private EspressOSContact defaultContact;
	private List<Notifyapp> notify;
	private List<BackgroundApp> background;
	private List<String> message;
	/* Every phone manufactured has the following attributes
	 * 
	 * the phone is off
	 * the phone has battery life 25
	 * the phone is not connected
	 * the phone has signal strength 0
	 * Each of the contacts stored in the array contacts has a null value
	 * 
	 * the owner first name "EspressOS"
	 * the owner last name is "Incorporated"
	 * the owner phone number is "180076237867"
	 * the owner chat message should have only one message 
	 *         "Thank you for choosing EspressOS products"
	 *
	 */
	public EspressOSMobile() {
		/* given */
		contacts = new EspressOSContact[MAXIMUM_CONTACTS];
		status=false;
		connected=false;
		signal=0;
		life=25;
		List<Notifyapp> notify=new ArrayList<Notifyapp>();
		List<BackgroundApp> background=new ArrayList<BackgroundApp>();
		List<String> message=new ArrayList<String>();
		defaultContact=new EspressOSContact("EspressOS","Incorporated","180076237867");
		defaultContact.addChatMessage("EspressOS","Thank you for choosing EspressOS products");
		addContact(defaultContact);
		
	}

	/* returns a copy of the owner contact details
	 * return null if the phone is off
	 */
	public EspressOSContact getCopyOfOwnerContact() {
		if(status){
			return defaultContact.copy();
		}
		return null;
	}


	/* only works if phone is on
	 * will add the contact in the array only if there is space and does not exist
	 * The method will find an element that is null and set it to be the contact
	 */
	public boolean addContact(EspressOSContact contact) {
		int counter=0;
		if(status){
			for(int i=0;i<MAXIMUM_CONTACTS;i++){
				if(contacts[i]==contact){
					counter++;
				}
			}
			for(int j=0;j<MAXIMUM_CONTACTS;j++){
				if(contacts[j]==null&&contact!=null&&counter==0){
					contacts[j]=contact;
					return true;
			}
			
			}
			
		}
		return false;
	}

	/* only works if phone is on
	 * find the object and set the array element to null
 	 * return true on successful remove
	 */
	public boolean removeContact(EspressOSContact contact) {
		if(status){
			for(int i=0;i<MAXIMUM_CONTACTS;i++){
				if(contact==null){
					break;
				}
				if(contacts[i]==contact){
					contacts[i]=null;
					return true;
				}
			}
		}
		return false;
	}

	/* only works if phone is on
	 * return the number of contacts, or -1 if phone is off
	 */
	public int getNumberOfContacts() {
			if(status==false){
				return -1;
			}else{
				int counter=0;
				for(int i=0;i<contacts.length;i++){
					if(contacts[i]!=null){
						counter++;
					}
				}
				return counter;
			}
	}

	/* only works if phone is on
	 * returns all contacts that match firstname OR lastname
	 * if phone is off, or no results, null is returned
	 */
	public EspressOSContact[] searchContact(String name) {
		if(status==false){
			return null;
		}
		List<EspressOSContact> matched=new ArrayList<EspressOSContact>();
		int counter=0;
		for(int i=0;i<contacts.length;i++){
			if(name!=null&&contacts[i]!=null){
			if(contacts[i].getFirstName().equals(name)||contacts[i].getLastName().equals(name)){
				matched.add(contacts[i]);
				counter++;
			}
			}
		}
		if(counter==0){
			return null;
		}
		EspressOSContact[] matched1= new EspressOSContact[matched.size()];
		matched1=matched.toArray(matched1);
		return matched1;
	}

	/* returns true if phone is on
	 */
	public boolean isPhoneOn() {
		if(status==false){
			return false;
		}
		return true;
	}

	/* when phone turns on, it costs 5 battery for startup. network is initially disconnected
	 * when phone turns off it costs 0 battery, network is disconnected
	 * always return true if turning off
	 * return false if do not have enough battery level to turn on
	 * return true otherwise
	 */
	 public boolean setPhoneOn(boolean on) {
		if(on==false){
			status=false;
			connected=false;
			return true;
		}else{
			if(life<6){
				return false;
			}
			connected=false;
			status=true;
			life=life-5;
			return true;
		}
	}
	
	/* Return the battery life level. if the phone is off, zero 
	is returned.
	 */
	public int getBatteryLife() {
		if(status){
			return life;
		}
		return 0;
	}
	
	/* Change battery of phone.
	 * On success. The phone is off and new battery level adjusted and returns true
	 * If newBatteryLevel is outside manufacturer specification of [0,100], then 
	 * no changes occur and returns false.
	 */
	public boolean changeBattery(Battery battery) {
		if(battery.getLevel()<0||battery.getLevel()>100){
			return false;
		}else{
			status=false;
			life=battery.getLevel();
			return true;
		}
	}
	
	/* only works if phone is on. 
	 * returns true if the phone is connected to the network
	 */
	public boolean isConnectedNetwork() {
		if(status==true){
			return connected;
		}
		return false;
	}
	
	/* only works if phone is on. 
	 * when disconnecting, the signal strength becomes zero
	 */
	public void disconnectNetwork() {
		if(status==true){
			connected=false;
			signal=0;
		}
		
	}
	
	/* only works if phone is on. 
	 * Connect to network
	 * if already connected do nothing
	 * if connecting: 
	 *  1) signal strength is set to 1 if it was 0
	 *  2) signal strength will be the previous value if it is not zero
	 *  3) it will cost 2 battery life to do so
	 * returns the network connected status
	 */
	public boolean connectNetwork() {
		int previous=signal;
		if(status==true){
			if(connected==false){
				if(life<=2){
					connected=false;
					status=false;
					signal=0;
					return connected;
				}
				life-=2;
				if(signal==0){
					signal=1;
				}else{
					
				}
				connected=true;
				return connected;
			}
	}
		return false;
	}
	/* only works if phone is on. 
	 * returns a value in range [1,5] if connected to network
	 * otherwise returns 0
	 */
	public int getSignalStrength() {
		if(connected&&status){
			return signal;
		}
		return 0;
	}

	/* only works if phone is on. 
	 * sets the signal strength and may change the network connection status to on or off
	 * signal of 0 disconnects network
	 * signal [1,5] can connect to network if not already connected
	 * if the signal is set outside the range [0,5], nothing will occur and will return false
	 */
	public boolean setSignalStrength(int x) {
		if(status){
			if(x==0){
				connected=false;
				signal=x;
				return true;
			}else if(x>=1&&x<=5){
				connected=true;
				signal=x;
				return true;
			}else{
				return false;
			}
		}
		return false;
    }
	
	/* each charge increases battery by 10
	 * the phone has overcharge protection and cannot exceed 100
	 * returns true if the phone was charged by 10
	 */
	public boolean chargePhone() {
		if(life<=90){
			life+=10;
			return true;
		}else{
			return false;
		}
	}
	
	/* Use the phone which costs k units of battery life.
	 * if the activity exceeds the battery life, the battery automatically 
	 * becomes zero and the phone turns off.
	 */
	public void usePhone(int k) {	
		if(k>=life){
			life=0;
			status=false;
		}else{
			life-=k;
		}
	}
	public boolean changeAntenna(Antenna antenna){
		if(antenna==null){
			return false;
		}
		if(connected==false&&signal==0){
			connected=true;
			signal=antenna.getSignalStrength();
			return true;
		}
		return false;
	}
	/*install the App into EspressOSmobile,
	 *if App is null,then do not install it and return false
	 *if App is installed already in this Mobile,do not install it and return false
	 *otherwise install it and return true
	 */
	public boolean install(Notifyapp app){
		if(app==null){
			return false;
		}
			for(Notifyapp each: notify){
				if(app==each){
					return false;
			}
			}
			notify.add(app);
			return true;
	}
		public boolean installbackground(BackgroundApp app){
			if(app==null){
			return false;
		}
			for(BackgroundApp each: background){
				if(app==each){
					return false;
			}
			}
			background.add(app);
			return true;	
		}
	/*uninstall the App in EspressOSmobile,
	 *if App is null,then do not uninstall it and return false
	 *if App is found in EsoressOSmobile, uninstall it and return true
	 *otherwise return false;
	 */
	public boolean uninstallnotify(Notifyapp app){
		if(app==null){
			return false;
		}
		for(Notifyapp each: notify){
			if(app==each){
				notify.remove(each);
				return true;
			}
		}
		return false;
	}
		
	public boolean uninstallbackground(BackgroundApp app){
		if(app==null){
			return false;
		}
		for(BackgroundApp each: background){
			if(app==each){
				background.remove(each);
				return true;
			}
		}
		return false;
	}
	/*get all the apps that is running
	 *and return these as a list object.
	 */
	public List<Notifyapp> getRunningApps(){
		List<Notifyapp> collected=new ArrayList<Notifyapp>();
		for(Notifyapp each:notify){
			if(each.isrunning()){
				collected.add(each);
			}
				
		}
		return collected;
	}
	
	public List<Notifyapp> getNotificationApps(){
		return notify;
	}
	
	public List<BackgroundApp> getBackgroundApps(){
		return background;
	}
	public List<APP> getinstalledApp(){
		List<APP> collected=new ArrayList<APP>();
		for(APP each:notify){
			collected.add(each);
		}
		for(APP each:background){
			collected.add(each);
		}
		return collected;
	}
	
	public List<String> getNotifications(){
		return message;
	}
	
	public boolean run(String name){
		for(Notifyapp each:notify){
			if(each.getname().equals(name)){
				each.start();
				return true;
			}
	}
		return false;
	}
	
	public boolean close(String name){
		for(Notifyapp each:notify){
			if(each.getname().equals(name)){
				each.exit();
				return true;
			}
	}
		return false;
	}
	
	
	
	
}
