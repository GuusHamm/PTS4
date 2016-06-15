package nl.pts4.controller;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guushamm on 15/06/16.
 */
public class CSRFController {
	private static CSRFController instance;
	private final String CSRFToken;
	private final int CSRFExpiry;
	private Map<String, Date> tokens;
	private SecureRandom random;

	private CSRFController() {
		tokens = new HashMap<>();
		random = new SecureRandom();
		CSRFToken = "CSRF";
		CSRFExpiry = 60 * 10; // 10 Minutes
	}

	public static CSRFController getInstance() {
		if (instance == null) {
			instance = new CSRFController();
		}
		return instance;
	}

	public String generateToken() {
		String token = new BigInteger(130, random).toString(32);
		tokens.put(token, new Date());

		return token;
	}

	public String getCSRFToken() {
		return CSRFToken;
	}

	public void voidToken(String csrfToken) {
		tokens.remove(csrfToken);
	}

	public boolean validToken(String csrfToken) {
		boolean validToken = tokens.containsKey(csrfToken);

		if (validToken) {
			Date date = tokens.get(csrfToken);
			validToken = isExpired(date);
		}

		return validToken;
	}

	/**
	 * Checks if date is later than date + CSRFExpiry
	 *
	 * @param date Date to check
	 * @return True if date is earlier than date + CSRFExpiry, false otherwise
	 */
	private boolean isExpired(Date date) {
		Date now = new Date();
		Date expiry = new Date(date.getTime());
		expiry.setTime(expiry.getTime() + CSRFExpiry);
		return now.before(expiry);
	}
}
