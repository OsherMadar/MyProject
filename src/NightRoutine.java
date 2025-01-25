public class NightRoutine extends ComplexTask{

    public NightRoutine(String complextaskName, String complextaskDescription){
        super(complextaskName, complextaskDescription);
        addSubTask(new SimpleTask("Shower" , "To clean off all the dirt from the day.", 600));
        addSubTask(new SimpleTask("Pajamas","Put on clothes that are comfortable to sleep in.", 300));
        addSubTask(new SimpleTask("Skin care","Wash your face well, apply creams and masks, Brush your teeth..", 600));
        addSubTask(new SimpleTask("Shema Israel","To say the \"Shema Yisrael\" from the siddur before going to bed.", 600));
    }
}
