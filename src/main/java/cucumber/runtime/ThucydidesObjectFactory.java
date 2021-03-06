package cucumber.runtime;

import cucumber.runtime.CucumberException;
import cucumber.runtime.java.ObjectFactory;
import net.thucydides.core.Thucydides;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Thucydides factory for cucumber tests.
 *
 * @author Liviu Carausu (liviu.carausu@gmail.com).
 */

public class ThucydidesObjectFactory implements ObjectFactory {

    private final Set<Class<?>> classes = Collections.synchronizedSet(new HashSet<Class<?>>());

    private final Map<Class<?>, Object> instances = Collections.synchronizedMap(new HashMap<Class<?>, Object>());

    public void start() {
    }

    public void stop() {
        instances.clear();
    }

    public void addClass(Class<?> clazz) {
        classes.add(clazz);
    }

    public <T> T getInstance(Class<T> type) {
        T instance = type.cast(instances.get(type));
        if (instance == null) {
            instance = cacheNewInstance(type);
        }
        return instance;
    }

    /**
     * Tries to instantiate the type using an empty constructor, if it does not work, tries to instantiate
     * using a constructor wit a Pages parameter.
     * @param type
     * @param <T>
     * @return
     */
    private <T> T cacheNewInstance(Class<T> type) {
        T instance;
        try {
            Constructor<T> constructor = type.getConstructor();
            instance = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            instance = createNewPageEnabledStepCandidate(type);
        } catch (Exception e) {
            throw new CucumberException(String.format("Failed to instantiate %s", type), e);
        }
        Thucydides.initializeWithNoStepListener(instance);
        instances.put(type, instance);
        return instance;
    }

    private <T> T createNewPageEnabledStepCandidate(final Class<T> type) {
        T newInstance = null;
        try {
            Pages pageFactory = ThucydidesWebDriverSupport.getPages();
            Class[] constructorArgs = new Class[1];
            constructorArgs[0] = Pages.class;
            Constructor<T> constructor = type.getConstructor(constructorArgs);
            newInstance = constructor.newInstance(pageFactory);
        } catch (Exception e) {
            throw new CucumberException(String.format("%s doesn't have an empty or a page enabled constructor.", type), e);
        }
        return newInstance;
    }


}
