package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.User.UserType.*;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE   //псевдоним экстеншена
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    //создаем мапу из трех очередей
    private static Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();
    static {
        Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationSentQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationReceivedQueue = new ConcurrentLinkedQueue<>();

        friendsQueue.add(user("bee", "12345", WITH_FRIENDS, "user_with_friend"));
        friendsQueue.add(user("user_with_friend", "12345", WITH_FRIENDS, "bee"));

        invitationSentQueue.add(user("user1", "12345", INVITATION_SENT, "user2"));
        invitationSentQueue.add(user("user_with_invitaion_send", "12345",
                INVITATION_SENT, "user_with_invitation_received"));

        invitationReceivedQueue.add(user("user2", "12345", INVITATION_RECEIVED, "user1"));
        invitationReceivedQueue.add(user("user_with_invitation_received", "12345",
                INVITATION_RECEIVED, "user_with_invitaion_send"));

        users.put(WITH_FRIENDS, friendsQueue);
        users.put(INVITATION_SENT, invitationSentQueue);
        users.put(INVITATION_RECEIVED, invitationReceivedQueue);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        //создаем список для всех методов, которые найдем в тестовом классе
        List<Method> allTestMethods = new ArrayList<>();

        Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())//получаем все методы, которые есть в тестовом классе
                .filter(x -> x.isAnnotationPresent(BeforeEach.class)) //выбираем из них те методы, над которыми стоит аннотация BeforeEach
                .forEach(allTestMethods::add);//добавляем их в список
        allTestMethods.add(extensionContext.getRequiredTestMethod()); //Добавляем тестовые методы в список.

        List<Parameter> parameters = allTestMethods.stream().map(x -> x.getParameters())//в списке методов, созданном выше,выбираем все, куда передаются параметры
                .flatMap(Arrays::stream)
                .filter(parameter -> parameter.isAnnotationPresent(User.class)) //выбираем из них те,у которых есть аннотация @User
                .filter(parameter -> parameter.getType().isAssignableFrom(UserJson.class)) //дополнительно проверяем, что в качестве параметра передается UserJson.class
                .toList();

        Map<User.UserType, UserJson> testUsers = new HashMap<>(); //создаем мапу для юзеров с определенным типом, которые будут использоваться в тесте

        for (Parameter parameter : parameters) {
            User annotation = parameter.getAnnotation(User.class); //достаем у каждого параметра @User
            if(testUsers.containsKey(annotation.value())) { //если в testUser уже есть ключ с типом пользователя из аннотации, то возвращаемся к началу цикла и проверяем следующий параметр
                continue;
            }
            Queue<UserJson> queue = users.get(annotation.value()); //берем из мапы с очередями ту очередь, у которой задан тот же тип пользователя, что и в аннотации
            UserJson testCandidate = null;
            while (testCandidate == null) {
                testCandidate = queue.poll(); //вытаскиваем из очереди одного из юзеров для теста
            }
            testUsers.put(annotation.value(), testCandidate); //кладем этого пользователя в testUsers
        }
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), testUsers);

        }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Map<User.UserType, UserJson> usersFromTest = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);
        for (User.UserType userType : usersFromTest.keySet()) {
            users.get(userType).add(usersFromTest.get(userType));
        }
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
        return (UserJson) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.findAnnotation(User.class).get().value());
    }

    private static UserJson user(String username, String password, User.UserType userType, String friendUserName) {
        return new UserJson(
                null,
                username,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                new TestData(password, friendUserName, userType)
        );
    }



}
