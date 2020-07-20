package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;

public abstract class LoadingbarStampe extends Page {
	
	protected boolean active = false;
	
	public abstract void init(XMLDocumento response);
	
	public abstract int getPercentage() throws Exception;
	
	public abstract boolean isCompleted() throws Exception;
	
	public abstract LoadingbarFormsAdapter getFormsAdapter();
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	protected XMLDocumento _refresh() throws Exception {
		UserBean userBean = getUserBean();
		getFormsAdapter().refresh(userBean.getLogin(), userBean.getMatricola());
		return getFormsAdapter().getDefaultForm().executePOST(userBean);
	}	
	
}
