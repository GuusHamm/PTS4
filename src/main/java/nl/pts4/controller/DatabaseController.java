package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.*;
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
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

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

    public boolean setAccountHash(AccountModel ac, String hash) {
        ac.setHash(hash);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET hash = ? WHERE id = ?", hash, ac.getUuid()) == 1;
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

//    public List<OrderModel> getAllOrders() {
//        JdbcTemplate select = new JdbcTemplate(dataSource);
//
//        ArrayList<OrderModel> orderModels = select.queryForObject("SELECT id, accountid,orderdate FROM order_;", (resultSet, i) -> {
//            ArrayList<OrderModel> orderModels1 = new ArrayList<>(i);
//
//            //This code is to make sure we don't crash when not getting any rows.
//            if (resultSet == null) {
//                return null;
//            }
//
//            do {
//                int id = resultSet.getInt("id");
//                UUID accountid = UUID.fromString(resultSet.getString("accountid"));
//                Date orderDate = resultSet.getDate("orderdate");
//
//                OrderModel orderModel = new OrderModel(id, orderDate, getAccount(accountid));
//
//                orderModels1.add(orderModel);
//            } while (resultSet.next());
//
//            return orderModels1;
//        });
//        return orderModels;
//    }

    public List<OrderModel> getAllOrders() {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = template.queryForList("SELECT o.id, o.accountid, o.orderdate FROM order_ o");
        List<AccountModel> accountModels = new ArrayList<>(rows.size());
        List<OrderModel> orderModels = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            UUID account = (UUID) row.get("accountid");
            Date orderDate = (Date) row.get("orderdate");

            AccountModel am;
            Optional<AccountModel> op = accountModels.stream().filter(o -> o.getUuid() == account).findAny();
            if (op.isPresent()) {
                am = op.get();
            }else{
                am = getAccount(account);
                accountModels.add(am);
            }
            orderModels.add(new OrderModel(id, orderDate, am));
        }

        rows = template.queryForList("SELECT o.id, o.orderid, o.photoconfigurationid FROM orderline o");
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            int orderid = (int) row.get("orderid");
            int photoConfigurationId = (int) row.get("photoconfigurationid");
            OrderLineModel olm = new OrderLineModel(id, orderid, photoConfigurationId);

            Optional<OrderModel> o = orderModels.stream().filter(a -> a.getId() == orderid).findAny();
            if (o.isPresent()) {
                o.get().getOrderLineModels().add(olm);
            }
        }

        return orderModels;
    }

    public List<OrderLineModel> getAllOrderLinesByOrderId(int orderID) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        ArrayList<OrderLineModel> orderlineModels;
        try {
            orderlineModels = select.queryForObject("SELECT id, orderid,photoconfigurationid FROM orderline WHERE orderid =" + orderID + ";", (resultSet, i) -> {
                ArrayList<OrderLineModel> orderLineModels1 = new ArrayList<OrderLineModel>(i);

                //This code is to make sure we don't crash when not getting any rows.
                if (resultSet == null) {
                    return null;
                }

                do {
                    int id = resultSet.getInt("id");
                    int orderid = resultSet.getInt("orderid");
                    int photoconfigItemId = resultSet.getInt("photoconfigurationid");

                    OrderLineModel orderLineModel = new OrderLineModel(id, orderid, photoconfigItemId);

                    orderLineModels1.add(orderLineModel);
                } while (resultSet.next());

                return orderLineModels1;
            });
        } catch (EmptyResultDataAccessException erdae) {
            Logger.getLogger(getClass().getName()).info("Empty result data; order id: " + orderID);
            return null;
        }



        return orderlineModels;
    }

    public List<PhotoModel> getPhotos() {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = template.queryForList("SELECT p.id, p.photographerid, p.childid, p.schoolid, p.price, p.capturedate, p.pathtophoto FROM photo p");
        List<PhotoModel> photoModels = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            UUID uuid = (UUID) row.get("id");
            UUID photographerid = (UUID) row.get("photographerid");
            UUID childid = (UUID) row.get("childid");
            int schoolid = (int) row.get("schoolid");
            int price = Integer.parseInt(String.valueOf(row.get("price")));
            Date captureDate = (Date) row.get("capturedate");
            String path = String.valueOf(row.get("pathtophoto"));
            photoModels.add(new PhotoModel(uuid, getAccount(photographerid), getAccount(childid), null, price, captureDate, path));
        }
        return photoModels;
    }

    public PhotoConfigurationModel getPhotoConfigurationModelById(int photoConfigid) {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        ArrayList<PhotoConfigurationModel> photoConfigurationModels = select.queryForObject("SELECT pc.id, pc.effectid,pc.itemid, p.id as photoid, p.price, p.capturedate, p.pathtophoto, p.photographerid, p.childid, p.schoolid FROM photoconfiguration pc, photo p WHERE p.id = pc.photoid AND pc.id = " + photoConfigid + ";", (resultSet, i) -> {
            ArrayList<PhotoConfigurationModel> photoConfigurationModels1 = new ArrayList<>(i);

            //This code is to make sure we don't crash when not getting any rows.
            if (resultSet == null) {
                return null;
            }

            do {
                int id = resultSet.getInt("id");
                int effectid = resultSet.getInt("effectid");
                int itemid = resultSet.getInt("itemid");
                UUID photoid = UUID.fromString(resultSet.getString("photoid"));
                int price = resultSet.getInt("price");
                Date capturedate = resultSet.getDate("capturedate");
                String pathtophoto = resultSet.getString("pathtophoto");
                UUID photographerid = UUID.fromString(resultSet.getString("photographerid"));
                UUID childid = UUID.fromString(resultSet.getString("childid"));
                int schoolid = resultSet.getInt("schoolid");

                //TODO get this with a database query
                SchoolModel schoolModel = new SchoolModel();
                EffectModel effectModel = new EffectModel();
                ItemModel itemModel = new ItemModel();

                File photoFile = new File(pathtophoto);
                PhotoModel photo = new PhotoModel(photoid, getAccount(photographerid), getAccount(childid), schoolModel, price, capturedate, pathtophoto);
                photoConfigurationModels1.add(new PhotoConfigurationModel(id, effectModel, itemModel, photo));

            } while (resultSet.next());

            return photoConfigurationModels1;
        });
        return photoConfigurationModels.get(0);
    }


    public void insertRating(UUID accountId, UUID photoId, int points) {
        JdbcTemplate insert = new JdbcTemplate(dataSource);
        //Make sure points is in range.
        if (points < 1 || points > 5) {
            return;
        }

        insert.update("INSERT INTO rating (accountid, photoid, points) VALUES (?, ?, ?)", accountId, photoId, points);
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
