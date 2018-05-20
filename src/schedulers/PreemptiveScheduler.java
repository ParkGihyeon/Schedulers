package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.PreemptiveScheduler
 * abstract class that defines how preemptive schedulers run,
 * including Round Robin, Shortest Remaining Time, and Feedback
 */
abstract class PreemptiveScheduler extends Scheduler {

    /***
     * constructor
     * assign a list of jobs for the scheduler
     * @param jobs : list of jobs
     */
    PreemptiveScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }

}
