package it.tredi.dw4.beans;

import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;

/**
 * Modale di attesa di completamento della stored procedure LUA
 */
public abstract class LuaStoredProcThrobber extends Loadingbar {

	private String title = "";
	private String status = "";
	private String exceptions = "";
	private int percentage = 0;
	private String startDate = "";
	private String endDate = "";
	
	// informazioni specifiche relative all'esito della stored procedure LUA
	private String evaluationDate = "";
	private int tot = 0; // numero totale di record elaborati
	private int done = 0; // numero di record aggiornati/elaborati
	private String doneList = ""; // elenco di record aggiornati/elaborati
	private int locked = 0; // numero di record bloccati
	private String lockedList = ""; // elenco di record bloccati
	private int failed = 0; // numero di errori riscontrati (elaborazione/salvataggio)
	private String failedSaveList = ""; // elenco di record con errore in fase di salvataggio
	private String failedActionList = ""; // elenco di record con errore in fase di elaborazione
	private String procFault = ""; // eventuale indicazione dell'errore che ha comportato il fallimento dell'operazione (es. parametri di input incorretti)
	
	@Override
	public int getPercentage() throws Exception {
		return percentage;
	}
	
	@Override
	public boolean isCompleted() throws Exception {
		 return (endDate != null && endDate.length() > 0);
	}
	
	/**
	 * Analisi common della response ricevuta da una chiamata di refresh
	 * @param response
	 */
	public void commonInit(XMLDocumento response) {
		if (response != null) {
			this.title = response.getElementText("/response/title", "").trim();
			this.status = response.getElementText("/response/status", "").trim();
			this.exceptions = response.getElementText("/response/exceptions", "").trim();
			this.startDate = response.getElementText("/response/startDate", "").trim();
			this.endDate = response.getElementText("/response/stopDate", "").trim();
			
			int total = Integer.parseInt(response.getElementText("/response/nStat", "0").trim()) - 1;
			int current = Integer.parseInt(response.getElementText("/response/nDoc", "0").trim());
			if (total != 0)
				this.percentage = (current * 100) / total; 
			
			Element statisticsEl = (Element) response.selectSingleNode("/response/statistics");
			if (statisticsEl != null) {
				this.tot = Integer.parseInt(statisticsEl.attributeValue("recordCounter", "0"));
				this.done = Integer.parseInt(statisticsEl.attributeValue("changedCounter", "0"));
				this.doneList = statisticsEl.attributeValue("changedList", "");
				this.locked = Integer.parseInt(statisticsEl.attributeValue("failLockCounter", "0"));
				this.lockedList = statisticsEl.attributeValue("failLockList", "");
				this.failed = Integer.parseInt(statisticsEl.attributeValue("failActionCounter", "0")) + Integer.parseInt(statisticsEl.attributeValue("failSaveCounter", "0"));
				this.failedActionList = statisticsEl.attributeValue("failActionList", "");
				this.failedSaveList = statisticsEl.attributeValue("failSaveList", "");
				this.procFault = statisticsEl.attributeValue("procFault", "");
			}
		}
	}
	
	public String getTitle() {
		return title;
	}

	public String getStatus() {
		return status;
	}

	public String getExceptions() {
		return exceptions;
	}
	
	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getEvaluationDate() {
		return evaluationDate;
	}

	public int getTot() {
		return tot;
	}

	public int getDone() {
		return done;
	}

	public String getDoneList() {
		return doneList;
	}

	public int getLocked() {
		return locked;
	}

	public String getLockedList() {
		return lockedList;
	}

	public int getFailed() {
		return failed;
	}

	public String getFailedSaveList() {
		return failedSaveList;
	}

	public String getFailedActionList() {
		return failedActionList;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	
	public String getProcFault() {
		return procFault;
	}
	
}
