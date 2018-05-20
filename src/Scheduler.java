/***
 * Bao Nguyen
 * BCN140030
 * SE 4348.501
 *
 * Project 3
 * This project demonstrate six different scheduling algorithms.
 */

import java.util.Collections;
import java.util.LinkedList;


/***
 * class: Scheduler
 * abstract class that holds common fields for all schedulers,
 * such as the list of jobs and an abstract method of how each
 * scheduler will run
 */
public abstract class Scheduler {
    protected LinkedList<Job> listOfJobs;                       // list of jobs to run
    protected LinkedList<Job> processedJobs;                    // list of finished jobs
    protected static int frame;                                 // start time of the next job
    public static final int FIRST_COME_FIRST_SERVE = 1;
    public static final int ROUND_ROBIN = 2;
    public static final int SHORTEST_PROCESS_NEXT = 3;
    public static final int SHORTEST_REMAINING_TIME = 4;
    public static final int HIGHEST_RESPONSE_RATIO_NEXT = 5;
    public static final int FEEDBACK = 6;


    /***
     * constructor
     * assign a list of jobs for the scheduler
     * @param jobs: list of jobs
     */
    public Scheduler(LinkedList<Job> jobs) {
        listOfJobs = jobs;
        processedJobs = new LinkedList<>();
        frame = 0;
    }


    static int getFrame() {
        return frame;
    }


    /***
     * method: printList
     * print a list of jobs,
     * one after another by name in alphabetical order
     */
    protected void printList() {
        // calculate and display time
        int totalTime = 0;
        for(Job j : processedJobs)
            totalTime += j.getDuration();
        for(int t = 0; t < totalTime; t++) {
            System.out.print(t%10);
        }
        System.out.println();

        // sort and display jobs by name
        processedJobs.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        processedJobs.forEach(Job::print);
        System.out.println();
    }


    /***
     * method: run
     * abstract method
     * this method gets call in polymorphism behavior
     */
    public abstract void run();


    /***
     * method: next
     * sort the list of jobs based on the priority,
     * i.e. the compareTo method of the Job class
     * this method will be overridden by the derived classes
     * remove and return the first job
     * @return the next job to run
     */
    protected Job next() {
        Collections.sort(listOfJobs, Job::compareTo);
        return listOfJobs.removeFirst();
    }

}


/***
 * class: PreemptiveScheduler
 * abstract class that defines how preemptive schedulers run,
 * including Round Robin, Shortest Remaining Time, and Feedback
 */
abstract class PreemptiveScheduler extends Scheduler {

    /***
     * constructor
     * assign a list of jobs for the scheduler
     * @param jobs : list of jobs
     */
    public PreemptiveScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }

    @Override
    public void run() {
    }

}


/***
 * class: NonpreemptiveScheduler
 * abstract class that defines how non-preemptive schedulers run,
 * including First Come First Serve, Shortest Process Next,
 * and Highest Response Ratio Next
 */
abstract class NonpreemptiveScheduler extends Scheduler {

    /***
     * constructor
     * assign a list of jobs for the scheduler
     * @param jobs : list of jobs
     */
    public NonpreemptiveScheduler(LinkedList<Job> jobs) {
        super(jobs);
    }


    /***
     * method: run
     * remove each job from the list
     * wait and run to its completion
     * add to the processed list after it finished
     * update new start time
     */
    @Override
    public void run() {

        while(!listOfJobs.isEmpty()) {
            Job job = next();
            job.runSlice(0, frame);
            job.runComplete();
            processedJobs.add(job);
            frame += job.getDuration();
        }

        printList();
    }


    /***
     * method: next
     * decide which job to run next
     * @param currentJob: currently running job
     * @param mode: which scheduling method
     */
    protected void next(Job currentJob, int mode) {
        /*
         * use arrival time as priority if only 1 job
         * else check jobs wait
         */
        if(listOfJobs.size() < 2) {
            Job.setSortMode(FIRST_COME_FIRST_SERVE);
        }
        else {
            int start = frame;                              // start time of current job
            int finish = start + currentJob.getDuration();  // finish time of current job
            int index = 0;                                  // index of job in the list
            int jobsWait = 0;                               // how many jobs are waiting

            // check till the end of the list
            while(index < listOfJobs.size()) {
                /*
                 * if there is a job that starts after the start time and finish before the finish time
                 * of the current job, increment the job wait and count to check the next job
                 * when find a job that does not satisfy the above condition, break out of the while loop
                 */
                if(listOfJobs.get(index).getArrivalTime() < finish &&
                        listOfJobs.get(index).getArrivalTime() > start) {
                    jobsWait++;
                }

                index++;
//                else
//                    break;
            }

            // if more than 1 job is waiting, set the priority to the mode
            if(jobsWait > 1)
                Job.setSortMode(mode);
        }
    }
}


/***
 * class: FirstComeFirstServeScheduler
 * run each job one by one based on the arrival time
 */
class FirstComeFirstServeScheduler extends NonpreemptiveScheduler {
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


/***
 * class: RoundRobinScheduler
 * FCFS with clock time-out, also called "time slicing"
 * Each process given a slice of time to execute, then placed at back of ready queue.
 * Time slice given is called a quantum.
 */
class RoundRobinScheduler extends PreemptiveScheduler {
    private int quantum;

    public RoundRobinScheduler(LinkedList<Job> jobs, int quantum) {
        super(jobs);
        this.quantum = quantum;
    }

    @Override
    public void run() {
        System.out.println("RoundRobinScheduler:");

        LinkedList<Job> queue = new LinkedList<>();             // queue to hold running jobs
        boolean jobDone = false;                                // mark when a job is done

        // sort the list of jobs by the arrival time
        Job.setSortMode(FIRST_COME_FIRST_SERVE);
        Collections.sort(listOfJobs, Job::compareTo);
        frame = 0;

        // run until either the original list or the queue is empty
        while(!listOfJobs.isEmpty() || !queue.isEmpty()) {
            Job job;

            /*
             * keep removing the head of the list (job) and add to the queue
             * if the arrival time of the head <= the current slice time
             * if the queue is empty or a previous job is done,
             * then add the current job to the end of the queue
             * otherwise, add to the second last element of the queue
             */
            while(!listOfJobs.isEmpty() && listOfJobs.getFirst().getArrivalTime() <= frame) {
                job = listOfJobs.removeFirst();

                if(queue.isEmpty() || jobDone) {
                    queue.addLast(job);
                    if(jobDone)
                        jobDone = false;
                }
                else
                    queue.add(queue.size() - 1, job);
            }

            // dequeue
            job = queue.removeFirst();
            int thisQuantum = (quantum < job.getRemainingTime()) ? quantum : job.getRemainingTime();

            // run the current job one slice
            job.runSlice(thisQuantum, 0);

            // wait all other jobs one slice
            for(Job j : queue) {
                j.runSlice(0, thisQuantum);
            }
            for(Job j : listOfJobs) {
                j.runSlice(0, thisQuantum);
            }

            // add back to the queue or to the processed list
            if(job.getRemainingTime() > 0)
                queue.addLast(job);
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


/***
 * class: ShortestProcessNextScheduler
 * run each job based on their relative arrival time and their process duration
 */
class ShortestProcessNextScheduler extends NonpreemptiveScheduler {
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


/***
 * class: ShortestRemainingTimeScheduler
 * Choose process with shortest expected remaining time.
 * A new process arriving in the ready queue can preempt
 * the current process if its remaining time is smaller.
 */
class ShortestRemainingTimeScheduler extends PreemptiveScheduler {
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
        while(!listOfJobs.isEmpty() || !queue.isEmpty()) {

            /*
             * if the front of the list has the arrival time <= to the start time,
             * remove it from the list add it to the end of the queue
             */
            if(!listOfJobs.isEmpty() && listOfJobs.getFirst().getArrivalTime() <= frame)
                queue.add(listOfJobs.removeFirst());

            job = next();

            // run job
            job.runSlice(1, 0);

            // wait all other jobs
            for(Job j : listOfJobs)
                j.runSlice(0, 1);
            for(Job j : queue)
                j.runSlice(0, 1);

            // add to the processed job or back to the queue
            if(job.getRemainingTime() <= 0)
                processedJobs.add(job);
            else
                queue.add(job);

            frame++;
        }

        printList();

    }


    protected Job next() {
        Job.setSortMode(SHORTEST_REMAINING_TIME);
        Collections.sort(queue, Job::compareTo);
        return queue.removeFirst();
    }
}


/***
 * class: ShortestProcessNextScheduler
 * run each job based on their relative arrival time and their response ratio
 */
class HighestResponseRatioNextScheduler extends NonpreemptiveScheduler {
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


/***
 * class: FeedbackScheduler
 * Use a series of queues of decreasing priority.
 * A process begins as high-priority and moves to lower priority queues with each execution.
 * This penalizes longer jobs while allowing shorter jobs to finish quickly.
 * Does not require a time estimate.
 */
class FeedbackScheduler extends PreemptiveScheduler {
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
        Collections.sort(listOfJobs, Job::compareTo);
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