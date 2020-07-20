package it.highwaytech.apps.xdocway.apv;

public enum StatoAvanzamentoApv {
	TuttiFlussiKillati {
		public String getI18N() {
			return "apv.stato_tuttiflussikillati";
		}
		public String getImgName() {
			return "wf_killati";
		}
		public int fieldForOrder() {
			return 10;
		}
	},
	Altro {
		public String getI18N() {
			return "apv.stato_altro";
		}
		public String getImgName() {
			return "altro";
		}
		public int fieldForOrder() {
			return 200;
		}
	},
	
	//Decretazione
	InLavorazione {
		public String getI18N() {
			return "apv.stato_inlavorazione";
		}
		public String getImgName() {
			return "in_lavorazione";
		}
		public int fieldForOrder() {
			return 20;
		}
	},
	Adottata {
		public String getI18N() {
			return "apv.stato_adottata";
		}
		public String getImgName() {
			return "adottata";
		}
		public int fieldForOrder() {
			return 30;
		}
	},
	Rifiutata {
		public String getI18N() {
			return "apv.stato_rifiutata";
		}
		public String getImgName() {
			return "rifiutata";
		}
		public int fieldForOrder() {
			return 40;
		}
	},
	
	//Ordine
	//InLavorazione,
	Inviata {
		public String getI18N() {
			return "apv.stato_inviata";
		}
		public String getImgName() {
			return "inviata";
		}
		public int fieldForOrder() {
			return 35;
		}
	},
	//Rifiutata,
	
	//Fattura
	DaAssegnare {
		public String getI18N() {
			return "apv.stato_daassegnare";
		}
		public String getImgName() {
			return "da_assegnare";
		}
		public int fieldForOrder() {
			return 13;
		}
	},
	RifiutataSDI {
		public String getI18N() {
			return "apv.stato_rifiutatasdi";
		}
		public String getImgName() {
			return "rifiutata_sdi";
		}
		public int fieldForOrder() {
			return 23;
		}
	},
	InLiquidazioneDallaDirezione {
		public String getI18N() {
			return "apv.stato_inliquidazionedalladirezione";
		}
		public String getImgName() {
			return "in_liquidazione_dalla_direzione";
		}
		public int fieldForOrder() {
			return 33;
		}
	},
	InAttesaDiPagamento {
		public String getI18N() {
			return "apv.stato_inattesadipagamento";
		}
		public String getImgName() {
			return "in_attesa_di_pagamento";
		}
		public int fieldForOrder() {
			return 43;
		}
	},
	OrdinativoEffettuato {
		public String getI18N() {
			return "apv.stato_ordinativoeffettuato";
		}
		public String getImgName() {
			return "ordinativo_effettuato";
		}
		public int fieldForOrder() {
			return 53;
		}
	},
	InContestazioneGF {
		public String getI18N() {
			return "apv.stato_incontestazionegf";
		}
		public String getImgName() {
			return "in_contestazione_gf";
		}
		public int fieldForOrder() {
			return 63;
		}
	},
	InContestazione {
		public String getI18N() {
			return "apv.stato_incontestazione";
		}
		public String getImgName() {
			return "in_contestazione";
		}
		public int fieldForOrder() {
			return 73;
		}
	},
	
	
	
	NessunFlusso {
		public String getI18N() {
			return "apv.stato_nessunflusso";
		}
		public String getImgName() {
			return "nessunflusso";
		}
		public int fieldForOrder() {
			return -5;
		}
},
	ErroreVariabiliFlussoNonTrovate {
		public String getI18N() {
			return "apv.stato_errorevariabiliflussonontrovate";
		}
		public String getImgName() {
			return "errorevariabiliflussonontrovate";
		}
		public int fieldForOrder() {
			return -10;
		}
	},
	Errore {
		public String getI18N() {
			return "apv.stato_errore";
		}
		public String getImgName() {
			return "errore";
		}
		public int fieldForOrder() {
			return -20;
		}
	}
	
	;

	public abstract String getImgName();
	public abstract String getI18N();
	public abstract int fieldForOrder();
}
