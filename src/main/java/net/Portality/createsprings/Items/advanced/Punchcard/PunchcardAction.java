package net.Portality.createsprings.Items.advanced.Punchcard;

public class PunchcardAction {
    public String name;
    public String parameter;

    public PunchcardAction(String name, String parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public String getName(){
        return name;
    }

    public String getParameter(){
        return parameter;
    }

    public static PunchcardAction getAllFromString(String string){
        String[] parts = string.split(":");

        if(string.isEmpty()){return new PunchcardAction(" ", " ");}
        if(parts.length < 2){return new PunchcardAction(" ", " ");}

        String name = parts[0];
        String parameter = parts[1];

        return new PunchcardAction(name, parameter);
    }

    public static String putPunchcardActionInString(PunchcardAction action){
        return action.name + ":" +
                action.parameter;
    }
}
