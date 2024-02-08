package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.db.sjdbc.UserAuthEntityResultSetExtractor;
import guru.qa.niffler.db.sjdbc.UserEntityRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepositorySJdbc implements UserRepository{
    private final TransactionTemplate authTxt;
    private final TransactionTemplate udTxt;
    private final JdbcTemplate authTemplate;
    private final JdbcTemplate udTemplate;
    public UserRepositorySJdbc(){
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH));
        JdbcTransactionManager udTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA));
        this.authTxt = new TransactionTemplate(authTm);
        this.udTxt = new TransactionTemplate(udTm);
        this.authTemplate = new JdbcTemplate(authTm.getDataSource());
        this.udTemplate = new JdbcTemplate(udTm.getDataSource());
    }
    @Override
    public UserAuthEntity createInAuth(UserAuthEntity user) {
        return null;
    }

    @Override
    public UserEntity createInUserData(UserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        udTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            return ps;
        }, kh);

        user.setId((UUID)kh.getKeys().get("id"));
        return user;
    }

    @Override
    public void deleteInAuthById(UUID id) {
        authTxt.execute(status -> {
            authTemplate.update("DELETE FROM \"authority\" WHERE user_id = ?", id);
            authTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
            return null;
        });
    }

    @Override
    public void deleteInUserDataById(UUID id) {
        udTxt.execute(status -> {
            udTemplate.update("DELETE FROM \"friendship\" WHERE user_id = ?", id);
            udTemplate.update("DELETE FROM \"friendship\" WHERE friend_id = ?", id);
            udTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
            return null;
        });
    }

    @Override
    public UserAuthEntity updateInAuth(UserAuthEntity user) {
        return null;
    }

    @Override
    public UserEntity updateInUserData(UserEntity user) {
        return null;
    }

    @Override
    public Optional<UserAuthEntity> readInAuth(UUID id) {
        try{
            return Optional.ofNullable(
                    authTemplate.query("SELECT * FROM \"user\" u " +
                            "JOIN \"authority\" a ON a.user_id = u.id" +
                            "WHERE u.id = ?",
                    UserAuthEntityResultSetExtractor.instance,
                            id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserEntity> readInUserData(UUID id) {
        try{
        return Optional.ofNullable(udTemplate.queryForObject("SELECT FROM \"user\" WHERE id = ?",
                UserEntityRowMapper.instance, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
