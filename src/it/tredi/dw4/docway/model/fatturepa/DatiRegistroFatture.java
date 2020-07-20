package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class DatiRegistroFatture extends XmlEntity {
	
	//private String progrReg = "";  // TODO occorre gestirlo come progressivo oppure puo' essere il num repertorio?
	private String numFattura = "";
	private String dataEmissioneFattura = "";
	private String oggettoFornitura = "";
	private String importoTotale = "";
	private String dataScadenzaFattura = "";
	private String estremiImpegno = "";
	private String finiIVA = "";
	private String cig = "";
	private String cup = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.numFattura 			= XMLUtil.parseStrictAttribute(dom, "datiRegistroFatture/@numFattura");
		this.dataEmissioneFattura 	= XMLUtil.parseStrictAttribute(dom, "datiRegistroFatture/@dataEmissioneFattura");
		this.oggettoFornitura 		= XMLUtil.parseStrictElement(dom, "datiRegistroFatture/oggettoFornitura");
		this.importoTotale 			= XMLUtil.parseStrictAttribute(dom, "datiRegistroFatture/@importoTotale");
		this.dataScadenzaFattura 	= XMLUtil.parseStrictAttribute(dom, "datiRegistroFatture/@dataScadenzaFattura");
		this.estremiImpegno 		= XMLUtil.parseStrictElement(dom, "datiRegistroFatture/estremiImpegno");
		this.finiIVA 				= XMLUtil.parseStrictAttribute(dom, "datiRegistroFatture/@finiIVA");
		this.cig 					= XMLUtil.parseStrictElement(dom, "datiRegistroFatture/cig");
		this.cup 					= XMLUtil.parseStrictElement(dom, "datiRegistroFatture/cup");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	String dateFormat = Const.DEFAULT_DATE_FORMAT; // TODO formato da caricare da file di properties
    	
		params.put(prefix+".@numFattura", this.numFattura);
    	if (this.dataEmissioneFattura.length() == 10)
    		params.put(prefix+".@dataEmissioneFattura", DateUtil.changeDateFormat(this.dataEmissioneFattura, dateFormat, "yyyyMMdd")); 
    	else
    		params.put(prefix+".@dataEmissioneFattura", this.dataEmissioneFattura);
    	params.put(prefix+".oggettoFornitura", this.oggettoFornitura);
    	params.put(prefix+".@importoTotale", this.importoTotale);
    	if (this.dataScadenzaFattura.length() == 10)
    		params.put(prefix+".@dataScadenzaFattura", DateUtil.changeDateFormat(this.dataScadenzaFattura, dateFormat, "yyyyMMdd"));
    	else
    		params.put(prefix+".@dataScadenzaFattura", this.dataScadenzaFattura);
    	params.put(prefix+".estremiImpegno", this.estremiImpegno);
    	if (StringUtil.booleanValue(this.finiIVA))
    		params.put(prefix+".@finiIVA", "si");
    	params.put(prefix+".cig", this.cig);
    	params.put(prefix+".cup", this.cup);
    	
    	return params;
	}
	
	public String getNumFattura() {
		return numFattura;
	}

	public void setNumFattura(String numFattura) {
		this.numFattura = numFattura;
	}

	public String getDataEmissioneFattura() {
		return dataEmissioneFattura;
	}

	public void setDataEmissioneFattura(String dataEmissioneFattura) {
		this.dataEmissioneFattura = dataEmissioneFattura;
	}

	public String getOggettoFornitura() {
		return oggettoFornitura;
	}

	public void setOggettoFornitura(String oggettoFornitura) {
		this.oggettoFornitura = oggettoFornitura;
	}

	public String getImportoTotale() {
		return importoTotale;
	}

	public void setImportoTotale(String importoTotale) {
		this.importoTotale = importoTotale;
	}

	public String getDataScadenzaFattura() {
		return dataScadenzaFattura;
	}

	public void setDataScadenzaFattura(String dataScadenzaFattura) {
		this.dataScadenzaFattura = dataScadenzaFattura;
	}

	public String getEstremiImpegno() {
		return estremiImpegno;
	}

	public void setEstremiImpegno(String estremiImpegno) {
		this.estremiImpegno = estremiImpegno;
	}

	public String getFiniIVA() {
		return finiIVA;
	}

	public void setFiniIVA(String finiIVA) {
		this.finiIVA = finiIVA;
	}

	public String getCig() {
		return cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

	public String getCup() {
		return cup;
	}

	public void setCup(String cup) {
		this.cup = cup;
	}

}
