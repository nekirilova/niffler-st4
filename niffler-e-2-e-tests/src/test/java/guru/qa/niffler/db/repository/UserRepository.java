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
    UserAuthEntity updateInAuth(UUID id, UserAuthEntity user);
    UserEntity updateInUserData(UUID id, UserEntity user);
    Optional<UserAuthEntity> findByIdInAuth(UUID id);
    Optional<UserEntity> findByIdInUserData(UUID id);
}
