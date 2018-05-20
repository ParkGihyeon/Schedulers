package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.ShortestProcessNextScheduler
 * run each job based on their relative arrival time and their response ratio
 */
public class HighestResponseRatioNextScheduler extends NonpreemptiveScheduler {
    public HighestResponseRatioNextScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }

    @Override
    public void run() {
        System.out.println("HighestResponseRatioNextScheduler:");
        super.run();
    }

    @Override
    protected Job next() {
        Job nextJob = super.next();
        super.next(nextJob, HIGHEST_RESPONSE_RATIO_NEXT);
        return nextJob;
    }
}
