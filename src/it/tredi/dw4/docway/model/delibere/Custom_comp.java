package it.tredi.dw4.docway.model.delibere;

public class Custom_comp {
	private String value = "";
	private boolean custom_pres = false;
	private boolean custom_ass = false;
	private boolean custom_assng = false;

	public Custom_comp() {
		// TODO Auto-generated constructor stub
		value = "";
		custom_pres = false;
		custom_ass = false;
		custom_assng = false;
	}
	
	public String getPresenzaForQuery(){
		String s = "";
		if(custom_pres) 
			s += ",\"Presente\"";
		if(custom_ass) 
			s += ",\"Assente giustificato\"";
		if(custom_assng) 
			s += ",\"Assente non giustificato\"";
		
		if(!s.isEmpty() && s.charAt(0)==',') 
			s = s.substring(1);
		
		if(s.isEmpty()) 
			return "";
		
		if(s.indexOf(",") != -1) 
			s = "{" + s + "}";
			
		return " ADJ [ALIAS_B]=" + s;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isCustom_pres() {
		return custom_pres;
	}

	public void setCustom_pres(boolean custom_pres) {
		this.custom_pres = custom_pres;
	}

	public boolean isCustom_ass() {
		return custom_ass;
	}

	public void setCustom_ass(boolean custom_ass) {
		this.custom_ass = custom_ass;
	}

	public boolean isCustom_assng() {
		return custom_assng;
	}

	public void setCustom_assng(boolean custom_assng) {
		this.custom_assng = custom_assng;
	}
	
}
