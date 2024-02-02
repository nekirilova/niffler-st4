package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository{
    private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH);
    private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA);

    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Override
    public UserAuthEntity createInAuth(UserAuthEntity user) {
        try(Connection connection = authDs.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement userPs = connection.prepareStatement("INSERT INTO \"user\" " +
                    "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement authorityPs = connection.prepareStatement("INSERT INTO \"authority\" " +
                         "(user_id, authority) " +
                         "VALUES (?, ?)"))
            {
                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());

                userPs.executeUpdate();

                UUID authUserId;
                try(ResultSet keys = userPs.getGeneratedKeys()){
                    if(keys.next()) {
                        authUserId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw  new IllegalStateException("Can't find id");
                    }
                }

                for (Authority value : Authority.values()) {
                    authorityPs.setObject(1, authUserId);
                    authorityPs.setString(2, value.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }
                authorityPs.executeBatch();
                connection.commit();
                user.setId(authUserId);
            } catch (Exception e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public UserEntity createInUserData(UserEntity user) {
        try(Connection connection = udDs.getConnection()) {

            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\" " +
                    "(username, currency) " +
                    "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS))
            {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());

                ps.executeUpdate();

                UUID userId;
                try(ResultSet keys = ps.getGeneratedKeys()){
                    if(keys.next()) {
                        userId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw  new IllegalStateException("Can't find id");
                    }
                }
                user.setId(userId);
            } catch (Exception e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public void deleteInAuthById(UUID id) {
        try(Connection connection = authDs.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement authorityPs = connection.prepareStatement("DELETE FROM \"authority\" " +
                    "WHERE user_id = ?");
                 PreparedStatement userPs = connection.prepareStatement("DELETE FROM \"user\" " +
                         "WHERE id = ?"))
            {
                authorityPs.setObject(1, id);
                authorityPs.executeUpdate();
                userPs.setObject(1, id);
                userPs.executeUpdate();

            } catch (Exception e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteInUserDataById(UUID id) {
        try(Connection connection = udDs.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM \"user\" " +
                         "WHERE id = ?"))
            {
                ps.setObject(1, id);
                ps.executeUpdate();

            } catch (Exception e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
