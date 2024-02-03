package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Optional;

public class UserAuthExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserAuthExtension.class);
    private final UserRepository userRepository = new UserRepositoryJdbc();
    private UserAuthEntity userAuth;
    private UserEntity user;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<DbUser> userDb = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class);

        if (userDb.isPresent()) {
            String username;
            String password;
            if (!userDb.get().username().isEmpty() && !userDb.get().password().isEmpty()) {
                username = userDb.get().username();
                password = userDb.get().password();
            } else {
                username = new Faker().funnyName().name();
                password = new Faker().internet().password();
            }
            userAuth = UserAuthEntity.builder()
                    .username(username)
                    .password(password)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .authorities(Arrays.stream(Authority.values())
                            .map(e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setAuthority(e);
                                return ae;
                            }).toList())
                    .build();
            user = UserEntity.builder()
                    .username(username)
                    .currency(CurrencyValues.RUB)
                    .build();
            userRepository.createInAuth(userAuth);
            userRepository.createInUserData(user);
            extensionContext.getStore(NAMESPACE)
                    .put("userAuth", userAuth);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        userRepository.deleteInAuthById(userAuth.getId());
        userRepository.deleteInUserDataById(user.getId());
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(UserAuthExtension.NAMESPACE)
                .get("userAuth", UserAuthEntity.class);
    }
}
