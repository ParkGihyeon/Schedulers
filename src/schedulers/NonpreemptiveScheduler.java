package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.NonpreemptiveScheduler
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
    NonpreemptiveScheduler(LinkedList<Job> jobs) {
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
            }

            // if more than 1 job is waiting, set the priority to the mode
            if(jobsWait > 1)
                Job.setSortMode(mode);
        }
    }
}
