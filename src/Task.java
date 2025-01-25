public interface Task {
    void Start();
    void Complete();
    void Cancel();
    boolean isComplete();
    String getName();
    int getDuration();
    // הוספת השיטות הדרושות
    boolean isRunning();
    void pause();
    boolean isPaused();
    void resume();
}
