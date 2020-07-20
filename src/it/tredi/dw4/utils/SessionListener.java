package it.tredi.dw4.utils;

import it.tredi.dw4.acl.AclInit;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.docway.DocwayInit;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Listener utilizzato in scadenza della sessione utente per chiamare il logout
 * di DocWay
 * 
 * @author mbernardini
 */
public class SessionListener implements HttpSessionListener  {

	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		Logger.info("SessionListener.sessionDestroyed(), chiamata a listener per scadenza sessione");
		HttpSession session = event.getSession();
		if (session != null) {
			logout(session);
		}
	}

	/**
	 * Chiamata a logout di Docway
	 * @param request
	 * @param response
	 */
	private void logout(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		if (userBean != null) {
			try {
				DocwayInit docwayInit = (DocwayInit) session.getAttribute("docwayInit");
				if (docwayInit != null) {
					Logger.info("SessionListener.logout(), chiamata a logout di Docway per l'utente " + userBean.getLogin());
					docwayInit.logout(userBean);
				}
				
				AclInit aclInit = (AclInit) session.getAttribute("aclInit");
				if (aclInit != null) {
					Logger.info("SessionListener.logout(), chiamata a logout di ACL per l'utente " + userBean.getLogin());
					aclInit.logout(userBean);
				}
			}
			catch (Throwable t) {
				Logger.error(t.getMessage(), t);
			}
		}
	}
	
}
