package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserRepository {
    UserAuthEntity createInAuth(UserAuthEntity user);
    UserEntity createInUserData(UserEntity user);

    void deleteInAuthById(UUID id);
    void deleteInUserDataById(UUID id);
    UserAuthEntity updateInAuth(UserAuthEntity user);
    UserEntity updateInUserData(UserEntity user);
    UserAuthEntity readInAuth(UserAuthEntity user);
    UserEntity readInUserData(UserEntity user);
}
