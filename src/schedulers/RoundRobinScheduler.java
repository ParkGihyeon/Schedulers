package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.RoundRobinScheduler
 * FCFS with clock time-out, also called "time slicing"
 * Each process given a slice of time to execute, then placed at back of ready queue.
 * Time slice given is called a quantum.
 */
public class RoundRobinScheduler extends PreemptiveScheduler {
    private int quantum;


    public RoundRobinScheduler(LinkedList<Job> jobs, int quantum) {
        super(jobs);
        this.quantum = quantum;
    }


    @Override
    public void run() {
        System.out.printf("RoundRobinScheduler (with quantum = %d):", quantum);

        LinkedList<Job> queue = new LinkedList<>();             // queue to hold running jobs
        boolean jobDone = false;                                // mark when a job is done

        // sort the list of jobs by the arrival time
        Job.setSortMode(FIRST_COME_FIRST_SERVE);
        listOfJobs.sort(Job::compareTo);
        frame = 0;

        // run until either the original list or the queue is empty
        while (!listOfJobs.isEmpty() || !queue.isEmpty()) {
            Job job;

            /*
             * keep removing the head of the list (job) and add to the queue
             * if the arrival time of the head <= the current slice time
             * if the queue is empty or a previous job is done,
             * then add the current job to the end of the queue
             * otherwise, add to the second last element of the queue
             */
            while (!listOfJobs.isEmpty() && listOfJobs.getFirst().getArrivalTime() <= frame) {
                job = listOfJobs.removeFirst();

                if (queue.isEmpty() || jobDone) {
                    queue.addLast(job);
                    if (jobDone) {
                        jobDone = false;
                    }
                }
                else {
                    queue.add(queue.size() - 1, job);
                }
            }

            // dequeue
            job = queue.removeFirst();
            int thisQuantum = (quantum < job.getRemainingTime()) ? quantum : job.getRemainingTime();

            // run the current job one slice
            job.runSlice(thisQuantum, 0);

            // wait all other jobs one slice
            for (Job j : queue) {
                j.runSlice(0, thisQuantum);
            }
            for (Job j : listOfJobs) {
                j.runSlice(0, thisQuantum);
            }

            // add back to the queue or to the processed list
            if (job.getRemainingTime() > 0) {
                queue.addLast(job);
            }
            else {
                processedJobs.addLast(job);
                jobDone = true;
            }

            // update start time
            frame += thisQuantum;
        }

        printList();

    }

}
