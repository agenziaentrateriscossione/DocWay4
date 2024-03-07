package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Document;

import java.net.URLEncoder;
import java.util.Map;

public class PostitFile extends XmlEntity {

	private String title;
	private String id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ritorna il nome del file encodato per l'utilizzo in URL
	 * @return
	 */
	public String titleUrlEncoded() {
		String encodedTitle = getTitle();
		try {
			encodedTitle = URLEncoder.encode(encodedTitle, "UTF-8");
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return encodedTitle;
	}

	/**
	 * ritorna l'id del file encodato per l'utilizzo in URL
	 * @return
	 */
	public String idUrlEncoded() {
		String encodedName = getId();
		try {
			encodedName = URLEncoder.encode(encodedName, "UTF-8");
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return encodedName;
	}

	@Override
	public PostitFile init(Document dom) {
		this.title = XMLUtil.parseAttribute(dom, "postit_file/@title");
		this.id = XMLUtil.parseAttribute(dom, "postit_file/@id");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
}
