package schedulers;

import driver.Job;

import java.util.LinkedList;


/***
 * class: schedulers.Scheduler
 * abstract class that holds common fields for all schedulers,
 * such as the list of jobs and an abstract method of how each
 * scheduler will run
 */
public abstract class Scheduler {
    LinkedList<Job> listOfJobs;                       // list of jobs to run
    LinkedList<Job> processedJobs;                    // list of finished jobs
    static int frame;                                 // start time of the next job
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


    public static int getFrame() {
        return frame;
    }


    /***
     * method: printList
     * print a list of jobs,
     * one after another by name in alphabetical order
     */
    void printList() {
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
     * i.e. the compareTo method of the driver.Job class
     * this method will be overridden by the derived classes
     * remove and return the first job
     * @return the next job to run
     */
    protected Job next() {
        listOfJobs.sort(Job::compareTo);
        return listOfJobs.removeFirst();
    }

}


