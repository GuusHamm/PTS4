package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import nl.pts4.security.HashConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
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

	public static DatabaseController getTestInstance(){
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
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/test");
		dataSource.setUsername("postgres");

		this.dataSource = dataSource;
	}

	public AccountModel createAccount(String name, String email,String password){
		UUID accountUUID = UUID.randomUUID();

		JdbcTemplate insert = new JdbcTemplate(dataSource);
		insert.update("INSERT INTO account (id, name, email, hash) VALUES (?,?,?,?)",new Object[] {accountUUID,name,email, SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p)});

		return getAccount(accountUUID);
	}

    public AccountModel getAccount(final UUID uuid) {
        JdbcTemplate select = new JdbcTemplate(dataSource);

        AccountModel am = select.
                queryForObject("SELECT oauthkey, oauthprovider, name, email, hash, active, type FROM account WHERE id = ?",
                        new Object[]{uuid}
                        , (resultSet, i) -> {
                            String oauthkey = resultSet.getString("oauthkey");

                            String oauthprovider = resultSet.getString("oauthprovider");
                            AccountModel.OAuthProviderEnum oAuthProvider = null;
                            if (oauthprovider != null)
                                oAuthProvider = AccountModel.OAuthProviderEnum.valueOf(oauthprovider);

                            String name = resultSet.getString("name");
                            String email = resultSet.getString("email");
                            String hash = resultSet.getString("hash");
                            boolean active = resultSet.getBoolean("active");
                            String type = resultSet.getString("type");
                            AccountModel.AccountTypeEnum accountTypeEnum = null;
                            if (type != null)
                                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

                            return new AccountModel(uuid, oauthkey, oAuthProvider, name, email, hash, active, accountTypeEnum);
                        });
        return am;
    }

    public AccountModel getAccount(final String email) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        AccountModel am = select.
                queryForObject("SELECT id, oauthkey, oauthprovider, name, hash, active, type FROM account WHERE email = ?",
                        new Object[]{email},
                        (resultSet, i) -> {
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
                            AccountModel.AccountTypeEnum accountTypeEnum = null;
                            if (type != null)
                                accountTypeEnum = AccountModel.AccountTypeEnum.valueOf(type);

                            return new AccountModel(uuid, oauthkey, oAuthProvider, name, email, hash, active, accountTypeEnum);

                        });
        return am;
    }

	public ArrayList<OrderModel> getAllOrders() {
		JdbcTemplate select = new JdbcTemplate(dataSource);

		ArrayList<OrderModel> orderModels = select.queryForObject("select id, accountid,orderdate FROM order_;", (resultSet, i) -> {
			ArrayList<OrderModel> orderModels1 = new ArrayList<>();

			while(resultSet.next()){
				int id = resultSet.getInt("id");
				UUID accountid  = UUID.fromString(resultSet.getString("accountid"));
				Date orderDate = resultSet.getDate("orderdate");

				OrderModel orderModel = new OrderModel(id,orderDate,getAccount(accountid));

				orderModels1.add(orderModel);
			}

			return orderModels1;
		});
		return orderModels;
	}
}
