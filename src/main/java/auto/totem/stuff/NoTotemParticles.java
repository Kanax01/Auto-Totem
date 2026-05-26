package auto.totem.stuff;

public class NoTotemParticles {

    public static boolean ParticlesToggled = false;

    public static boolean enabled() {
        return ParticlesToggled;
    }

    public static void toggle() {
        ParticlesToggled = !ParticlesToggled;
    }
}