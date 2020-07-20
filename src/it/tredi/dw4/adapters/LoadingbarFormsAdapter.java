package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class LoadingbarFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public LoadingbarFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("observerId", root.attributeValue("observerId", ""));
		defaultForm.addParam("jReportParams", root.attributeValue("jReportParams", "")); //eventuali parametri da passare ai report
		defaultForm.addParam("printTemplate", root.attributeValue("printTemplate", ""));
		defaultForm.addParam("profileType", root.attributeValue("profileType", ""));
		defaultForm.addParam("outType", root.attributeValue("outType", ""));
		defaultForm.addParam("dataSource", root.attributeValue("dataSource", ""));
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	public void refresh(String login, String matricola) {
		//this.defaultForm.addParam("verbo", "loadingbar");
		
		// TODO modificato a causa del problema cache in login (verificare meglio, si tratta solo di un workaround)
		this.defaultForm.addParam("login", login); 
		this.defaultForm.addParam("matricola", matricola);
	}
	
	public void queryPage() {
		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("dbTable", "");
		this.defaultForm.addParam("query", "");
		this.defaultForm.addParam("selid", "");
	}
	
	public void downloadFile(String fileName) throws Exception {
		/*
		<xsl:variable name="theid">
			<xsl:call-template name="hxpstring">
				<xsl:with-param name="blockPath" select="$blockPath"/><xsl:with-param name="data" select="$blockData"/>
				<xsl:with-param name="thestring" select="concat('$hxp_data(',$realid,')')"/>
			</xsl:call-template>
		</xsl:variable>
		 */
		
		/*
		<xsl:variable name="nameVal">
			<xsl:choose>
				<xsl:when test="$name!=''">
					<xsl:call-template name="do-replace">
						<xsl:with-param name="text" select="$name"/>
						<xsl:with-param name="replace">'</xsl:with-param>
						<xsl:with-param name="by" select="'_'"/>
					</xsl:call-template>

					<!--
						Federico 21/04/09: modificato il test per controllare se la descrizione dell'allegato contiene o meno l'estensione del
						file per evitare di duplicarla [M 0000323]
					-->
					<xsl:variable name="ext">
						<xsl:text>.</xsl:text>
						<xsl:value-of select="substring-after($theid,'.')"/>
					</xsl:variable>
					<!-- il test equivale a not(endsWith($name,$ext) -->
					<xsl:if test="string-length($name)-string-length($ext)&lt;0 or substring($name,string-length($name)-string-length($ext)+1)!=$ext">
						<xsl:value-of select="$ext"/>
					</xsl:if>

					<!-- simone 7 Ott 2004 aggiunge .p7m se non ce l'ha-->
					<xsl:if test="contains(substring-after($theid,'.'),'.p7m') and not(contains(substring-after($name,'.'),'.p7m'))">
					 	<xsl:text>.p7m</xsl:text>
					</xsl:if>

				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$theid"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		 */
		
		this.defaultForm.addParam("verbo", "attach");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("id", fileName);
		this.defaultForm.addParam("name", fileName);
	}
	
}
