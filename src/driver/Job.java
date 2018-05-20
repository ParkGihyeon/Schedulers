package driver;

import schedulers.Scheduler;


/***
 * class: driver.Job
 * represent each job with name, arrival time, duration,
 * and how to compare 2 jobs
 */
public class Job implements Comparable {
    private String name;                 // job name
    private int arrivalTime;             // job arrival time
    private int duration;                // job process duration
    private int runTime;                 // times the job ran
    private StringBuilder runStat;       // display when running and waiting
    private static int sortMode;         // type of scheduling


    /***
     * constructor
     * create new job
     * @param name: job's name
     * @param arrivalTime: job's arrival time
     * @param duration: job's duration
     */
    public Job(String name, int arrivalTime, int duration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        runTime = 0;
        runStat = new StringBuilder();
        sortMode = Scheduler.FIRST_COME_FIRST_SERVE;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public static void setSortMode(int mode) {
        sortMode = mode;
    }

    public void print() {
        System.out.println(runStat.toString());
    }

    public int getRemainingTime() {
        return this.duration - this.runTime;
    }


    /***
     * method: runComplete
     * run the entire job to its completion
     */
    public void runComplete() {
        for (int time = 0; time < duration; time++) {
            runStat.append(name);
        }
    }


    /***
     * method: runSlice
     * run a job until its time slice expires
     * @param timeSlice: time to run
     * @param wait: wait time
     */
    public void runSlice(int timeSlice, int wait) {
        // run time
        for (int t = 0; t < timeSlice; t++) {
            runStat.append(name);
        }
        runTime += timeSlice;

        // wait time
        for (int w = 0; w < wait; w++) {
            runStat.append(" ");
        }
    }


    /***
     * method: compareTo
     * overridden method to sort the list of Jobs
     * @param o: another job object to compare
     * @return the integer that represents the higher priority job among the two
     */
    @Override
    public int compareTo(Object o) {
        Job rJob = (Job) o;

        // first come first serve case
        if (sortMode == Scheduler.FIRST_COME_FIRST_SERVE) {
            return Integer.compare(this.arrivalTime, rJob.arrivalTime);
        }
        // shortest process next case
        else if (sortMode == Scheduler.SHORTEST_PROCESS_NEXT) {
            return Integer.compare(this.duration, rJob.duration);
        }
        // highest response ratio next case
        else if (sortMode == Scheduler.HIGHEST_RESPONSE_RATIO_NEXT) {
            // calculate ratio of this job
            int thisWait = Scheduler.getFrame() - this.arrivalTime;
            double thisRatio = (double) (thisWait + this.duration) / this.duration;

            // calculate ratio of rjob
            int rWait = Scheduler.getFrame() - rJob.arrivalTime;
            double rRatio = (double) (rWait + rJob.duration) / rJob.duration;

            // compare
            return Double.compare(rRatio, thisRatio);
        }
        // shortest remaining time case
        else if (sortMode == Scheduler.SHORTEST_REMAINING_TIME) {
            return Integer.compare(this.getRemainingTime(), rJob.getRemainingTime());
        }
        // feedback and round robin does not need this function
        else
            return 1;
    }
}