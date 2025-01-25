import java.util.ArrayList;
import java.util.List;

public class ComplexTask implements Task, Runnable {
    private String complextaskName;
    private String complextaskDescription;
    private boolean completed;
    private boolean isPaused;
    private boolean isRunning;
    private List<SimpleTask> subTasks;
    private int totalDuration;  // זמן כולל של המשימה
    private int elapsedTime;    // זמן שחלף
    private Thread taskThread;

    public ComplexTask(String complextaskName, String complextaskDescription) {
        this.complextaskName = complextaskName;
        this.complextaskDescription = complextaskDescription;
        this.completed = false;
        this.isPaused = false;
        this.isRunning = false;
        this.subTasks = new ArrayList<>();
        this.totalDuration = 0; // מאתחלים את הזמן הכולל
        this.elapsedTime = 0;   // מאתחלים את הזמן שחלף
    }

    public String getComplexTaskName() {
        return complextaskName;
    }

    public List<SimpleTask> getSubTasks() {
        return subTasks;
    }

    public String getName() {
        return complextaskName;
    }

    @Override
    public int getDuration() {
        return totalDuration;
    }

    public void addSubTask(SimpleTask subTask) throws IllegalArgumentException {
        if (subTask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        subTasks.add(subTask);
        totalDuration += subTask.getDuration(); // עדכון הזמן הכולל של המשימה המורכבת
    }

    @Override
    public String toString() {
        return "complex task Name=" + complextaskName + ": " + complextaskDescription;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexTask) {
            ComplexTask other = (ComplexTask) obj;
            return complextaskName.equals(other.complextaskName) && complextaskDescription.equals(other.complextaskDescription);
        }
        return false;
    }

    @Override
    public void Start() throws IllegalStateException {
        if (isComplete()) {
            throw new IllegalStateException("The task is already completed.");
        }
        if (isRunning) {
            throw new IllegalStateException("The task is already running.");
        }

        isRunning = true;
        elapsedTime = 0;  // איפוס הזמן שחלף

        taskThread = new Thread(this);
        taskThread.start();

        // התחלת כל תת-משימה
        for (SimpleTask subTask : subTasks) {
            subTask.Start();
        }
    }

    @Override
    public void Complete() {
        try {
            int completedCount = 0;
            for (SimpleTask subTask : subTasks) {
                if (subTask.isComplete()) {
                    completedCount++;
                }
            }
            if (completedCount == subTasks.size()) {
                System.out.println("All sub-tasks have been completed. The complex task is now complete.");
                completed = true;
            } else if (completedCount == 0) {
                System.out.println("No sub-tasks have been completed.");
            } else {
                System.out.println("Some sub-tasks have been completed. The complex task is not yet complete.");
            }
        } catch (Exception e) {
            System.out.println("Error completing complex task: " + e.getMessage());
        }
    }

    @Override
    public void Cancel() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Cannot cancel a task that is not running.");
        }

        System.out.println("Canceling complex task " + complextaskName);
        if (taskThread != null) {
            taskThread.interrupt(); // עוצרים את הטרד
        }
        for (SimpleTask subTask : subTasks) {
            subTask.Cancel();
        }
        isRunning = false;
    }

    @Override
    public boolean isComplete() {
        for (SimpleTask subTask : subTasks) {
            if (!subTask.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public double getProgressPercentage() {
        double totalProgress = 0;
        int totalDuration = 0;
        for (SimpleTask subTask : subTasks) {
            totalProgress += subTask.getProgressPercentage();
            totalDuration += subTask.getDuration();
        }
        return totalDuration > 0 ? totalProgress / totalDuration * 100 : 0;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void pause() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Cannot pause a task that is not running.");
        }

        if (!isPaused) {
            isPaused = true;
            isRunning = false;
            System.out.println("The complex task is paused.");

            // עצירת כל תת-משימה
            for (SimpleTask subTask : subTasks) {
                subTask.pause();
            }
        } else {
            System.out.println("The complex task is already paused.");
        }
    }

    @Override
    public void resume() throws IllegalStateException {
        if (isRunning) {
            throw new IllegalStateException("Cannot resume a task that is already running.");
        }

        if (!isPaused) {
            throw new IllegalStateException("The complex task is not paused.");
        }

        isPaused = false;
        isRunning = true;
        System.out.println("Resuming the complex task.");

        // חידוש כל תת-משימה בצורה מסונכרנת עם המשימה הראשית
        for (SimpleTask subTask : subTasks) {
            if (subTask.isPaused()) {
                subTask.resume();
            }
        }
    }

    public void calculateTotalDuration() {
        totalDuration = 0;
        for (SimpleTask subTask : subTasks) {
            totalDuration += subTask.getDuration();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        while (elapsedTime < totalDuration && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
                elapsedTime++;
                if (elapsedTime >= totalDuration) {
                    completed = true;
                    for (SimpleTask subTask : subTasks) {
                        subTask.Complete();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("The complex task was interrupted.");
            } catch (Exception e) {
                System.out.println("Error during the execution of the complex task: " + e.getMessage());
            }
        }
    }
}
