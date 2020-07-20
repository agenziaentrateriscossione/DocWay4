package it.highwaytech.apps.xdocway.apv;

public class OrdineApv extends EntityBaseApv {
	private String repertorioDecretazione;
	
	public String getRepertorioDecretazione() {
		return repertorioDecretazione;
	}

	public void setRepertorioDecretazione(String repertorioDecretazione) {
		this.repertorioDecretazione = repertorioDecretazione;
	}

	public String getFullRepertorioDecretazione() {
		if(repertorioDecretazione != null)
			return "DECRETAZIONI^APVEAPV-" + repertorioDecretazione.replace(".", "");
		return "";
	}
		
	@Override
	public String toString() {
		return "OrdineApv - repertorioDecretazione: " + (repertorioDecretazione==null?"":repertorioDecretazione) + super.toString();
	}
}
