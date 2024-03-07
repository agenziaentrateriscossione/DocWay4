package it.tredi.dw4.beans;

public class AutoCloseMsg extends ReloadMsg {
	
	protected boolean autoclose = false;
	
	public AutoCloseMsg() {
		super();
	}
	
	public boolean isAutoclose() {
		return autoclose;
	}

	public void setAutoclose(boolean autoclose) {
		this.autoclose = autoclose;
	}
	
}
