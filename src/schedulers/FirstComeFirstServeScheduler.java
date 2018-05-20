package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.FirstComeFirstServeScheduler
 * run each job one by one based on the arrival time
 */
public class FirstComeFirstServeScheduler extends NonpreemptiveScheduler {
    public FirstComeFirstServeScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }

    @Override
    public void run() {
        System.out.println("FirstComeFirstServeScheduler:");
        super.run();
    }

    @Override
    protected Job next() {
        Job.setSortMode(FIRST_COME_FIRST_SERVE);
        return super.next();
    }
}
