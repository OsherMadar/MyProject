public class MorningRoutine extends ComplexTask{

    public MorningRoutine(String complextaskName, String complextaskDescription){
        super(complextaskName, complextaskDescription);
        addSubTask(new SimpleTask("Hygiene treatments" , "Brushing teeth, washing face, using the restroom, and similar activities.", 900));
        addSubTask(new SimpleTask("Brachot Hashachar","Thank the Creator for waking us up in the morning.", 600));
        addSubTask(new SimpleTask("Breakfast","A meal of your choice, recommended with a hot drink on the side.", 1500));
        addSubTask(new SimpleTask("Getting ready to go out.","Makeup, hair, clothes...", 1800));
    }
}
