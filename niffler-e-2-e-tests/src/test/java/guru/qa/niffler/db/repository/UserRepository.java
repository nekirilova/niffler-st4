package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    UserAuthEntity createInAuth(UserAuthEntity user);
    UserEntity createInUserData(UserEntity user);

    void deleteInAuthById(UUID id);
    void deleteInUserDataById(UUID id);
    UserAuthEntity updateInAuth(UserAuthEntity user);
    UserEntity updateInUserData(UserEntity user);
    Optional<UserAuthEntity> readInAuth(UUID id);
    Optional<UserEntity> readInUserData(UUID id);
}
