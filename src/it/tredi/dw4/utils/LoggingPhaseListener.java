package it.tredi.dw4.utils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class LoggingPhaseListener implements PhaseListener {
	private static final String REQUEST_ID_NAME = "LoggingPhaseListener_request_id";
	private static final String START_TS_NAME = "LoggingPhaseListener_start_ts";

	private static final long serialVersionUID = 1L;
	
	private static long requestIdCounter = 0;
	
	@Override
	public void beforePhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
			FacesContext ctx = event.getFacesContext();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			
			//crea un id per la richiesta in modo da poterla tracciare nei log
			String requestId = Long.toString(getRerquestCounter());
			request.setAttribute(LoggingPhaseListener.REQUEST_ID_NAME, requestId);
			request.setAttribute(LoggingPhaseListener.START_TS_NAME, Long.toString(System.currentTimeMillis()));
			
			Logger.info(request.getRequestURI() + " entering request #" + requestId);
			
			String cookiesList= "{ ";
			Cookie[] cookies = request.getCookies();
			if (null != cookies){
				for (int i = 0; i < cookies.length; i++) {
					cookiesList += cookies[i].getName() + "=" + cookies[i].getValue() + " ";
				}
				cookiesList += " }";
				Logger.debug("cookies: " + cookiesList);
			}
			
			
			String parametersList = "{ ";
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
				parametersList += parameter.getKey() + "=";
				String[] values = parameter.getValue();
				for (int i = 0; i < values.length; i++)
					parametersList += values[i] + ";";
				
				parametersList += " ";
			}
			parametersList += " }";
			
			Logger.debug("parameters: " + parametersList);
		}
	}
	
	private static synchronized long getRerquestCounter() {
		return ++requestIdCounter;
	}
	
	@Override
	public void afterPhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			FacesContext ctx = event.getFacesContext();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();

			String requestId = (String) request.getAttribute(LoggingPhaseListener.REQUEST_ID_NAME);
			String start_ts = (String)request.getAttribute(LoggingPhaseListener.START_TS_NAME);
			long elapsed = System.currentTimeMillis() - Long.parseLong(start_ts); 
			
			Logger.info(request.getRequestURI() + " exiting request #" + requestId + " (" + Long.toString(elapsed) + "ms)");
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
