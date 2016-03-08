package rx;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.plugins.RxJavaTestPlugins;
import rx.schedulers.Schedulers;

@SuppressWarnings("unused")
public class RxJavaTestRunner extends RobolectricGradleTestRunner {
    public RxJavaTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        RxJavaTestPlugins.resetPlugins();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }
}
