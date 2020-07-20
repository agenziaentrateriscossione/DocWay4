package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;

public abstract class Loadingbar extends Page {
	
	protected boolean active = false;
	protected boolean error = false;
	
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
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(userBean); 
		if (handleErrorResponse(response)) {
			error = true;
		}
		return response;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return error;
	}	
	
}
