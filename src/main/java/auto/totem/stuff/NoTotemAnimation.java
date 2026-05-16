package auto.totem.stuff;

public class NoTotemAnimation {

    public static boolean AnimationToggled = false;

    public static boolean enabled() {
        return AnimationToggled;
    }

    public static void toggle() {
        AnimationToggled = !AnimationToggled;
    }
}