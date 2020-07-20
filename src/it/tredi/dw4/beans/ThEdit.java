package it.tredi.dw4.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.adapters.ThEditFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.utils.string.Text;

import org.dom4j.Document;

public abstract class ThEdit extends Page {

	protected boolean active = false;
	
	protected String keypath = "";
	protected String name = "";
	protected String values = "";
	protected List<Option> relDescrOptions = new ArrayList<Option>();
	protected int relDescrSelected = 0;
	
	public abstract void init(Document dom);
	
	public abstract String salvaNodo() throws Exception;
	
	public abstract String reload() throws Exception;
	
	public abstract ThEditFormsAdapter getFormsAdapter();
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getKeypath() {
		return keypath;
	}

	public void setKeypath(String keypath) {
		this.keypath = keypath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValues() {
		return values;
	}

	public void setValues(String rels) {
		this.values = rels;
	}
	
	public List<Option> getRelDescrOptions() {
		return relDescrOptions;
	}

	public void setRelDescrOptions(List<Option> relDescrOptions) {
		this.relDescrOptions = relDescrOptions;
	}
	
	public int getRelDescrSelected() {
		return relDescrSelected;
	}

	public void setRelDescrSelected(int relDescrSelected) {
		this.relDescrSelected = relDescrSelected;
	}
	
	public String close() {
		this.active = false;
		return null;
	}
	
	/**
	 * Caricamento dei valori di relDescr
	 */
	public void loadRelDescr() {
		int relIndex = new Integer(getFormsAdapter().getDefaultForm().getParam("relIndex")).intValue();
		String[] rd = getFormsAdapter().getDefaultForm().getParam("relDescr").split(",");
		if (rd != null && rd.length > 0) {
			for (int i=0; i<rd.length; i++) {
				Option rdOption = new Option();
				rdOption.setLabel(rd[i]);
				rdOption.setValue(rd[i]);
				if (i == relIndex) {
					rdOption.setSelected("true");
					relDescrSelected = i;
				}
				
				relDescrOptions.add(rdOption);
			}
		}
	}
	
	private int getRelsStart(String rels, String seek) {
		int start = 0;
		if (!rels.startsWith(seek))
			start = rels.indexOf("," + seek);
		
		if (start < 0)
			return start;
		
		if (start > 0)
			start++;
		
		/*
		if (start < 0 || start > rels.length())
			start = 0;
		*/
		
		return start;	
	}
	
	private int getRelsStop(String rels, String seek, int start) {
		int stop = 0;
		
		if (start >= 0) {
			boolean exit = false;
			stop = start;
			while(!exit) {
				int pos = this.findRelStop(rels, stop + seek.length());
				if (pos < 0 || pos > rels.length() - 1 - seek.length()) {
					stop = rels.length();
					exit = true;
				}
				else {
					stop = pos;
					
					if (!rels.substring(stop + 1, stop + 1 + seek.length()).equals(seek))
						exit = true;
				}
			}
		}
		
		return stop;
	}
	
	private int findRelStop(String where, int from) {
		int result = -1;
		boolean found = false;
		
		while (!found) {
			int pos = where.indexOf(",", from);
			if (pos < 0)
				return pos;
			result = pos;
			from = pos + 1;
			if (Character.isDigit(where.charAt(from))) {
				while (Character.isDigit(where.charAt(from)))
					from++;
			
				if (where.charAt(from) == ' ')
					found = true;
			}
		}
		
		return result;
	}

	/**
	 * Formattazione dei valori relativi a tipologia o mezzo di trasmissione in base
	 * al canale
	 * da STRINGA PER XML a TEXTAREA
	 */
	public void rels2Form() {
		String rels = getFormsAdapter().getDefaultForm().getParam("rels");
		if (rels != null && !rels.equals("")) {
			int theRel = relDescrSelected + 1;
			String seek = theRel + " ";
			
			int start = this.getRelsStart(rels, seek);
			if (start >= 0) {
				int stop = this.getRelsStop(rels, seek, start);
				
				String[] valuesList = rels.substring(start + seek.length(), stop).split("," + seek);
				if (valuesList != null && valuesList.length > 0) {
					for (int i=0; i<valuesList.length; i++) {
						String value = valuesList[i].trim();
						// TODO da verificare, deriva dalla riga 160 di XMLDocumento (xmlString = xmlString.replaceAll("&#", "&amp;#");)
						value = Text.htmlToText(value);
						
						values = values + value + "\n";
					}
				}
			}
		}
	}
	
	/**
	 * recupera il nodo padre sul thesauro
	 * @return
	 */
	public String getBtFromRels() {
		String value = "";
		
		String rels = getFormsAdapter().getDefaultForm().getParam("rels");
		if (rels != null && !rels.equals("")) {
			int theRel = 2; // BT
			String seek = theRel + " ";
			
			int start = this.getRelsStart(rels, seek);
			if (start >= 0) {
				int stop = this.getRelsStop(rels, seek, start);
				
				String[] valuesList = rels.substring(start + seek.length(), stop).split("," + seek);
				if (valuesList != null && valuesList.length == 1) {
					value = valuesList[0].trim();
					value = Text.htmlToText(value);
				}
			}
		}
		
		return value;
	}
	
	/**
	 * Formattazione dei valori relativi a tipologia o mezzo di trasmissione in base
	 * al canale
	 * da TEXTAREA a STRINGA PER XML
	 */
	public String form2Rels() {
		String rels = getFormsAdapter().getDefaultForm().getParam("rels");
		
		if (values != null) {
			int theRel = relDescrSelected + 1;
			String seek = theRel + " ";
			
			int start = this.getRelsStart(rels, seek);
			int stop = 0;
			if (start >= 0) {
				int pos = 0;
				if (start > 0) start++;
				stop = start;
				
				boolean prosegui = true;
				
				while (prosegui) {
					pos = this.findRelStop(rels, stop+seek.length());
					if (pos < 0) {
						stop = rels.length();
						prosegui = false;	
					}
					else {
						stop = pos + 1;
						if (!rels.substring(stop, stop+seek.length()).equals(seek))
							prosegui = false;
					}
				}
			}
			else
				start = 0;
						
			String[] valuesList = values.split("\n");
			String relString = "";
			for (int i=0; i<valuesList.length; i++) 
				if (valuesList[i] != null && valuesList[i].trim().length() > 0)
					relString += theRel + " "  + valuesList[i] + ",";
			
			rels = rels.substring(0, start) + relString + rels.substring(stop);
			if (rels.length() > 0 && rels.substring(rels.length()-1).equals(","))
				rels = rels.substring(0, rels.length()-1);
		}
		
		return rels;
	}
	
}
