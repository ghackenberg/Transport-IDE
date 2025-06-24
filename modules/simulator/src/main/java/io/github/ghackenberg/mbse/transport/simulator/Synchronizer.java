package example.simulator;

import java.util.ArrayList;
import java.util.List;

public class Synchronizer {

	public final int threads;
	
	public int finished;
	
	public final List<Double> steps = new ArrayList<>();
	public final List<Boolean> marks = new ArrayList<>();
	
	public Synchronizer(int threads) {
		this.threads = threads;
		finished = threads;
	}
	
	public synchronized void start() {
		finished--;
		
		notifyAll();
	}
	
	public synchronized double vote(double step) throws InterruptedException {
		steps.add(step);
		
		notifyAll();
		
		while (steps.size() < threads - finished) {
			wait();
		}
		
		for (double vote : steps) {
			step = Math.min(step, vote);
		}
		
		marks.add(true);
		
		notifyAll();
			
		while (marks.size() < threads - finished) {
			wait();
		}
		
		steps.remove(0);
		
		notifyAll();
		
		while (steps.size() > 0) {
			wait();
		}
		
		marks.remove(0);
		
		notifyAll();
		
		while (marks.size() > 0) {
			wait();
		}
		
		return step;
	}
	
	public synchronized void finish() {
		finished++;
		
		notifyAll();
	}
	
}
