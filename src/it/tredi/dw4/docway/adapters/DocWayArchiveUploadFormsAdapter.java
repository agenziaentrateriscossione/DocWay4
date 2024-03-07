package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.docway.model.CompressedArchive;

public class DocWayArchiveUploadFormsAdapter extends DocEditFormsAdapter {

	public DocWayArchiveUploadFormsAdapter(AdapterConfig config) {
		super(config);
	}

	public void unZipAndUploadArchive(CompressedArchive archive) {
		this.defaultForm.addParam("verbo", "uploadArchive");
		this.defaultForm.addParam("xverb", "unzipAndUpload");
		this.defaultForm.addParam("da_firmare", String.valueOf(archive.isDa_firmare()));
	}
	

}
