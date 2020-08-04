/*
* BackgroundThread class
* Allows for asynchronous execution of a code when a
* background app is passed to it.
* when .start() is invoked, it will start a separate thread
* of execution.
*/
public class BackgroundThread implements Runnable {
	private Thread backgroundThread;
	private APP app;
	/*
	 * Constructor for a background thread object, pass the background
	 * app that will be started asynchronously.
	 */
	public BackgroundThread(MyObj app) {
        this.app = app;
        backgroundThread = new Thread(this);
	}
    
    /*
     * Starts the background application. the execution will be in its own
     * thread.
     */ 
	public void start() {
        backgroundThread.start();
	}
	
	/*
	 * Allows the application to execute asynchronously, when 
	 * app.start() method is invoked. Will invoke backgroundStart()
	 * method.
	 */
	public void run() {
        try {
            while(!backgroundThread.isInterrupted()) {
                app.backgroundStart();
            }
            Thread.sleep(100);//Can be interrupted
        }catch(InterruptedException e){}
            app.exit();
	}
	
	/*
	 * Will exit the application and stop executing the program.
	 */
	public void exit() {
        backgroundThread.interrupt();
	}
	
}
