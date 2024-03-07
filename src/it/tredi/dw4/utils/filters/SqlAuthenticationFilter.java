package it.tredi.dw4.utils.filters;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class SqlAuthenticationFilter implements Filter {

	// parametri di connessione al database per autenticazione utente
	private String driverClassName = ""; // driver da utilizzare per la connessione al db
	private String connectionUrl = ""; // url di connessione al database
	private String dbUser = ""; // username da utilizzare per l'accesso al database
	private String dbPassword = ""; // password da utilizzare per l'accesso al database
	private String loginQuery = ""; // query di login chiamata da maschera di autenticazione di DocWay
	private String autoLoginQuery = ""; // query di login chiamata da passaggio di parametri tramite request HTTP (parametri criptati) (se vuota viene impostata quella di loginQuery)
	private String encryptionAlghoritm = ""; // eventuale algoritmo di codifica della password dell'utente su database
	private String encryptionKey = ""; // eventuale chiave da utilizzare nell'algoritmo di codifica
	private boolean passwordEncrypted = false;
	
	// costanti utili alla definizione della query di login (sostituite poi con i relativi valori a runtime)
	private final String USERNAME = "%USERNAME%";
	private final String SESSIONID = "%SESSIONID%";
	
	// algoritmi crittografici gestiti
	private final String MD5 = "MD5";
	
	private Cipher encoder = null;
	private Cipher decoder = null;
	
	/**
	 * caricamento dei parametri di inizializzazione del filtro (caricati da web.xml)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		driverClassName = config.getInitParameter("driverClassName");
		connectionUrl = config.getInitParameter("connectionUrl");
		dbUser = config.getInitParameter("dbUser");
		dbPassword = config.getInitParameter("dbPassword");
		loginQuery = config.getInitParameter("loginQuery");
		autoLoginQuery = config.getInitParameter("autoLoginQuery");
		if (autoLoginQuery == null || autoLoginQuery.equals(""))
			autoLoginQuery = loginQuery;
		encryptionAlghoritm = config.getInitParameter("encryptionAlghoritm");
		if (encryptionAlghoritm != null && encryptionAlghoritm.length() > 0)
			passwordEncrypted = true;
		encryptionKey = config.getInitParameter("encryptionKey");
		
		if (encryptionAlghoritm != null && !encryptionAlghoritm.equals(MD5)) { // DES, TripleDES, DESEDE
			ObjectInputStream objin = null;
			try {
				encoder = Cipher.getInstance(encryptionAlghoritm);
				decoder = Cipher.getInstance(encryptionAlghoritm);
				
				Base64 base64dec = new Base64();
				byte[] encodedKey = base64dec.decode(encryptionKey);
				//BASE64Decoder base64dec = new BASE64Decoder();
				//byte[] encodedKey = base64dec.decodeBuffer(encryptionKey);
				
				objin = new ObjectInputStream(new ByteArrayInputStream(encodedKey));
				Key key = (Key) objin.readObject();
				
				encoder.init(Cipher.ENCRYPT_MODE, key);
				decoder.init(Cipher.DECRYPT_MODE, key);
			}
			catch (Throwable t) {
				Logger.error("SqlAuthenticationServlet.init(): " + t.getMessage(), t);
			}
			finally {
				try {
					if (objin != null)
						objin.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		// mbernardini 18/10/2016 : corretto bug su encoding di caratteri speciali (es. accenti) in caso di filtro di autenticazione SQL
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(true);
		if (request.getParameter("forceLogin") != null && request.getParameter("forceLogin").equals("true"))
			invalidateSession(session);
		
		if (session != null && session.getAttribute("userBean") != null) { // ricerca utente in sessione
		
			Logger.info("SqlAuthenticationServlet.doFilter(), user " + ((UserBean)session.getAttribute("userBean")).getLogin() + " already found in session");
		}
		else {
			// recupero dei parametri di username e password dalla request
			String username = request.getParameter("sqluser");
			String password = request.getParameter("sqlpwd");
			String query = loginQuery;
			String sessionId = ""; // gestito solo in login automatico
			
			// recupero dalla request del parametro di autenticazione (criptato) 
			// questo parametro deve contenere i valori di username e password
			if (request.getParameter("authCode") != null) {
				try {
					String authcode = (String) request.getParameter("authCode");
					if (!encryptionAlghoritm.equals(MD5) && decoder != null) // DES, TripleDES, DESEDE
						authcode = new String(decoder.doFinal(new Base64().decode(authcode.getBytes())));
					
					String[] authparams = authcode.split("\\|");
					if (authparams.length == 3) {
						username = authparams[0];
						password = authparams[1];
						sessionId = authparams[2];
						
						query = autoLoginQuery;
					}
				}
				catch (Throwable t) {
					Logger.error("SqlAuthenticationServlet.doFilter(): " + t.getMessage(), t);
				}
			}
			
			// encrypt della password specificata dall'utente
			if (passwordEncrypted) 
				password = ecryptPassword(password);
			
			if (username != null && username.length() > 0 && password != null && password.length() > 0) {
				if (authenticateUser(query, username, password, sessionId)) { // autenticazione utente tramite controllo di username e password
					UserBean userBean = new UserBean(username);
					session.setAttribute("userBean", userBean);
					
					Logger.info("SqlAuthenticationServlet.doFilter(), login for user " + username + " is completed! User added in session");
				}
				else {
					Logger.info("SqlAuthenticationServlet.doFilter(), forward to loginErrorSql.jsp");
					request.getRequestDispatcher("../loginErrorSql.jsp").forward(request, response);
					return;
				}
			}
			else {
				Logger.info("SqlAuthenticationServlet.doFilter(), forward to loginSql.jsp");
				request.getRequestDispatcher("../loginSql.jsp").forward(request, response);
				return;
			}
		}
		
		request.setAttribute("authentication_mode", "SQLAUTH");
		chain.doFilter(request, response);
	}
	
	/**
	 * Controllo username e password in base a connessione ad un database
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private boolean authenticateUser(String query, String username, String password, String sessionId) {
		if (driverClassName == null || driverClassName.length() == 0 || connectionUrl == null || connectionUrl.length() == 0) {
			Logger.error("SqlAuthenticationServlet.authenticateUser(): empty paras: " + driverClassName + " :: " + connectionUrl);
			return false;
		}
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName(driverClassName); // registrazione del driver JDBC
			conn = DriverManager.getConnection(connectionUrl, dbUser, dbPassword); // connessione al database
			
			// esecuzione della query di verifica password
			stmt = conn.createStatement();
			
			String pwd = "";
			
			query = query.replaceAll(USERNAME, username);
			query = query.replaceAll(SESSIONID, sessionId);
			
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
	            pwd = rs.getString(1);
			
			if (password != null && password.length() > 0 
					&& pwd != null && pwd.length() > 0 
					&& password.equals(pwd))
				return true;
			else
				return false;
			
		}
		catch (Throwable t) {
			Logger.error("SqlAuthenticationServlet.authenticateUser(): " + t.getMessage(), t);
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			catch (Exception e) {}
		}
		
		return false;
	}
	
	/**
	 * Encrypt della password specificata dall'utente tramite il form di login
	 * @param password
	 * @return
	 */
	private String ecryptPassword(String password) {
		try {
			if (password != null && password.length() > 0) {
				if (encryptionAlghoritm.equals(MD5)) {
					password = DigestUtils.md5Hex(password);
				}
				else if (encoder != null) { // DES, TripleDES, DESEDE
					Base64 base64enc = new Base64();
					password = base64enc.encodeAsString(encoder.doFinal(password.getBytes()));
					//BASE64Encoder base64enc = new BASE64Encoder();
					//password = base64enc.encode(encoder.doFinal(password.getBytes()));
				}
			}
		}
		catch (Throwable t) {
			Logger.error("SqlAuthenticationServlet.ecryptPassword(): " + t.getMessage(), t);
		}
		
		return password;
	}
	
	@Override
	public void destroy() {
	}
	
	/**
	 * metodo di test
	 * utilizzato per generare la chiave criptata da utilizzare per l'autenticazione automatica (parametro authCode)
	 */
	public static void main(String[] args) {
		String username = "fsimoni";
		String password = "fsimoni123";
		String sessionId = "2622";
		
		ObjectInputStream objin = null;
		try {
			Cipher encoder = Cipher.getInstance("DESEDE");
			
			Base64 base64dec = new Base64();
			byte[] encodedKey = base64dec.decode("rO0ABXNyACFjb20uc3VuLmNyeXB0by5wcm92aWRlci5ERVNlZGVLZXkiMda6D0P12gIAAVsAA2tleXQAAltCeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAGLX+H6HanbVGRlgNblehEAjsiZG8pJENlA==");
			//BASE64Decoder base64dec = new BASE64Decoder();
			//byte[] encodedKey = base64dec.decodeBuffer("rO0ABXNyACFjb20uc3VuLmNyeXB0by5wcm92aWRlci5ERVNlZGVLZXkiMda6D0P12gIAAVsAA2tleXQAAltCeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAGLX+H6HanbVGRlgNblehEAjsiZG8pJENlA==");
			
			objin = new ObjectInputStream(new ByteArrayInputStream(encodedKey));
			Key key = (Key) objin.readObject();
			
			encoder.init(Cipher.ENCRYPT_MODE, key);
			
			String paramAuthCode = username + "|" + password + "|" + sessionId;
			
			
			Base64 base64enc = new Base64();
			paramAuthCode = base64enc.encodeAsString(encoder.doFinal(paramAuthCode.getBytes()));
			//BASE64Encoder base64enc = new BASE64Encoder();
			//paramAuthCode = base64enc.encode(encoder.doFinal(paramAuthCode.getBytes()));
			
			System.out.println("authCode: " + paramAuthCode);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (objin != null)
					objin.close();
			}
			catch (Exception e) { }
		}
	}
	
	/**
	 * invalida la sessione corrente (cancellazione di tutti gli attributi in sessione)
	 * @param session
	 */
	private void invalidateSession(HttpSession session) {
		Logger.info("SqlAuthenticationServlet.invalidateSession(), remove all attributes in session...");
		
		if (session != null) {
			Enumeration<?> attributes = session.getAttributeNames();
			while (attributes.hasMoreElements()) {
				String name = (String) attributes.nextElement();
				if (name != null && name.length() > 0) {
					session.removeAttribute(name);
				}
			}
		}
	}

}
