/*package it.tredi.dw4.docway.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

public class NewDelega extends XmlEntity{

	private List<Rif> newDeleghe = new ArrayList<Rif>();
	private String startDate;
	private String endDate;
	private boolean permanente = false;
	private boolean uneditable = false;

	public List<Rif> getNewDeleghe() {
		return newDeleghe;
	}

	public void setNewDeleghe(List<Rif> newDeleghe) {
		this.newDeleghe = newDeleghe;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public LocalDate getStartLocalDate() {
		if(startDate != null && !startDate.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
			return LocalDate.parse(startDate, formatter);
		}
		else return null;
	}

	public void setStartLocalDate(LocalDate startDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		this.startDate = startDate.format(formatter);
	}

	public LocalDate getEndLocalDate() {
		if(endDate != null && !endDate.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
			return LocalDate.parse(endDate, formatter);
		}
		else return null;
	}

	public void setEndLocalDate(LocalDate endDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DEFAULT_DATE_FORMAT);
		this.endDate = endDate.format(formatter);
	}

	public boolean isPermanente() {
		return permanente;
	}

	public void setPermanente(boolean permanente) {
		this.permanente = permanente;
	}

	public boolean isUneditable() {
		return uneditable;
	}

	public void setUneditable(boolean uneditable) {
		this.uneditable = uneditable;
	}

	public NewDelega() {}

	public void clean() {
		this.newDeleghe.clear();
		this.newDeleghe.add(new Rif());
		this.startDate = null;
		this.endDate = null;
		this.permanente = false;
	}

	@Override
	public XmlEntity init(Document dom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix){
    	return new HashMap<String, String>();
    }
}*/
