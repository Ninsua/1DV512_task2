import java.util.Random;

public class Philosopher implements Runnable {
	
	private int id;
	private final boolean DEBUG_MODE;
	
	private final ChopStick leftChopStick;
	private final ChopStick rightChopStick;
	
	private Random randomGenerator = new Random();
	private final int RANDOM_TIME = 1000; //Random time dipswitch. Default is 1000.
	
	private int numberOfEatingTurns = 0;
	private int numberOfThinkingTurns = 0;
	private int numberOfHungryTurns = 0;

	private double thinkingTime = 0;
	private double eatingTime = 0;
	private double hungryTime = 0;
	
	public Philosopher(int id, ChopStick leftChopStick, ChopStick rightChopStick, int seed, boolean debug) {
		this.id = id;
		this.leftChopStick = leftChopStick;
		this.rightChopStick = rightChopStick;
		DEBUG_MODE = debug;
		
		/*
		 * set the seed for this philosopher. To differentiate the seed from the other philosophers, we add the philosopher id to the seed.
		 * the seed makes sure that the random numbers are the same every time the application is executed
		 * the random number is not the same between multiple calls within the same program execution 
		 
		 * NOTE
		 * In order to get the same average values use the seed 100, and set the id of the philosopher starting from 0 to 4 (0,1,2,3,4). 
		 * Each philosopher sets the seed to the random number generator as seed+id. 
		 * The seed for each philosopher is as follows:
		 * 	 	P0.seed = 100 + P0.id = 100 + 0 = 100
		 * 		P1.seed = 100 + P1.id = 100 + 1 = 101
		 * 		P2.seed = 100 + P2.id = 100 + 2 = 102
		 * 		P3.seed = 100 + P3.id = 100 + 3 = 103
		 * 		P4.seed = 100 + P4.id = 100 + 4 = 104
		 * Therefore, if the ids of the philosophers are not 0,1,2,3,4 then different random numbers will be generated.
		 */
		
		randomGenerator.setSeed(id+seed);
	}
	public int getId() {
		return id;
	}

	public double getAverageThinkingTime() {
		return thinkingTime/numberOfThinkingTurns;
	}

	public double getAverageEatingTime() {
		return eatingTime/numberOfEatingTurns;
	}

	public double getAverageHungryTime() {
		return hungryTime/numberOfHungryTurns;
	}
	
	public int getNumberOfThinkingTurns() {
		return numberOfThinkingTurns;
	}
	
	public int getNumberOfEatingTurns() {
		return numberOfEatingTurns;
	}
	
	public int getNumberOfHungryTurns() {
		return numberOfHungryTurns;
	}

	public double getTotalThinkingTime() {
		return thinkingTime;
	}

	public double getTotalEatingTime() {
		return eatingTime;
	}

	public double getTotalHungryTime() {
		return hungryTime;
	}

	@Override
	public void run() {
		/*
		 * Think,
		 * Hungry,
		 * Eat,
		 * Repeat until thread is interrupted
		 * Increment the thinking/eating turns after thinking/eating process has finished.
		 */
		
		//Think, hungry and eat loop
		try {
			while (!Thread.currentThread().isInterrupted()) {
				//Thinking
				thinking();
				
				//Hungry
				hungry();
				
				//Eating
				eating();
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		} finally {
			if (DEBUG_MODE)
				System.out.println("Philosopher "+id+" has quit");
		}
	}
	
	//The philosophers thinking function
	private void thinking() throws InterruptedException {
		numberOfThinkingTurns++;
		
		if (DEBUG_MODE)
			System.out.println("Philosopher "+id+" is THINKING");
		
		int thinkingRandomTime = randomTime(); 
		Thread.sleep(thinkingRandomTime);
		thinkingTime += thinkingRandomTime;
	}
	
	//The philosophers hungry function
	private void hungry() throws InterruptedException {
		numberOfHungryTurns++;
		
		if (DEBUG_MODE)
			System.out.println("Philosopher "+id+" is HUNGRY");

		boolean hasTwoChopsticks = false;
		long timeCount = System.currentTimeMillis();
		while (!hasTwoChopsticks && !Thread.currentThread().isInterrupted()) {
			//If the left is available but the right isn't the philosopher drops the left one
			
			//Attemps to pick up the left chopstick.
			if (leftChopStick.pickUp()) {
				
				//Prints debug message
				if (DEBUG_MODE)
					pickedUpChopstickMsg(leftChopStick);
				
				//Attemps to pick up the right chopstick
				if (rightChopStick.pickUp()) {
					
					//Prints debug message
					if (DEBUG_MODE)
						pickedUpChopstickMsg(rightChopStick);
				}
				
				//Drops left if the right cannot be pickedup
				else {
					leftChopStick.putDown();
					
					if (DEBUG_MODE)
						putDownChopstickMsg(leftChopStick);
				}
			}
			
			if (rightChopStick.holds() && leftChopStick.holds()) {
				hasTwoChopsticks = true;	//Exists loop if philosopher has 2 chopsticks
			}
		}
		
		//Adds time waiting to hungry time.
		hungryTime += System.currentTimeMillis()-timeCount;
	}
	
	//The philosophers eat function
	private void eating() throws InterruptedException {
		numberOfEatingTurns++;
		
		if (DEBUG_MODE)
			System.out.println("Philosopher "+id+" is EATING");
		
		//Sleeps thread for random time and adds the sleept time to the total eating time.
		int eatingRandomTime = randomTime();
		Thread.sleep(eatingRandomTime);
		eatingTime += eatingRandomTime;
		
		//Puts down both chopsticks when the philosophers has finished eating
		putDownChopsticks();
	}
	
	//Generates a random time between based on the RANDOM_TIME dipswitch
	private int randomTime() {
		return randomGenerator.nextInt(RANDOM_TIME);
	}
	
	//Puts down both chopsticks
	private void putDownChopsticks() {
		rightChopStick.putDown();
		leftChopStick.putDown();
		
		if (DEBUG_MODE) {
			putDownChopstickMsg(rightChopStick);
			putDownChopstickMsg(leftChopStick);
		}
	}
	
	//Message refactoring
	private void pickedUpChopstickMsg(ChopStick chopId) {
		System.out.println("Philosopher "+id+" picked up chopstick "+chopId.getId());
	}
	
	//Message refactoring
	private void putDownChopstickMsg(ChopStick chopId) {
		System.out.println("Philosopher "+id+" put down chopstick "+chopId.getId());
	}
}
