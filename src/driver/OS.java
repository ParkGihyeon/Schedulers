/*
 * Bao Nguyen
 * BCN140030
 * SE 4348.501
 *
 * Project 3
 * This project demonstrate six different scheduling algorithms.
 */

package driver;

import schedulers.*;
import schedulers.FeedbackScheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class OS {
    public static void main(String[] args) {
        try {
            String jobsFileName = getJobsFileName();

            System.out.println("Mode:\n" +
                    "1. Individual\n" +
                    "2. All"
            );
            int type = getInt("Your choice: ", 1, 2);

            if (type == 1) {
                runIndividualScheduler(jobsFileName);
            }
            else {
                runAllSchedulers(jobsFileName);
            }
        }
        catch (Exception e) {
            System.out.println("Error: cannot find the specified file.");
        }
    }


    private static void runIndividualScheduler(String jobsFileName) throws FileNotFoundException {
        int type = 0;                           // get type of scheduler
        Scheduler scheduler;

        // print and get menu
        System.out.println("Types of scheduler:\n" +
                "1. First-come-first-served\n" +
                "2. Round-robin\n" +
                "3. Shortest process next\n" +
                "4. Shortest remaining time\n" +
                "5. Highest response ratio next\n" +
                "6. Feedback");
        type = getInt("Your choice: ", 1, 6);
        LinkedList<Job> jobs = createJobs(jobsFileName);

        // set the desired scheduler
        switch (type) {
            // run first-come-first-serve scheduler (done)
            case Scheduler.FIRST_COME_FIRST_SERVE:
                scheduler = new FirstComeFirstServeScheduler(jobs); break;
            // run highest-response-ratio-next scheduler (done)
            case Scheduler.HIGHEST_RESPONSE_RATIO_NEXT:
                scheduler = new HighestResponseRatioNextScheduler(jobs); break;
            // run round-robin scheduler
            case Scheduler.ROUND_ROBIN:
                int quantum = getInt("Enter quantum: ", 1, Integer.MAX_VALUE);
                scheduler = new RoundRobinScheduler(jobs, quantum); break;
            // run shortest-process-next scheduler (done)
            case Scheduler.SHORTEST_PROCESS_NEXT:
                scheduler = new ShortestProcessNextScheduler(jobs); break;
            // run shortest-remaining-time scheduler (done)
            case Scheduler.SHORTEST_REMAINING_TIME:
                scheduler = new ShortestRemainingTimeScheduler(jobs); break;
            // run feedback scheduler (done)
            case Scheduler.FEEDBACK:
                scheduler = new FeedbackScheduler(jobs); break;
            // safeguard: never execute
            default:
                scheduler = new FirstComeFirstServeScheduler(jobs);
        }

        // run the scheduler as polymorphism
        scheduler.run();
    }


    private static void runAllSchedulers(String jobsFileName) throws FileNotFoundException {
        Scheduler scheduler = null;
        LinkedList<Job> jobs = createJobs(jobsFileName);

        scheduler = new FirstComeFirstServeScheduler(jobs);
        scheduler.run();

        jobs = createJobs(jobsFileName);
        scheduler = new HighestResponseRatioNextScheduler(jobs);
        scheduler.run();

        jobs = createJobs(jobsFileName);
        scheduler = new RoundRobinScheduler(jobs, 3);
        scheduler.run();

        jobs = createJobs(jobsFileName);
        scheduler = new ShortestProcessNextScheduler(jobs);
        scheduler.run();

        jobs = createJobs(jobsFileName);
        scheduler = new ShortestRemainingTimeScheduler(jobs);
        scheduler.run();

        jobs = createJobs(jobsFileName);
        scheduler = new FeedbackScheduler(jobs);
        scheduler.run();

    }


    private static String getJobsFileName() throws FileNotFoundException {
        System.out.print("Enter a file name: ");
        String fileName = new Scanner(System.in).nextLine();
        System.out.println();
        Scanner sc = new Scanner(new File(fileName));
        return fileName;
    }


    /***
     * method: createJobs
     * read data from the file and create new jobs
     * @return a linked list of jobs
     */
    private static LinkedList<Job> createJobs(String fileName) throws FileNotFoundException {
        LinkedList<Job> jobs = new LinkedList<>();

        Scanner sc = new Scanner(new File(fileName));

        while (sc.hasNext()) {
            String name = sc.next();
            int arrivalTime = sc.nextInt();
            int duration = sc.nextInt();

            jobs.add(new Job(name, arrivalTime, duration));
        }
        sc.close();

        return jobs;
    }


    /***
     * method: getInt
     * get, validate, and return an integer within a range
     * @param prompt: message to the user
     * @param lowerBound: lower bound of the domain of input
     * @param upperBound: upper bound of the domain of input
     * @return an integer
     */
    private static int getInt(String prompt, int lowerBound, int upperBound) {
        int number = 0;
        boolean parseSuccess = false;
        Scanner keyboard = new Scanner(System.in);
        String input = "";

        // validate input
        do {
            System.out.print(prompt);
            input = keyboard.nextLine();

            try {
                number = Integer.parseInt(input);
                parseSuccess = true;
            }
            // not a number
            catch(NumberFormatException e) {
                System.out.printf("Invalid input. Please enter a number between %d and %d.\n",
                        lowerBound, upperBound);
            }

            // number not in domain
            if(parseSuccess && (number < lowerBound || number > upperBound))
                System.out.printf("Invalid input. Please enter a number between %d and %d.\n",
                        lowerBound, upperBound);

        } while(number < lowerBound || number > upperBound);

        System.out.println();

        return number;
    }
}
