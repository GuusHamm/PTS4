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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseController {
    private static DatabaseController instance;
    private DataSource dataSource;

    /**
     * Get the instance of the DatabaseControlle
     *
     * @return the singleton instance of DatabaseController
     */
    public static DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
            instance.setDefaultDataSource();
        }
        return instance;
    }

    /**
     * returns an instance for testing
     *
     * @return returns the instance with the default test data set.
     */
    public static DatabaseController getTestInstance() {
        instance = getInstance();
        instance.setupDefaultTestDataSource();

        return instance;
    }

    /**
     * Sets the default data source to connect to the database
     */
    private void setDefaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DatabaseCredentials.Driver);
        dataSource.setUrl(DatabaseCredentials.Url);
        dataSource.setUsername(DatabaseCredentials.Username);
        dataSource.setPassword(DatabaseCredentials.Authentication);

        this.dataSource = dataSource;
    }

    /**
     * Sets the test data source to connect to the test database
     */
    private void setupDefaultTestDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DatabaseCredentials.Driver);
        dataSource.setUrl(DatabaseCredentials.TestUrl);
        dataSource.setUsername(DatabaseCredentials.TestUsername);
        dataSource.setPassword(DatabaseCredentials.Authentication);

        this.dataSource = dataSource;
    }

    /**
     * Run the create_tables script
     *
     * @return true if there is no SQL exception, returns false when there is.
     */
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

    /**
     * Create an account with the parameters
     *
     * @param name     : The username the user chooses
     * @param email    : The email of the user
     * @param password : The password that the users enters
     * @return The account that was created
     */
    public AccountModel createAccount(String name, String email, String password) {
        UUID accountUUID = UUID.randomUUID();

        JdbcTemplate insert = new JdbcTemplate(dataSource);
        insert.update("INSERT INTO account (id, name, email, hash) VALUES (?,?,?,?)", accountUUID, name, email, SCryptUtil.scrypt(password, HashConstants.N, HashConstants.R, HashConstants.P));

        return getAccount(accountUUID);
    }

    public LinkModel getLinkModel(String key) {
        JdbcTemplate retrieve = new JdbcTemplate(dataSource);
        return retrieve.queryForObject("SELECT key, link, authorizeduser FROM link WHERE key = ?", new Object[]{key}, new RowMapper<LinkModel>() {
            @Override
            public LinkModel mapRow(ResultSet resultSet, int i) throws SQLException {
                return new LinkModel(resultSet.getString("key"), resultSet.getString("link"), getAccount((UUID) resultSet.getObject("authorizeduser")));
            }
        });
    }

    public LinkModel createLinkModel(LinkModel lm) {
        JdbcTemplate insert = new JdbcTemplate(dataSource);
        insert.update("INSERT INTO link (key, link, authorizedUser) VALUES (?, ?, ?)", lm.getKey(), lm.getLink(), lm.getAuthorizedUser().getUUID());
        return lm;
    }

    /**
     * Change the email from an account
     *
     * @param ac    : The account which email needs to be changed
     * @param email : The new email
     * @return if the update worked or not
     */
    public boolean setAccountEmail(AccountModel ac, String email) {
        ac.setEmail(email);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET email = ? WHERE id = ?", email, ac.getUUID()) == 1;
    }

    /**
     * Change the username from an account
     *
     * @param ac   : The account which username needs to be changed
     * @param name : The new username
     * @return if the update worked or not
     */
    public boolean setAccountName(AccountModel ac, String name) {
        ac.setName(name);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET name = ? WHERE id = ?", name, ac.getUUID()) == 1;
    }

    /**
     * Sets the password from an account
     *
     * @param ac   : The account which password needs to be changed
     * @param hash : The new hash for the database
     * @return if the update worked or not
     */
    public boolean setAccountHash(AccountModel ac, String hash) {
        ac.setHash(hash);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET hash = ? WHERE id = ?", hash, ac.getUUID()) == 1;
    }

    public HashMap<UUID, AccountModel> getAllAccounts() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        HashMap<UUID, AccountModel> accountModels = new HashMap<>();
        List<Map<String, Object>> rows = template.queryForList("SELECT id, name, email, active, type, theme FROM account");

        for (Map<String, Object> row : rows) {
            UUID uuid = (UUID) row.get("id");

            String name = (String) row.get("name");
            boolean active = (boolean) row.get("active");

            String type = (String) row.get("type");
            String email = (String) row.get("email");
            String theme = (String) row.get("theme");
            AccountModel.AccountTypeEnum accountTypeEnum = null;
            if (type != null)
                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

            accountModels.put(uuid, new AccountModel(uuid, null, null, name, email, null, active, accountTypeEnum, theme));

        }
        return accountModels;
    }

    /**
     * Get an account with a uuid
     *
     * @param uuid : The UUID of the account
     * @return the account from the database with the parameter
     */
    public AccountModel getAccount(final UUID uuid) {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            AccountModel am = select.
                    queryForObject("SELECT id, oauthkey, oauthprovider, name, email, hash, active, type, theme FROM account WHERE id = ? AND active = true",
                            new Object[]{uuid}
                            , (resultSet, i) -> {
                                return getAccountFromResultSet(resultSet);
                            });
            return am;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AccountModel getRandomAccount() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            AccountModel am = select.
                    queryForObject("SELECT id, oauthkey, oauthprovider, name, email, hash, active, type,theme FROM account WHERE type = 'customer' AND active = true LIMIT 1",
                            new Object[]{}
                            , (resultSet, i) -> {
                                return getAccountFromResultSet(resultSet);
                            });
            return am;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UUID getRandomAccountUUID() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            UUID uuid = select.
                    queryForObject("SELECT id FROM account WHERE type = 'customer' LIMIT 1",
                            new Object[]{}
                            , (resultSet, i) -> {
                                UUID id = UUID.fromString(resultSet.getString("id"));
                                return id;
                            });
            return uuid;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AccountModel getRandomPhotographer() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            AccountModel am = select.
                    queryForObject("SELECT id, oauthkey, oauthprovider, name, email, hash, active, type,theme FROM account WHERE type = 'photographer' LIMIT 1",
                            new Object[]{}
                            , (resultSet, i) -> {
                                return getAccountFromResultSet(resultSet);
                            });
            return am;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UUID getRandomPhotographerUUID() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            UUID uuid = select.
                    queryForObject("SELECT id FROM account WHERE type = 'photographer' LIMIT 1",
                            new Object[]{}
                            , (resultSet, i) -> {
                                UUID id = UUID.fromString(resultSet.getString("id"));
                                return id;
                            });
            return uuid;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UUID getRandomChildUUID() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        try {
            UUID uuid = select.
                    queryForObject("SELECT id FROM childaccount LIMIT 1",
                            new Object[]{}
                            , (resultSet, i) -> {
                                UUID id = UUID.fromString(resultSet.getString("id"));
                                return id;
                            });
            return uuid;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Return an account with an email
     *
     * @param email : The email of the account
     * @return the account from the database with the parameter
     */
    public AccountModel getAccount(final String email) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        AccountModel am;
        try {
            am = select.
                    queryForObject("SELECT id, email, oauthkey, oauthprovider, name, hash, active, type,theme FROM account WHERE email = ? AND active = true",
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


    /**
     * Get all the orders of a account from the database
     *
     * @return a list of order models from the database
     */
    public List<OrderModel> getAllAccountOrders(UUID uuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        HashMap<UUID, AccountModel> accountModels = getAllAccounts();
        List<OrderModel> orderModels = new ArrayList<>();

        List<Map<String, Object>> rows = template.queryForList("SELECT o.id, o.accountid, o.orderdate FROM order_ o WHERE accountid = ?", uuid);

        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            UUID account = (UUID) row.get("accountid");
            Date orderDate = (Date) row.get("orderdate");

            orderModels.add(new OrderModel(id, orderDate, accountModels.get(account)));
        }

        rows = template.queryForList("SELECT o.id, o.orderid, o.photoconfigurationid FROM orderline o");
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            int orderid = (int) row.get("orderid");
            int photoConfigurationId = (int) row.get("photoconfigurationid");
            OrderLineModel olm = new OrderLineModel(id, orderid, photoConfigurationId);

            Optional<OrderModel> o = orderModels.stream().filter(a -> a.getId() == orderid).findFirst();
            if (o.isPresent()) {
                o.get().getOrderLineModels().add(olm);
            }
        }

        return orderModels;
    }

    public List<OrderModel> getAllPhotographerOrders(UUID uuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = template.queryForList("SELECT o.* FROM order_ o JOIN orderline ol ON o.id = ol.orderid JOIN photoconfiguration pc ON ol.photoconfigurationid = pc.id JOIN photo p ON pc.photoid = p.id WHERE p.photographerid = ?;", uuid);
        HashMap<UUID, AccountModel> accountModels = getAllAccounts();
        List<OrderModel> orderModels = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            UUID account = (UUID) row.get("accountid");
            Date orderDate = (Date) row.get("orderdate");

            orderModels.add(new OrderModel(id, orderDate, accountModels.get(account)));
        }

        rows = template.queryForList("SELECT o.* FROM orderline o JOIN photoconfiguration pc ON o.photoconfigurationid = pc.id JOIN photo p ON pc.photoid = p.id WHERE p.photographerid = ?", uuid);
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            int orderid = (int) row.get("orderid");
            int photoConfigurationId = (int) row.get("photoconfigurationid");
            OrderLineModel olm = new OrderLineModel(id, orderid, photoConfigurationId);

            Optional<OrderModel> o = orderModels.stream().filter(a -> a.getId() == orderid).findFirst();
            if (o.isPresent()) {
                o.get().getOrderLineModels().add(olm);
            }
        }

        return orderModels;
    }

    /**
     * Get all the orders from the database
     *
     * @return a list of order models from the database
     */
    public List<OrderModel> getAllOrders() {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = template.queryForList("SELECT o.id, o.accountid, o.orderdate FROM order_ o");
        HashMap<UUID, AccountModel> accountModels = getAllAccounts();
        List<OrderModel> orderModels = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            UUID account = (UUID) row.get("accountid");
            Date orderDate = (Date) row.get("orderdate");

            orderModels.add(new OrderModel(id, orderDate, accountModels.get(account)));
        }

        rows = template.queryForList("SELECT o.id, o.orderid, o.photoconfigurationid FROM orderline o");
        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            int orderid = (int) row.get("orderid");
            int photoConfigurationId = (int) row.get("photoconfigurationid");
            OrderLineModel olm = new OrderLineModel(id, orderid, photoConfigurationId);

            Optional<OrderModel> o = orderModels.stream().filter(a -> a.getId() == orderid).findFirst();
            if (o.isPresent()) {
                o.get().getOrderLineModels().add(olm);
            }
        }

        return orderModels;
    }

    /**
     * Get all the order lines with the order ID
     *
     * @param orderID : The order that you want to have the order lines of.
     * @return a list of order lines
     */
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

    public List<Integer> createPhotoConfigurations(UUID[] uuids, int[] effects, int[] items) {
        List<PhotoConfigurationModel> photoConfigurationModels = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        for (int i = 0; i < uuids.length; i++) {
            ids.add(jdbcTemplate.queryForObject("INSERT INTO photoconfiguration(effectid,itemid,photoid) VALUES (?,?,?) RETURNING id", new Object[]{effects[i], items[i], uuids[i]}, Integer.TYPE));
        }

        return ids;
    }

    public int createOrderModel(UUID[] uuids, int[] effects, int[] items, UUID accountUUID) {
        OrderModel orderModel = null;

        List<Integer> configurationIDS = new ArrayList<>();
        configurationIDS = createPhotoConfigurations(uuids, effects, items);
        if (configurationIDS.isEmpty()) {
            return 0;
        }

        JdbcTemplate insertModel = new JdbcTemplate(dataSource);

        int orderID = insertModel.queryForObject("INSERT INTO order_(accountid,orderdate) VALUES (?,?) RETURNING id", new Object[]{accountUUID, new Date()}, Integer.TYPE);

        boolean success = true;

        for (int i = 0; i < configurationIDS.size(); i++) {
            if (!(insertModel.update("INSERT INTO orderline (orderid,photoconfigurationid,amount) VALUES (?,?,1)", orderID, configurationIDS.get(i)) == 1)) {
                success = false;
            }
        }

        if (!success) {
            return 0;
        } else {
            return orderID;
        }
    }

    /**
     * Get all the photos
     *
     * @return a list of all the photoModels
     */
    public List<PhotoModel> getPhotos() {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        return getPhotosFromMap(template.queryForList("SELECT p.*, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid GROUP BY p.id ORDER BY capturedate"));
    }

    /**
     * Get all the photos
     *
     * @return a list of all the photoModels
     */
    public List<PhotoModel> getPhotosByUUID(UUID[] uuids) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        return getPhotosFromMap(template.queryForList("SELECT p.*, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid WHERE p.id in (?) GROUP BY p.id ORDER BY capturedate", new Object[]{uuids}));
    }

    public List<PhotoModel> getPhotosofChildAccount(UUID childAccountID) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return getPhotosFromMap(template.queryForList("SELECT p.*, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid WHERE p.childid = ? GROUP BY p.id ORDER BY capturedate", childAccountID));
    }

    public List<PhotoModel> getPhotosOfAccount(UUID accountUuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return getPhotosFromMap(template.queryForList("SELECT p.*, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid JOIN childaccount_account ca ON p.childid = ca.childaccount_id JOIN account a ON ca.account_id = a.id WHERE a.id = ? GROUP BY p.id ORDER BY capturedate", accountUuid));
    }

    public List<PhotoModel> getPhotosOfPhotographer(UUID accountUuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        return getPhotosFromMap(template.queryForList("SELECT p.id, p.photographerid, p.childid, p.schoolid, p.price,p.capturedate,p.pathtophoto,p.pathtolowresphoto, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid WHERE p.photographerid = ? GROUP BY p.id ORDER BY capturedate", accountUuid));
    }

    private List<PhotoModel> getPhotosFromMap(List<Map<String, Object>> rows) {
        List<PhotoModel> photoModels = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            UUID uuid = (UUID) row.get("id");
            UUID photographerid = (UUID) row.get("photographerid");
            UUID childid = (UUID) row.get("childid");
            //// TODO: 4-4-16 fix this
//			int schoolid = (int) row.get("schoolid");
            int price = Integer.parseInt(String.valueOf(row.get("price")));
            Date captureDate = (Date) row.get("capturedate");
            String path = String.valueOf(row.get("pathtophoto"));
            String pathLowRes = String.valueOf(row.get("pathtolowresphoto"));
            Integer points = 0;
            if (row.get("points") != null) {
                try {
                    Long tempoints = (Long) row.get("points");
                    points = tempoints.intValue();
                } catch (Exception e) {
                    points = (Integer) row.get("points");
                }
            }
            photoModels.add(new PhotoModel(uuid, getAccount(photographerid), getAccount(childid), null, price, captureDate, path, pathLowRes, points));
        }
        return photoModels;
    }

    public PhotoModel getPhotoByUUID(UUID uuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        PhotoModel photoModel = template.queryForObject("SELECT p.*, sum(r.points) AS points FROM photo p LEFT JOIN rating r ON p.id = r.photoid WHERE p.id=? GROUP BY p.id", new Object[]{uuid}, ((resultSet, i) -> {
            try {
                UUID id = UUID.fromString(resultSet.getString("id"));
                UUID photographerid = UUID.fromString(resultSet.getString("photographerid"));
                UUID childid = UUID.fromString(resultSet.getString("childid"));
                int schoolid = resultSet.getInt("schoolid");
                double price = resultSet.getInt("price") / 100;
                Date captureDate = resultSet.getDate("capturedate");
                String path = resultSet.getString("pathtophoto");
                String pathLowRes = resultSet.getString("pathtolowresphoto");
                //TODO create a get school

                Long points = 0l;
                if (resultSet.getObject("points") != null) {
                    points = resultSet.getLong("points");
                }
                return new PhotoModel(id, DatabaseController.getInstance().getAccount(photographerid), DatabaseController.getInstance().getAccount(childid), null, price, captureDate, path, pathLowRes, points.intValue());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }));
        return photoModel;
    }

    /**
     * Get all the effects from the database
     *
     * @return a list with all the effects in effectmodels
     */
    public List<EffectModel> getEffects() {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = template.queryForList("SELECT id, type, description, price  FROM effect");
        List<EffectModel> effectModels = new ArrayList<>(rows.size());

        for (Map<String, Object> row : rows) {
            int id = (int) row.get("id");
            String type = (String) row.get("type");
            String description = (String) row.get("description");
            double price = (int) row.get("price") / 100;

            effectModels.add(new EffectModel(id, type, description, price));
        }

        return effectModels;
    }

    public List<ChildAccountModel> getUnclaimedChildren(UUID photographerID) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<ChildAccountModel> childAccountModels = new ArrayList<>();

        for (Map<String, Object> row : template.queryForList("SELECT DISTINCT c.* FROM childaccount c JOIN photo p ON c.id = p.childid WHERE c.id NOT IN (SELECT c1.id FROM childaccount c1 JOIN childaccount_account ca ON c1.id = ca.childaccount_id) AND p.photographerid = ?;", new Object[]{photographerID})) {
            childAccountModels.add(new ChildAccountModel((UUID) row.get("id"), (String) row.get("uniquecode")));
        }

        return childAccountModels;
    }

    /**
     * Get a photo config item by id
     *
     * @param photoConfigid : The id of the item that you want
     * @return the corresponding PhotoConfigurationModel with the ID
     */
    public PhotoConfigurationModel getPhotoConfigurationModelById(int photoConfigid) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        ArrayList<PhotoConfigurationModel> photoConfigurationModels = null;
        try {
            photoConfigurationModels = select.queryForObject("SELECT pc.id, pc.effectid,pc.itemid, p.id as photoid, p.price, p.capturedate, p.pathtophoto, p.pathtolowresphoto , p.photographerid, p.childid, p.schoolid, s.name, s.location, s.country, e.type, e.description, e.price as effectprice, i.price as itemprice, i.description as itemdescription, i.thumbnailpath " + "FROM photoconfiguration pc, photo p, school s, effect e, item i " +
                    "WHERE p.id = pc.photoid " +
                    "AND s.id = p.schoolid " +
                    "AND pc.effectid = e.id " +
                    "AND pc.itemid = i.id " +
                    " AND pc.id = " + photoConfigid + ";", (resultSet, i) -> {
                ArrayList<PhotoConfigurationModel> photoConfigurationModels1 = new ArrayList<>(i);

                //This code is to make sure we don't crash when not getting any rows.
                if (resultSet == null) {
                    return null;
                }

                do {
                    int id = resultSet.getInt("id");
                    UUID photoid = UUID.fromString(resultSet.getString("photoid"));
                    double price = resultSet.getInt("price") / 100;
                    price = roundDouble(price, 2);
                    Date capturedate = resultSet.getDate("capturedate");
                    String pathtophoto = resultSet.getString("pathtophoto");
                    String pathLowRes = resultSet.getString("pathtolowresphoto");
                    UUID photographerid = UUID.fromString(resultSet.getString("photographerid"));
                    UUID childid = UUID.fromString(resultSet.getString("childid"));

                    //School data
                    int schoolid = resultSet.getInt("schoolid");
                    String name = resultSet.getString("name");
                    String location = resultSet.getString("location");
                    String country = resultSet.getString("country");

                    //Effect data
                    int effectid = resultSet.getInt("effectid");
                    String type = resultSet.getString("type");
                    String description = resultSet.getString("description");
                    double effectprice = resultSet.getInt("effectprice") / 100;
                    effectprice = roundDouble(effectprice, 2);

                    //Item data
                    int itemid = resultSet.getInt("itemid");
                    double itemprice = resultSet.getInt("itemprice") / 100;
                    itemprice = roundDouble(itemprice, 2);
                    String itemdescription = resultSet.getString("itemdescription");
                    String itemType = resultSet.getString("itemdescription");
                    String thumbnailpath = resultSet.getString("thumbnailpath");

                    //TODO get this with a database query
                    SchoolModel schoolModel = new SchoolModel(schoolid, name, location, country);//////////////////////////todo THIS WILL NEVER WORK RIGHT 0.o
                    EffectModel effectModel = new EffectModel(effectid, type, description, effectprice);
                    ItemModel itemModel = new ItemModel(itemid, itemprice, itemType, itemdescription, thumbnailpath);

                    File photoFile = new File(pathtophoto);
                    //TODO incase shit get's fucked actually get the points of the photomodel
                    PhotoModel photo = new PhotoModel(photoid, getAccount(photographerid), getAccount(childid), schoolModel, price, capturedate, pathtophoto, pathLowRes, 0);
                    photoConfigurationModels1.add(new PhotoConfigurationModel(id, effectModel, itemModel, photo));

                }
                while (resultSet.next());

                return photoConfigurationModels1;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return photoConfigurationModels.get(0);
    }

    private double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public boolean createPhoto(UUID uuid, String path, UUID photographer, UUID child, String pathToLowResPhoto) {
        int price = 500;
        Date captureDate = new Date();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            return jdbcTemplate.update("INSERT INTO public.photo (id, price, capturedate, pathtophoto, photographerid, childid, pathtolowresphoto) VALUES (?, ?, ?, ?, ?, ?, ?);", uuid, price, captureDate, path, photographer, child, pathToLowResPhoto) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a rating of a photo / photograph into the database
     *
     * @param accountId : The one that is rating the photo
     * @param photoId   : The photo that is being rated
     * @param points    : Between 1 to 5, 5 being highest and 1 lowest
     */
    public boolean insertRating(UUID accountId, UUID photoId, int points) {
        JdbcTemplate insert = new JdbcTemplate(dataSource);
        //Make sure points is in range.
        if (points != 1 && points != -1) {
            return false;
        }

        photoHasBeenRatedByUser(accountId, photoId);

        insert.update("INSERT INTO rating (accountid, photoid, points) VALUES (?, ?, ?)", accountId, photoId, points);

        return true;
    }

    public void photoHasBeenRatedByUser(UUID accountid, UUID photoid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        int count = template.queryForObject("SELECT COUNT(*) from rating where photoid = ? and accountid = ?;", new Object[]{photoid, accountid}, Integer.TYPE);
        if (count > 0) {
            JdbcTemplate remove = new JdbcTemplate(dataSource);
            remove.update("DELETE FROM rating WHERE accountid = ? AND photoid = ?", accountid, photoid);

        }
    }


    /**
     * Create a cookie with an account and a cookie UUID
     *
     * @param user       : The account of the current user
     * @param cookieuuid : The ID which you use to identify the cookie
     */
    public void createUserCookie(AccountModel user, UUID cookieuuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        template.update("INSERT INTO user_cookie (id, account) VALUES (?, ?)", cookieuuid, user.getUUID());
    }

    /**
     * Get an account with a cookie string
     *
     * @param cookie : The cookie where you want the account from
     * @return The account that was in the cookie, if empty return null
     */
    public AccountModel getAccountByCookie(final String cookie) {
        if (cookie == null) return null;
        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            AccountModel am = template.queryForObject("SELECT a.id, a.oauthkey, a.oauthprovider, a.name, a.email, a.hash, a.active, a.type,theme FROM account a, user_cookie uc WHERE a.id = uc.account AND uc.id = ? AND a.active = true", new Object[]{UUID.fromString(cookie)}, new RowMapper<AccountModel>() {
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

    /**
     * Delete an account from the database
     *
     * @param uuid : The UUID of the account that needs to be deleted
     * @return true if the update succeeded an false if you get an Empty result data access Exception
     * @throws IllegalArgumentException when UUId is null
     */
    public boolean deleteAccount(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) throw new IllegalArgumentException("Invalid UUID");

        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            template.update("UPDATE account a SET active = false WHERE a.id = ?", uuid);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean deletePhoto(UUID uuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        return template.update("DELETE FROM photo WHERE id=?", uuid) == 1;
    }

    /**
     * Checks if the user is privileged i.e. the user is either an administrator or a photographer if so it returns true
     *
     * @param uuid
     * @return true if the user is privileged
     */
    public boolean checkPriviledgedUser(UUID uuid) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        return template.queryForObject("SELECT count(id) FROM account WHERE id = ? AND type IN ('photographer','administrator')", new Object[]{uuid}, Integer.TYPE) > 0;
    }

    /**
     * Get an account with a cookie
     *
     * @param cookie : The cookie which contains the account
     * @return The account from the cookie
     */
    public AccountModel getAccountByCookie(Cookie cookie) {
        return getAccountByCookie(cookie.getValue());
    }

    /**
     * Get an account from a resultset
     *
     * @param resultSet the resultset from getAccount
     * @return the account from the resultSet
     */
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
            String theme = resultSet.getString("theme");
            AccountModel.AccountTypeEnum accountTypeEnum = null;
            if (type != null)
                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

            return new AccountModel(uuid, oauthkey, oAuthProvider, name, email, hash, active, accountTypeEnum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inserts a new item into the database
     *
     * @param price
     * @param type
     * @param description
     * @param thumbnailPath
     * @return
     */
    public boolean insertItem(double price, String type, String description, String thumbnailPath) {

        JdbcTemplate insert = new JdbcTemplate(dataSource);
        try {
            insert.update("INSERT INTO item ( type, price, description, thumbnailpath) VALUES (?,?,?,?)", type, price, description, thumbnailPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * Gets a ItemModel by id from the database
     *
     * @param id
     * @return
     */
    public ItemModel getItemByID(int id) {

        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            ItemModel im = template.queryForObject("SELECT * FROM item i WHERE i.id = ?", new Object[]{id}, new RowMapper<ItemModel>() {
                @Override
                public ItemModel mapRow(ResultSet resultSet, int i) throws SQLException {
                    return getItemFromResultSet(resultSet);
                }
            });
            return im;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Gets all ItemModel from the database
     *
     * @return
     */
    public List<ItemModel> getItems() {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = select.queryForList(
                "SELECT  * FROM item");

        List<ItemModel> itemModels = new ArrayList<>(rows.size());

        for (Map<String, Object> row : rows) {
            int id = (Integer) row.get("id");
            double price = (Integer) row.get("price") / 100;
            String type = (String) row.get("type");
            String description = (String) row.get("description");
            String thumbnailPath = (String) row.get("thumbnailPath");

            itemModels.add(new ItemModel(id, price, type, description, thumbnailPath));
        }
        return itemModels;

    }

    /**
     * Gets a Itemmodel from a resultset
     *
     * @param resultSet
     * @return
     */
    private ItemModel getItemFromResultSet(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            double price = resultSet.getInt("price") / 100;
            String type = resultSet.getString("type");
            String description = resultSet.getString("description");
            String thumbnailPath = resultSet.getString("thumbnailpath");

            return new ItemModel(id, price, type, description, thumbnailPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * updates the item in the database with all new values
     *
     * @param id
     * @param price
     * @param type
     * @param description
     * @param thumbnailPath
     * @return
     */
    public boolean updateItem(int id, double price, String type, String description, String thumbnailPath) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE item SET (price, type, description, thumbnailpath) = (?,?,?,?)  " +
                "WHERE id= ? ", price, type, description, thumbnailPath, id) == 1;
    }

    /**
     * updates the item in the database with all new values without changing the image
     *
     * @param id
     * @param price
     * @param type
     * @param description
     * @return
     */
    public boolean updateItem(int id, double price, String type, String description) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE item SET (price, type, description) = (?,?,?)  " +
                "WHERE id= ? ", price, type, description, id) == 1;
    }

    /**
     * Gets all the schools the photographer photographs for by his PhotographerID
     *
     * @param photographerID
     * @return
     */
    public List<SchoolModel> getSchools(UUID photographerID) {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = select.queryForList(
                "SELECT  DISTINCT  s.id, s.name, s.location,s.country FROM school s, photo p, account a " +
                        "WHERE a.id = ? and p.photographerid = ? and p.schoolid = s.id",

                photographerID, photographerID);
        List<SchoolModel> schoolModels = new ArrayList<>(rows.size());

        for (Map<String, Object> row : rows) {
            int id = (Integer) row.get("id");
            String name = (String) row.get("name");
            String location = (String) row.get("location");
            String country = (String) row.get("country");

            schoolModels.add(new SchoolModel(id, name, location, country));
        }
        return schoolModels;
    }

    public boolean deleteItem(int id) throws IllegalArgumentException {


        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            template.update("DELETE FROM item i WHERE i.id = ?", id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean setAccountTheme(AccountModel ac, String theme) {
        ac.setTheme(theme);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("UPDATE account SET theme = ? WHERE id = ?", theme, ac.getUUID()) == 1;

    }

    public void updatePhotoPrice(UUID photoId, double price) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        int newPrice = (int) (price * 100);

        template.update("UPDATE photo SET price=? WHERE id=?", newPrice, photoId);
    }

    public ChildAccountModel createChild(UUID uuid, String uniqueCode) {
        ChildAccountModel childAccountModel = new ChildAccountModel(uuid, uniqueCode);

        JdbcTemplate select = new JdbcTemplate(dataSource);

        int i = select.queryForObject("Select count(*) from childaccount WHERE uniquecode = ?", new Object[]{childAccountModel.getUniqueCode()}, Integer.TYPE);

        if (i > 0) {
            return null;
        } else {
            JdbcTemplate insert = new JdbcTemplate(dataSource);

            insert.update("INSERT INTO childaccount(id,uniquecode) VALUES (?,?)", childAccountModel.getUuid(), childAccountModel.getUniqueCode());

            return childAccountModel;
        }

    }

    public boolean addChildToUser(AccountModel currentUser, String childCode) {
        ChildAccountModel child = getChildByCode(childCode);

        if (child == null) return false;

        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.update("INSERT INTO childaccount_account(account_id, childaccount_id) VALUES (?,?);", currentUser.getUUID(), child.getUuid()) == 1;
    }

    public ChildAccountModel getChildByCode(String childCode) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        try {
            ChildAccountModel cm = template.queryForObject("SELECT * FROM childaccount c WHERE c.uniquecode= ?", new Object[]{childCode}, new RowMapper<ChildAccountModel>() {
                @Override
                public ChildAccountModel mapRow(ResultSet resultSet, int i) throws SQLException {
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("id"));

                        return new ChildAccountModel(uuid, childCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
            return cm;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AccountModel> getParentFromChild(ChildAccountModel childAccountModel) {

        JdbcTemplate select = new JdbcTemplate(dataSource);

        List<Map<String, Object>> rows = select.queryForList(
                "SELECT a.* from account a, childaccount_account cha where a.id = cha.account_id and cha = ?", childAccountModel.getUuid()
        );

        List<AccountModel> parents = new ArrayList<>(rows.size());

        for (Map<String, Object> row : rows) {

            UUID uuid = UUID.fromString(row.get("id").toString());
            String oauthkey = (String) row.get("oauthkey");
            String oauthprovider = (String) row.get("oauthprovider");

            AccountModel.OAuthProviderEnum oAuthProvider = null;
            if (oauthprovider != null)
                oAuthProvider = AccountModel.OAuthProviderEnum.valueOf(oauthprovider);

            String name = (String) row.get("name");
            String hash = (String) row.get("hash");
            boolean active = (boolean) row.get("active");

            String type = (String) row.get("type");
            String email = (String) row.get("email");
            String theme = (String) row.get("theme");
            AccountModel.AccountTypeEnum accountTypeEnum = null;
            if (type != null)
                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

            parents.add(new AccountModel(uuid, oauthkey, oAuthProvider, name, email, hash, active, accountTypeEnum));

        }
        return parents;
    }
}