package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import nl.pts4.security.HashConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseController {
    private DataSource dataSource;

    private static DatabaseController instance;

    public static DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
            instance.setDefaultDataSource();
        }
        return instance;
    }

    public static DatabaseController getTestInstance() {
        instance = getInstance();
        instance.setupDefaultTestDataSource();

        return instance;
    }

    private void setDefaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DatabaseCredentials.Driver);
        dataSource.setUrl(DatabaseCredentials.Url);
        dataSource.setUsername(DatabaseCredentials.Username);
        dataSource.setPassword(DatabaseCredentials.Authentication);

        this.dataSource = dataSource;
    }

    private void setupDefaultTestDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DatabaseCredentials.Driver);
        dataSource.setUrl(DatabaseCredentials.TestUrl);
        dataSource.setUsername(DatabaseCredentials.TestUsername);
        dataSource.setPassword(DatabaseCredentials.Authentication);

        this.dataSource = dataSource;
    }

    public boolean createTables() {
        Resource resource = new ClassPathResource("create_tables.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(resource);
        try {
            databasePopulator.populate(dataSource.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public AccountModel createAccount(String name, String email, String password) {
        UUID accountUUID = UUID.randomUUID();

        JdbcTemplate insert = new JdbcTemplate(dataSource);
        insert.update("INSERT INTO account (id, name, email, hash) VALUES (?,?,?,?)", accountUUID, name, email, SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p));

        return getAccount(accountUUID);
    }

    public boolean setAccountEmail(AccountModel ac, String email) {
        ac.setEmail(email);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET email = ? WHERE id = ?", email, ac.getUuid()) == 1;
    }

    public boolean setAccountName(AccountModel ac, String name) {
        ac.setName(name);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET name = ? WHERE id = ?", name, ac.getUuid()) == 1;
    }

    public AccountModel getAccount(final UUID uuid) {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            AccountModel am = select.
                    queryForObject("SELECT id, oauthkey, oauthprovider, name, email, hash, active, type FROM account WHERE id = ?",
                            new Object[]{uuid}
                            , (resultSet, i) -> {
                                return getAccountFromResultSet(resultSet);
                            });
            return am;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AccountModel getAccount(final String email) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        AccountModel am;
        try {
            am = select.
                    queryForObject("SELECT id, email, oauthkey, oauthprovider, name, hash, active, type FROM account WHERE email = ?",
                            new Object[]{email},
                            (resultSet, i) -> {
                                return getAccountFromResultSet(resultSet);
                            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return am;
    }

    public List<OrderModel> getAllOrders() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        ArrayList<OrderModel> orderModels = select.queryForObject("SELECT id, accountid,orderdate FROM order_;", (resultSet, i) -> {
            ArrayList<OrderModel> orderModels1 = new ArrayList<>(i);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                UUID accountid = UUID.fromString(resultSet.getString("accountid"));
                Date orderDate = resultSet.getDate("orderdate");

                OrderModel orderModel = new OrderModel(id, orderDate, getAccount(accountid));

                orderModels1.add(orderModel);
            }

            return orderModels1;
        });
        return orderModels;
    }

    public void createUserCookie(AccountModel user, UUID cookieuuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        template.update("INSERT INTO user_cookie (id, account) VALUES (?, ?)", cookieuuid, user.getUuid());
    }

    public AccountModel getAccountByCookie(final String cookie) {
        if (cookie == null) return null;
        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            AccountModel am = template.queryForObject("SELECT a.id, a.oauthkey, a.oauthprovider, a.name, a.email, a.hash, a.active, a.type FROM account a, user_cookie uc WHERE a.id = uc.account AND uc.id = ?", new Object[]{UUID.fromString(cookie)}, new RowMapper<AccountModel>() {
                @Override
                public AccountModel mapRow(ResultSet resultSet, int i) throws SQLException {
                    return getAccountFromResultSet(resultSet);
                }
            });
            return am;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean deleteAccount(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) throw new IllegalArgumentException ("Invalid UUID");

        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            template.update("DELETE FROM account a WHERE a.id = ?", uuid);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public AccountModel getAccountByCookie(Cookie cookie) {
        return getAccountByCookie(cookie.getValue());
    }

    private AccountModel getAccountFromResultSet(ResultSet resultSet) {
        try {
            UUID uuid = UUID.fromString(resultSet.getString("id"));
            String oauthkey = resultSet.getString("oauthkey");
            String oauthprovider = resultSet.getString("oauthprovider");

            AccountModel.OAuthProviderEnum oAuthProvider = null;
            if (oauthprovider != null)
                oAuthProvider = AccountModel.OAuthProviderEnum.valueOf(oauthprovider);

            String name = resultSet.getString("name");
            String hash = resultSet.getString("hash");
            boolean active = resultSet.getBoolean("active");

            String type = resultSet.getString("type");
            String email = resultSet.getString("email");
            AccountModel.AccountTypeEnum accountTypeEnum = null;
            if (type != null)
                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

            return new AccountModel(uuid, oauthkey, oAuthProvider, name, email, hash, active, accountTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
