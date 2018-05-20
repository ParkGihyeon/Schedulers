package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.FeedbackScheduler
 * Use a series of queues of decreasing priority.
 * A process begins as high-priority and moves to lower priority queues with each execution.
 * This penalizes longer jobs while allowing shorter jobs to finish quickly.
 * Does not require a time estimate.
 */
public class FeedbackScheduler extends PreemptiveScheduler {
    private LinkedList<Job> queue1;
    private LinkedList<Job> queue2;
    private LinkedList<Job> queue3;

    public FeedbackScheduler(LinkedList<Job> jobs) {
        super(jobs);
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
        queue3 = new LinkedList<>();
    }


    /***
     * method: emptyQueues
     * check to see if all queues are empty
     * @return true if all queues are empty and false otherwise
     */
    private boolean emptyQueues() {
        return queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty();
    }


    /***
     * method: waitJobs
     * wait all jobs in the list and 3 queues
     */
    private void waitJobs() {
        for(Job j : listOfJobs)
            j.runSlice(0, 1);
        for(Job j : queue1)
            j.runSlice(0, 1);
        for(Job j : queue2)
            j.runSlice(0, 1);
        for(Job j : queue3)
            j.runSlice(0, 1);
    }


    /***
     * method: addToQueue
     * add a job into an appropriate queue
     * @param job: a job to add
     * @param currentQueueNumber: queue number of the parametric job
     * @param nextQueue: add the job to the same queue or next queue
     */
    private void addToQueue(Job job, int currentQueueNumber, boolean nextQueue) {
        if(currentQueueNumber == 1) {
            if(nextQueue)
                queue2.addLast(job);
            else
                queue1.addLast(job);
        }
        else if(currentQueueNumber == 2) {
            if(nextQueue)
                queue3.addLast(job);
            else
                queue2.addLast(job);
        }
        else {
            queue3.addLast(job);
        }
    }


    @Override
    public void run() {
        System.out.println("FeedbackScheduler:");
        int queueNumber = 1;

        // sort the list of jobs by the arrival time
        Job.setSortMode(FIRST_COME_FIRST_SERVE);
        listOfJobs.sort(Job::compareTo);
        frame = 0;

        // run until either the original list or the queue is empty
        while(!listOfJobs.isEmpty() || !emptyQueues()) {
            Job job;

            /*
             * remove the head of the list (job) and add to the queue
             * if the arrival time of the head <= the current slice time
             */
            if(!listOfJobs.isEmpty() && listOfJobs.getFirst().getArrivalTime() <= frame)
                queue1.add(listOfJobs.removeFirst());

            // dequeue by queue number (highest to lowest priority)
            if(!queue1.isEmpty()) {
                job = queue1.removeFirst();
                queueNumber = 1;
            }
            else if(!queue2.isEmpty()) {
                job = queue2.removeFirst();
                queueNumber = 2;
            }
            else {
                job = queue3.removeFirst();
                queueNumber = 3;
            }

            // run the current job one slice
            job.runSlice(1, 0);

            // wait all other jobs one slice
            waitJobs();

            /*
             * add back to the queues if not done or to the processed list if done
             * if add to the queue:
             *      add to the same queue if:
             *          1. all queues are empty OR
             *          2. the next job in the listOfJobs is about to be loaded in
             *      add to the next queue if at least 2 of these conditions fail
             */
            if(job.getRemainingTime() > 0) {
                addToQueue(job, queueNumber, !emptyQueues() ||
                    (!listOfJobs.isEmpty() &&
                    listOfJobs.getFirst().getArrivalTime() == frame + 1));
            }
            else {
                processedJobs.addLast(job);
            }

            // increment frame
            frame++;
        }

        printList();
    }

}
