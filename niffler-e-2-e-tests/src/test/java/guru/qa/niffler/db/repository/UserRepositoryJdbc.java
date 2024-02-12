package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Optional;
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
                    "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.executeUpdate();

                UUID userId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can't find id");
                    }
                }
                user.setId(userId);
            }
            } catch (SQLException e){
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
                         "WHERE id = ?");
            PreparedStatement friendPs = connection.prepareStatement("DELETE FROM \"friendship\" " +
                    "WHERE user_id = ?");
                 PreparedStatement invitedFriendPs = connection.prepareStatement("DELETE FROM \"friendship\" " +
                         "WHERE friend_id = ?");
            )
            {
                ps.setObject(1, id);
                friendPs.setObject(1, id);
                invitedFriendPs.setObject(1, id);
                ps.executeUpdate();
                friendPs.executeUpdate();
                invitedFriendPs.executeUpdate();
                connection.commit();
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
    public UserAuthEntity updateInAuth(UserAuthEntity user) {
        try(Connection connection = authDs.getConnection()) {
            try (PreparedStatement userPs = connection.prepareStatement("UPDATE \"user\" " +
                    "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                    "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?"))
            {
                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());
                userPs.setObject(7, user.getId());

                userPs.executeUpdate();
                connection.commit();
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
    public UserEntity updateInUserData(UserEntity user) {
        try(Connection connection = udDs.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("UPDATE \"user\" " +
                    "SET username = ?, currency = ?, firstname = ?, surname = ?, " +
                    "photo = ? WHERE id = ?");
                 PreparedStatement deleteFromAuthority = connection.prepareStatement(
                         "DELETE FROM \"authority\" where user_id = ?");
                 PreparedStatement updateAuthority = connection.prepareStatement(
                         "INSERT INTO \"authority\" (user_id, authority) " +
                                 "VALUES (?, ?)")
            )
            {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setString(3, user.getFirstname());
                ps.setString(4, user.getSurname());
                ps.setBytes(5, user.getPhoto());
                ps.setObject(6, user.getId());
                ps.executeUpdate();

                deleteFromAuthority.setObject(1, user.getId());
                deleteFromAuthority.executeUpdate();

                for (Authority authority : Authority.values()) {
                    updateAuthority.setObject(1, user.getId());
                    updateAuthority.setString(2, authority.name());
                    updateAuthority.addBatch();
                    updateAuthority.clearParameters();
                }
                updateAuthority.executeBatch();
                connection.commit();
                return user;
            } catch (Exception e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserAuthEntity> findByIdInAuth(UUID id) {
       try(Connection connection = authDs.getConnection();
           PreparedStatement userPs = connection.prepareStatement("SELECT * FROM \"user\" u " +
                   "JOIN \"authority\" a ON a.user_id = u.id" +
                    "WHERE u.id = ?")){
                userPs.setObject(1, id);
                userPs.execute();
                UserAuthEntity user = new UserAuthEntity();
                boolean userIsProcessed = false;
                try(ResultSet result = userPs.getResultSet()) {
                    while (result.next()) {
                        if (!userIsProcessed) {
                            user.setId(result.getObject(1, UUID.class));
                            user.setUsername(result.getString(2));
                            user.setPassword(result.getString(3));
                            user.setEnabled(result.getBoolean(4));
                            user.setAccountNonExpired(result.getBoolean(5));
                            user.setAccountNonLocked(result.getBoolean(6));
                            user.setCredentialsNonExpired(result.getBoolean(7));
                            userIsProcessed = true;
                        }
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setId(result.getObject(8, UUID.class));
                        authority.setAuthority(Authority.valueOf(result.getString(10)));
                        user.getAuthorities().add(authority);
                    }
                }
                    return userIsProcessed
                            ? Optional.of(user)
                            : Optional.empty();
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }

        @Override
    public Optional<UserEntity> findByIdInUserData(UUID id) {
        UserEntity user = UserEntity.builder().build();
        try(Connection connection = udDs.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT FROM \"user\" " +
                    "WHERE id = ?")){
                ps.setObject(1, user.getId());
                ps.execute();
                try(ResultSet result = ps.getResultSet()) {
                    if (result.next()) {
                        user.setId(result.getObject("id", UUID.class));
                        user.setUsername(result.getString("username"));
                        user.setCurrency(CurrencyValues.valueOf(result.getString("currency")));
                        user.setFirstname(result.getString("firstname"));
                        user.setSurname(result.getString("surname"));
                        user.setPhoto(result.getBytes("photo"));
                    } else {
                        return Optional.empty();
                    }
                }
            } catch(SQLException sqlException){
                throw new RuntimeException(sqlException);
            }
        return Optional.of(user);
    }

}
