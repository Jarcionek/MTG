package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class CreateToken extends Action {
    public boolean red;
    public boolean blue;
    public boolean white;
    public boolean black;
    public boolean green;
    public String name;
    public String type;
    public String desc;
    public boolean creature;
    public int atk;
    public int def;
    
    public String cardID;

    public CreateToken(boolean red, boolean blue, boolean white, boolean black,
            boolean green, String name, String type, String desc,
            boolean creature, int atk, int def) {
        super(-1);
        this.red = red;
        this.blue = blue;
        this.white = white;
        this.black = black;
        this.green = green;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.creature = creature;
        this.atk = atk;
        this.def = def;
        this.cardID = null;
    }

    public String toString() {
        return super.toString() + ", red = " + red + ", blue = " + blue
                + ", white = " + white + ", black = " + black + ", green = "
                + green + ", name = " + name + ", type = " + type + ", desc = "
                + desc.replaceAll("\n", "/") + ", creature = " + creature
                + ", atk = " + atk + ", def = " + def + ", cardID = "
                + cardID + ")";
    }
    
}
