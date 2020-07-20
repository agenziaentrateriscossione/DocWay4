package it.highwaytech.apps.xdocway.apv;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecretazioneApv extends EntityBaseApv {
	private Date data;
	private String contenutoAtto;
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	public String getContenutoAtto() {
		return contenutoAtto;
	}
	public void setContenutoAtto(String contenutoAtto) {
		this.contenutoAtto = contenutoAtto;
	}

	public boolean isLinkContenutoAttoShow() {
		return getProcess() != null;
	}

	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	@Override
	public String toString() {
		return "DecretazioneApv - data: " + (data==null?"":df.format(data)) + super.toString();
	}
}
