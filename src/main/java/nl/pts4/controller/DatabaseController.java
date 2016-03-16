package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.security.HashConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class DatabaseController {
	private DataSource dataSource;

	private static DatabaseController instance;

	public static DatabaseController getInstance(){
		if (instance == null){
			instance = new DatabaseController();
			instance.setDefaultDataSource();
		}
		return instance;
	}

	public void setDefaultDataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://guushamm.me:5432/pts4");
		dataSource.setUsername("pts4");
		dataSource.setPassword("JZlRvopu7Ue0lgeh8O1d");

		this.dataSource = dataSource;
	}

	public boolean createAccount(String name, String email,String password){
		JdbcTemplate insert = new JdbcTemplate(dataSource);

		insert.update("INSERT INTO account (id, name, email, hash) VALUES (?,?,?,?)",new Object[] {UUID.randomUUID(),name,email, SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p)});

		return true;
	}
}
