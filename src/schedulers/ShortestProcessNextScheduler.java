package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.ShortestProcessNextScheduler
 * run each job based on their relative arrival time and their process duration
 */
public class ShortestProcessNextScheduler extends NonpreemptiveScheduler {
    public ShortestProcessNextScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }

    @Override
    public void run() {
        System.out.println("ShortestProcessNextScheduler:");
        super.run();
    }

    @Override
    protected Job next() {
        Job nextJob = super.next();
        super.next(nextJob, SHORTEST_PROCESS_NEXT);
        return nextJob;
    }
}
