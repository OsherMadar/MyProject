import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TaskManager {
    private JFrame frame;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextArea taskDescriptionArea;
    private ArrayList<Task> tasks;
    private JPanel progressPanel;
    private JScrollPane scrollPane;

    public TaskManager() {
        tasks = new ArrayList<>();

        frame = new JFrame("Task Board with Timer");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.addListSelectionListener(e -> showTaskDetails());
        JScrollPane taskListScrollPane = new JScrollPane(taskList);

        taskDescriptionArea = new JTextArea();
        taskDescriptionArea.setEditable(false);
        JScrollPane descriptionScrollPane = new JScrollPane(taskDescriptionArea);

        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(progressPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> addTask());

        buttonPanel.add(addTaskButton);

        frame.add(taskListScrollPane, BorderLayout.WEST);
        frame.add(descriptionScrollPane, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.EAST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        System.out.println("Task Manager initialized.");
    }

    private void addTask() {
        Object[] options = {"Simple Task", "Complex Task", "Morning Routine", "Night Routine"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Select task type",
                "Task Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Simple Task
                createSimpleTask();
                break;
            case 1: // Complex Task
                createComplexTask();
                break;
            case 2: // Morning Routine
                createRoutine(new MorningRoutine("Morning Routine", "Start your day with these tasks"));
                break;
            case 3: // Night Routine
                createRoutine(new NightRoutine("Night Routine", "Wind down your day with these tasks"));
                break;
            default:
                break;
        }
    }

    private void createSimpleTask() {
        String name = JOptionPane.showInputDialog(frame, "Enter task name:");
        String description = JOptionPane.showInputDialog(frame, "Enter task description:");
        String timeInput = JOptionPane.showInputDialog(frame, "Enter task duration (in seconds):");

        if (name != null && description != null && timeInput != null) {
            try {
                int duration = Integer.parseInt(timeInput);
                SimpleTask task = new SimpleTask(name, description, duration);
                tasks.add(task);
                taskListModel.addElement(task.getName());
                addTaskProgress(task);
                System.out.println("Simple Task added: " + name);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid duration! Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Task creation cancelled.");
        }
    }

    private void createComplexTask() {
        String name = JOptionPane.showInputDialog(frame, "Enter complex task name:");
        String description = JOptionPane.showInputDialog(frame, "Enter complex task description:");

        if (name != null && description != null) {
            ComplexTask complexTask = new ComplexTask(name, description);
            tasks.add(complexTask);
            taskListModel.addElement(complexTask.getName());
            System.out.println("Complex Task added: " + name);

            while (true) {
                String subTaskName = JOptionPane.showInputDialog(frame, "Enter subtask name (or Cancel to finish):");
                if (subTaskName == null) break;

                String subTaskDescription = JOptionPane.showInputDialog(frame, "Enter subtask description:");
                String subTaskTimeInput = JOptionPane.showInputDialog(frame, "Enter subtask duration (in seconds):");

                if (subTaskDescription != null && subTaskTimeInput != null) {
                    try {
                        int subTaskDuration = Integer.parseInt(subTaskTimeInput);
                        SimpleTask subTask = new SimpleTask(subTaskName, subTaskDescription, subTaskDuration);
                        complexTask.addSubTask(subTask);
                        System.out.println("Subtask added to " + name + ": " + subTaskName);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, "Invalid duration! Please enter a valid number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Subtask creation cancelled.");
                    break;
                }
            }
            addComplexTaskProgress(complexTask);
        } else {
            JOptionPane.showMessageDialog(frame, "Complex task creation cancelled.");
        }
    }

    private void createRoutine(Task routineTask) {
        tasks.add(routineTask);
        taskListModel.addElement(routineTask.getName());
        addComplexTaskProgress((ComplexTask) routineTask);
    }

    private void addTaskProgress(SimpleTask task) {
        JProgressBar progressBar = task.getProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString(task.getName());

        JPanel taskPanel = createTaskPanel(task, progressBar);

        progressPanel.add(taskPanel);
        progressPanel.revalidate();
    }

    private JPanel createTaskPanel(SimpleTask task, JProgressBar progressBar) {
        JPanel taskPanel = new JPanel(new BorderLayout());
        taskPanel.add(progressBar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        setButtonActions(task, startButton, pauseButton, resumeButton);

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);

        taskPanel.add(buttonPanel, BorderLayout.SOUTH);

        return taskPanel;
    }

    private void setButtonActions(SimpleTask task, JButton startButton, JButton pauseButton, JButton resumeButton) {
        startButton.setEnabled(!task.isRunning());
        startButton.addActionListener(e -> {
            task.Start();
            System.out.println("Task started: " + task.getName());
            updateButtonStates(startButton, pauseButton, resumeButton, false, true, false);
        });

        pauseButton.addActionListener(e -> {
            task.pause();
            updateButtonStates(startButton, pauseButton, resumeButton, false, false, true);
        });

        resumeButton.addActionListener(e -> {
            task.resume();
            updateButtonStates(startButton, pauseButton, resumeButton, false, true, false);
        });
    }

    private void updateButtonStates(JButton startButton, JButton pauseButton, JButton resumeButton, boolean start, boolean pause, boolean resume) {
        startButton.setEnabled(start);
        pauseButton.setEnabled(pause);
        resumeButton.setEnabled(resume);
    }

    private void addComplexTaskProgress(ComplexTask complexTask) {
        for (SimpleTask subTask : complexTask.getSubTasks()) {
            addTaskProgress(subTask);
        }
    }

    private void showTaskDetails() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            Task selectedTask = tasks.get(selectedIndex);

            progressPanel.removeAll();
            if (selectedTask instanceof ComplexTask complexTask) {
                StringBuilder details = new StringBuilder(complexTask.toString());
                details.append("\nSubtasks:\n");

                for (SimpleTask subTask : complexTask.getSubTasks()) {
                    details.append(subTask.getName()).append(": ").append(subTask.getDescription()).append("\n");
                }

                taskDescriptionArea.setText(details.toString());
                addComplexTaskProgress(complexTask);
            } else {
                taskDescriptionArea.setText(selectedTask.toString());
                addTaskProgress((SimpleTask) selectedTask);
            }
            System.out.println("Task details shown: " + selectedTask.getName());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManager::new);
    }
}
