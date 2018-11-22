import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {
	private final int id;
	
	//Was changed from type Lock to ReentrantLock
	//to be able to use the isHeldByCurrentThread() method
	ReentrantLock myLock = new ReentrantLock();
	
	public ChopStick(int id) {
		this.id = id;
	}
	
	//Timed tryLocks to ensure no deadlocks occur.
	//Waits for the maximum amount of eating time +1 before stopping trying to obtain the lock
	public boolean pickUp() throws InterruptedException {
		return myLock.tryLock(1001,TimeUnit.MILLISECONDS);
	}
	
	public void putDown() {
		myLock.unlock();
	}
	
	public boolean holds() {
		return myLock.isHeldByCurrentThread();
	}
	
	public int getId() {
		return id;
	}
}
