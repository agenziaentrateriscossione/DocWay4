package it.tredi.dw4.adapters;

import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.DocWayProperties;

public class AdaptersConfigurationLocator {
	private static final String DEFAULT_PROTOCOL = "http";
	private static final String DEFAULT_USER_AGENT = "HCAPI";
	
	private static class SingletonHolder { 
        public static final AdaptersConfigurationLocator instance = new AdaptersConfigurationLocator();
	}
	
	public static AdaptersConfigurationLocator getInstance() {
        return SingletonHolder.instance;
	}
	
	public class AdapterConfig {
		private String host;
		private int port; 
		private String protocol;
		private String resource;
		private String userAgent;
		
		public String getHost() {
			return host;
		}
		public int getPort() {
			return port;
		}
		public String getProtocol() {
			return protocol;
		}
		public String getResource() {
			return resource;
		}
		public String getUserAgent() {
			return userAgent;
		}
	}
	
	public AdapterConfig getAdapterConfiguration(String adapter) throws Exception {
		AdapterConfig adapterConfig = new AdapterConfig();
		
		String host = DocWayProperties.readProperty(DocWayProperties.ADAPTERS_NAMESPACE, adapter + ".host", "");
		String protocol = DocWayProperties.readProperty(DocWayProperties.ADAPTERS_NAMESPACE, adapter + ".protocol", AdaptersConfigurationLocator.DEFAULT_PROTOCOL);
		String resource = DocWayProperties.readProperty(DocWayProperties.ADAPTERS_NAMESPACE, adapter + ".resource", "");
		String userAgent = DocWayProperties.readProperty(DocWayProperties.ADAPTERS_NAMESPACE, adapter + ".userAgent", AdaptersConfigurationLocator.DEFAULT_USER_AGENT);
		String portStr = DocWayProperties.readProperty(DocWayProperties.ADAPTERS_NAMESPACE, adapter + ".port", "");
		
		try {
			int port = Integer.parseInt(portStr);
			adapterConfig.port = port;
		}
		catch (Exception nfe) {
			throw new Exception(I18N.mrs("AdaptersConfigurationLocator.configurationException", new Object[]{adapter}), nfe);
		}
		
		if (host == null || resource == null)
			throw new Exception(I18N.mrs("AdaptersConfigurationLocator.configurationException", new Object[]{adapter}));
		else {
			adapterConfig.host = host;
			adapterConfig.protocol = protocol;
			adapterConfig.resource = resource;
			adapterConfig.userAgent = userAgent;
		}
		
		return adapterConfig;
	}
}

