package rx.plugins;

@SuppressWarnings("unused")
public class RxJavaTestPlugins extends RxJavaPlugins {
    RxJavaTestPlugins() {
        super();
    }

    public static void resetPlugins(){
        getInstance().reset();
    }
}

