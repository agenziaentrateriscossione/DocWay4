package it.tredi.dw4.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestUploadWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> parameterMap;
	
	public HttpServletRequestUploadWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public void setParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	@Override
	public String[] getParameterValues(String name) {
		if (parameterMap != null)
			return (String[]) parameterMap.get(name);
		else
			return null;
	}

	@Override
	public String getParameter(String name) {
		String[] params = getParameterValues(name);
		if (params == null)
			return null;
		return params[0];
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (parameterMap != null)
			return Collections.enumeration(parameterMap.keySet());
		else
			return null;
	}

}
