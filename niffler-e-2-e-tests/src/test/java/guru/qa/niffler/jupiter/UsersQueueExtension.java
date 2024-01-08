package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.User.UserType.COMMON;
import static guru.qa.niffler.jupiter.User.UserType.WITH_FRIENDS;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();
    static {
        Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();
        friendsQueue.add(user("dima", "12345", WITH_FRIENDS));
        friendsQueue.add(user("duck", "12345", WITH_FRIENDS));

        commonQueue.add(user("bee", "12345", COMMON));
        commonQueue.add(user("barsik", "12345", COMMON));



        users.put(WITH_FRIENDS, friendsQueue);
        users.put(User.UserType.COMMON, commonQueue);

    }


    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Parameter[] parameters = extensionContext.getRequiredTestMethod().getParameters();
//        Method[] methods = extensionContext.getRequiredTestClass().getMethods();
//        for (Method method : methods) {
//            if(method.isAnnotationPresent(BeforeEach.class)){
//                method.getParameters();
//            }
//        }
        for (Parameter parameter : parameters) {
            User annotation = parameter.getAnnotation(User.class);
            if(annotation != null && parameter.getType().isAssignableFrom(UserJson.class)) {
                UserJson testCandidate = null;
                Queue<UserJson> queue = users.get(annotation.value());
                while (testCandidate == null) {
                    testCandidate = queue.poll();
                }
                extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), testCandidate);
                break;
            }

        }
    }
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        UserJson userFromTest = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserJson.class);
        users.get(userFromTest.testData().userType()).add(userFromTest);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) &&
                parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserJson.class);
    }

    private static UserJson user(String username, String password, User.UserType userType) {
        return new UserJson(
                null,
                username,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                new TestData(password, userType)
        );
    }


}
