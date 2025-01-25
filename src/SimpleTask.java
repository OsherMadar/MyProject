import javax.swing.*;
import java.awt.event.*;

public class SimpleTask implements Task, Runnable {

    private String simpletaskName;
    private String simpletaskDescription;
    private int duration;
    private int remainingTime;
    private boolean completed = false;
    private boolean paused = false;
    private JProgressBar progressBar;
    private Timer timer;
    private int elapsedTime;
    private boolean isRunning;
    private Thread taskThread;

    public SimpleTask(String taskName, String simpletaskDescription, int duration) throws IllegalArgumentException {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive value.");
        }
        this.simpletaskName = taskName;
        this.simpletaskDescription = simpletaskDescription;
        this.duration = duration;
        this.remainingTime = duration;
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setStringPainted(true);
    }

    @Override
    public String toString() {
        return "Simple Task Name=" + simpletaskName + ": " + simpletaskDescription;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleTask) {
            SimpleTask other = (SimpleTask) obj;
            return simpletaskName.equals(other.simpletaskName);
        }
        return false;
    }

    public String getName() {
        return simpletaskName;
    }

    public String getDescription() {
        return simpletaskDescription;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) throws IllegalArgumentException {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive value.");
        }
        this.duration = duration;
        this.remainingTime = duration;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void Start() throws IllegalStateException {
        if (isComplete()) {
            throw new IllegalStateException("Task is already complete and cannot be started again.");
        }
        if (isRunning) {
            throw new IllegalStateException("Task is already running.");
        }

        elapsedTime = 0; // Reset elapsed time when starting
        isRunning = true;
        paused = false;

        // Create and start a new thread
        taskThread = new Thread(this);
        taskThread.start();
    }

    @Override
    public void Complete() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Task is not running, cannot complete.");
        }
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt(); // Stop the thread
        }
        completed = true;
        remainingTime = 0;
        progressBar.setValue(100);
        progressBar.setString(getName() + " - Completed");
        System.out.println("Task " + simpletaskName + " completed manually.");
    }

    @Override
    public synchronized void Cancel() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Task is not running, cannot cancel.");
        }
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt();  // Stop the thread
        }
        remainingTime = 0;
        progressBar.setValue(0);
        progressBar.setString("Cancelled");
        System.out.println("Task " + simpletaskName + " cancelled.");
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public void pause() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Task is not running, cannot pause.");
        }
        if (paused) {
            throw new IllegalStateException("Task is already paused.");
        }

        paused = true;
        isRunning = false;
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt(); // Stop the thread
        }
        System.out.println("Task " + simpletaskName + " paused.");
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void resume() throws IllegalStateException {
        if (!paused) {
            throw new IllegalStateException("Task is not paused, cannot resume.");
        }
        if (completed) {
            throw new IllegalStateException("Task is already completed and cannot be resumed.");
        }

        paused = false;
        isRunning = true;

        // Create and start a new thread after the task was paused
        taskThread = new Thread(this);
        taskThread.start();
        System.out.println("Resuming task: " + simpletaskName);
    }

    // Update progress
    public double getProgressPercentage() {
        if (duration == 0) {
            throw new ArithmeticException("Duration cannot be zero.");
        }
        return Math.min(((double) elapsedTime / duration) * 100, 100); // Ensure progress doesn't exceed 100%
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    // Runnable method for handling the timer
    @Override
    public void run() {
        while (remainingTime > 0 && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000); // Wait for one second
            } catch (InterruptedException e) {
                // If the thread is interrupted, exit the loop
                return;
            }

            elapsedTime++;
            remainingTime--;

            progressBar.setValue((int) getProgressPercentage());
            progressBar.setString(simpletaskName + " - " + elapsedTime + "/" + duration + " sec");

            if (remainingTime <= 0) {
                completed = true;
                progressBar.setString(simpletaskName + " - Completed");
                System.out.println("Task " + simpletaskName + " completed!");
            }
        }
    }
}
