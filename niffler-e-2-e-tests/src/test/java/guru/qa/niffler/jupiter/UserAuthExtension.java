package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositorySJdbc;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;

public class UserAuthExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserAuthExtension.class);
    private final UserRepository userRepository = new UserRepositorySJdbc();
    private final Faker faker  = new Faker();
    static String userAuthDataKey = "userAuthDataKey";
    static String userDataKey = "userDataKey";


    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        Optional<DbUser> userDb = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class);

        if (userDb.isPresent()) {
            String username;
            String password;
            Map<String, Object> usersData = new HashMap<>();

            if (!userDb.get().username().isEmpty() && !userDb.get().password().isEmpty()) {
                username = userDb.get().username();
                password = userDb.get().password();
            } else {
                username = faker.funnyName().name();
                password = faker.internet().password();
            }
           UserAuthEntity userAuth = new UserAuthEntity();
                   userAuth.setUsername(username);
                   userAuth.setPassword(password);
                   userAuth.setEnabled(true);
                   userAuth.setCredentialsNonExpired(true);
                   userAuth.setAccountNonLocked(true);
                   userAuth.setAccountNonExpired(true);
                   userAuth.setAuthorities(Arrays.stream(Authority.values())
                            .map(e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setAuthority(e);
                                return ae;
                            }).toList());

            UserEntity user = UserEntity.builder()
                    .username(username)
                    .currency(CurrencyValues.RUB)
                    .build();
            UUID userAuthId = userRepository.createInAuth(userAuth).getId();
            userAuth.setId(userAuthId);
            UUID userDataId = userRepository.createInUserData(user).getId();
            user.setId(userDataId);

            usersData.put(userAuthDataKey, userAuth);
            usersData.put(userDataKey, user);

            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), usersData);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        Map usersMap = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        userRepository.deleteInAuthById(((UserAuthEntity) usersMap.get(userAuthDataKey)).getId());
        userRepository.deleteInUserDataById(((UserEntity) usersMap.get(userDataKey)).getId());
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Map users = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        return (UserAuthEntity) users.get(userAuthDataKey);
    }
}
