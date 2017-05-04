package com.blackducksoftware.integration.hub.fod.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplication;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplicationRelease;

@Service
public class FoDImporterService {

	private static final String REPORT_NAME = "Black Duck Open Source Vulnerability Report for ";
	private static final String REPORT_NOTES = "";

	@Autowired
	FoDRestConnectionService fodRestClient;

	@Autowired
	HubFoDConfigProperties appProps;

	private final Logger appLog = LoggerFactory.getLogger(VulnerabilityReportService.class);

	public void foDAuthenticate() throws Exception {
		// Authenticate to FoD
		fodRestClient.authenticate();
	}

	public void importVulnerabilityPDF() throws IOException{
		
		// InputStream is = new FileInputStream(outputPDF);
		long vulnPdfLength = new File(appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename())).length();
		appLog.debug(appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename()) + "PDF size: " + vulnPdfLength);
		
		// TODO: test Notes field and understand how it manifests.
		String importSessionId = fodRestClient.getFoDImportSessionId(appProps.getFodReleaseId(), vulnPdfLength,
				REPORT_NAME + appProps.getHubProject() + " " + appProps.getHubProjectVersion(), REPORT_NOTES);
		appLog.debug("FoD Import Session Id: " + importSessionId);

		// TODO: upload the PDF
		fodRestClient.uploadFoDPDF(appProps.getFodReleaseId(), importSessionId, appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename()), vulnPdfLength);

		

	}

	public List<FoDApplication> getFodApplications() {
		return fodRestClient.getFoDApplicationList();
	}

	public List<FoDApplicationRelease> getFodApplicationReleases(String applicationId) {
		return fodRestClient.getFoDApplicationReleases(applicationId);
	}

}
