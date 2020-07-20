package it.tredi.dw4.utils.filters;

import it.tredi.dw4.utils.HttpServletRequestUploadWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFilter implements Filter {
	public static Long UPLOADFILTER_MAX_FILE_SIZE = -1L;
	
	private String repositoryPath;
	private Long maxFileSize;
	private DiskFileItemFactory factory;
	private ServletFileUpload upload;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.factory = new DiskFileItemFactory();
		
		setRepositoryPath(config);
		setMaxFileSize(config);
		
		this.upload = new ServletFileUpload(factory);
	}
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		if ((request instanceof HttpServletRequest)) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			
			if (ServletFileUpload.isMultipartContent(httpRequest)) {
				try {
					@SuppressWarnings("unchecked")
					List<FileItem> items = (List<FileItem>) this.upload.parseRequest(httpRequest);
					final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
					
					for (FileItem item : items) {
						if (item.isFormField())
							processFormField(item, parameterMap);
						else
							processFileField(item, httpRequest);
					}
 
					request = wrapRequest(httpRequest, parameterMap);
 
				} catch (FileUploadException ex) {
					ServletException servletEx = new ServletException();
					servletEx.initCause(ex);
					throw servletEx;
				}
			}
		}
			
		filterChain.doFilter(request, response);
	}

	private HttpServletRequest wrapRequest(HttpServletRequest request, final Map<String, String[]> parameterMap) {
		HttpServletRequestUploadWrapper wrappedRequest = new HttpServletRequestUploadWrapper(request);
		wrappedRequest.setParameterMap(parameterMap);
		return wrappedRequest;
	}
	
	/**
	 * Method for retrieving the temporary directory for storing uploaded file
	 * By default the system's temporary directory will be used if no repository path is configure in the web.xml
	 * @param config
	 */
	private void setRepositoryPath(FilterConfig config) {
		String repositoryPath = config.getInitParameter("it.treditech.jsfcomponents.filter.UploadFilter.repositoryPath");
		if (repositoryPath == null || repositoryPath.length() == 0) {
			repositoryPath = System.getProperty("java.io.tmpdir");
		}
		if (!repositoryPath.endsWith("/"))
			repositoryPath += "/";
		this.repositoryPath = repositoryPath;
		
		factory.setRepository(new File(this.repositoryPath));
	}
	
	private void setMaxFileSize(FilterConfig config) {
		String maxFileSize = config.getInitParameter("it.treditech.jsfcomponents.filter.UploadFilter.maxFileSize");
		
		if (maxFileSize == null || maxFileSize.length() == 0) {
			this.maxFileSize = UploadFilter.UPLOADFILTER_MAX_FILE_SIZE;
		}
		else
			this.maxFileSize = Long.parseLong(maxFileSize);
	}
	
	private void processFormField(FileItem formField, Map<String, String[]> parameterMap) {
		String name = formField.getFieldName();
		String value = formField.getString();
		String[] values = parameterMap.get(name);
 
		if (values == null) {
			// Not in parameter map yet, so add as new value.
			parameterMap.put(name, new String[] { value });
		} else {
			// Multiple field values, so add new value to existing array.
			int length = values.length;
			String[] newValues = new String[length + 1];
			System.arraycopy(values, 0, newValues, 0, length);
			newValues[length] = value;
			parameterMap.put(name, newValues);
		}
	}
 
	private void processFileField(FileItem fileField, HttpServletRequest request) {
		if (this.maxFileSize < 0 || fileField.getSize() <= this.maxFileSize)
			request.setAttribute(fileField.getFieldName(), fileField);
		else {
			fileField.delete();
			//TODO fare in modo che passi un messaggio all'applicazione
		}
	}
}
