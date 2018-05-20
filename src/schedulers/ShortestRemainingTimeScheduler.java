package schedulers;

import driver.Job;

import java.util.Collections;
import java.util.LinkedList;


/***
 * class: schedulers.ShortestRemainingTimeScheduler
 * Choose process with shortest expected remaining time.
 * A new process arriving in the ready queue can preempt
 * the current process if its remaining time is smaller.
 */
public class ShortestRemainingTimeScheduler extends PreemptiveScheduler {
    private LinkedList<Job> queue = new LinkedList<>();             // queue to hold running jobs


    public ShortestRemainingTimeScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }


    @Override
    public void run() {
        System.out.println("ShortestRemainingTimeScheduler:");
        frame = 0;
        Job job;

        // run until the list of job and the queue are both empty
        while (!listOfJobs.isEmpty() || !queue.isEmpty()) {

            /*
             * if the front of the list has the arrival time <= to the start time,
             * remove it from the list add it to the end of the queue
             */
            if (!listOfJobs.isEmpty() && listOfJobs.getFirst().getArrivalTime() <= frame) {
                queue.add(listOfJobs.removeFirst());
            }

            job = next();

            // run job
            job.runSlice(1, 0);

            // wait all other jobs
            for (Job j : listOfJobs) {
                j.runSlice(0, 1);
            }
            for (Job j : queue) {
                j.runSlice(0, 1);
            }

            // add to the processed job or back to the queue
            if (job.getRemainingTime() <= 0) {
                processedJobs.add(job);
            }
            else {
                queue.add(job);
            }

            frame++;
        }

        printList();

    }


    protected Job next() {
        Job.setSortMode(SHORTEST_REMAINING_TIME);
        queue.sort(Job::compareTo);
        return queue.removeFirst();
    }
}
