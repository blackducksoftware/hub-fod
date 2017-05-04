package com.blackducksoftware.integration.hub.fod.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.common.VulnerabilityReportConstants;
import com.blackducksoftware.integration.hub.fod.domain.FoDAppReleaseResponseItems;
import com.blackducksoftware.integration.hub.fod.domain.FoDAppResponseItems;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplication;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplicationRelease;
import com.blackducksoftware.integration.hub.fod.exception.FoDConnectionException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FoDRestConnectionService {
	
	private final Logger logger = LoggerFactory.getLogger(FoDRestConnectionService.class);
	
    private final static int CONNECTION_TIMEOUT = 10;
    private final static int WRITE_TIMEOUT = 30;
    private final static int READ_TIMEOUT = 30;
    public final static int MAX_SIZE = 50;
    
    private final static String APPLICATIONS = "applications";
    private final static String RELEASES = "releases";
    private final static String FOD_API_URL_VERSION = "/api/v3/";
    private final static String FPR_IMPORT_SESSIONID = "import-scan-session-id";
    // TODO: TO BE USED with 5/20 FoD
    private final static String REPORT_IMPORT_SESSIONID = "reports/import-report-session-id";
    private final static String FPR_IMPORT = "/dynamic-scans/import-scan";

    private OkHttpClient client;
    private String token;

    @Autowired
    HubFoDConfigProperties appProps;
  
    /**
     * Constructor that encapsulates the api
     *
     * @param key     api key
     * @param secret  api secret
     * @param baseUrl api url
     */
    public FoDRestConnectionService() {
     
        client = Create();

    }
    
    
    public List<FoDApplication> getFoDApplicationList()
    {
    	
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + this.getToken());
    	
    	HttpEntity<String> request = new HttpEntity<String>(headers);
    	ResponseEntity<FoDAppResponseItems> response = restTemplate.exchange(appProps.getFodBaseURL() + FOD_API_URL_VERSION + APPLICATIONS, 
    																HttpMethod.GET, request, FoDAppResponseItems.class);
    	FoDAppResponseItems fodAppResponse = response.getBody();
    	return fodAppResponse.getItems();
    
  
    }
    
    public List<FoDApplicationRelease> getFoDApplicationReleases(String applicationId)
    {
    	
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + this.getToken());
    	
    	HttpEntity<String> request = new HttpEntity<String>(headers);
    	ResponseEntity<FoDAppReleaseResponseItems> response = restTemplate.exchange(appProps.getFodBaseURL() + FOD_API_URL_VERSION + APPLICATIONS+"/"+applicationId+"/"+RELEASES, 
    																				HttpMethod.GET, request, FoDAppReleaseResponseItems.class);
    	FoDAppReleaseResponseItems fodAppReleaseResponse = response.getBody();
    	return fodAppReleaseResponse.getItems();
    
    
    }
    
    
    public String getFoDImportSessionId(String releaseId, long fileLength, String reportName, String reportNotes)
    {
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + this.getToken());
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	JsonObject requestJson = new JsonObject();
    	
    	requestJson.addProperty("releaseId", Integer.parseInt(releaseId));
    	requestJson.addProperty("fileLength", fileLength);
    	requestJson.addProperty("fileExtension", ".pdf");
    	requestJson.addProperty("reportName", reportName);
    	requestJson.addProperty("reportNotes", reportNotes);
    	
    	logger.debug("SENDING this to FoD:"+requestJson.toString());
    	
    	HttpEntity<String> request = new HttpEntity<String>(requestJson.toString(), headers);
    	
    	// TODO change this to /api/v3/reports/import-report-session-id & HttpMethod.PUT
    	ResponseEntity<String> response = restTemplate.exchange(appProps.getFodBaseURL() + FOD_API_URL_VERSION + RELEASES +"/"+releaseId+"/"+FPR_IMPORT_SESSIONID, 
    															HttpMethod.GET, request, String.class);
    	
    	JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(response.getBody()).getAsJsonObject();

    	return obj.get("importScanSessionId").getAsString();
    }
    
    public void uploadFoDPDF(String releaseId, String sessionId, String uploadFilePath, long fileLength) throws IOException
    {
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + this.getToken());
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	
    	int fragSize = VulnerabilityReportConstants.FRAG_SIZE;
    	byte[] fragBytes = null;
    	int totalBytesRead = 0;
    	int numberOfFrags = 0;
    	
    	//TODO: maybe calc total chunks to display to user
    	
    	//TODO: Test data, remove with new version of fod
    	File testFile = new File("/Users/dmeurer/TEST/TEST-hub-fod-upload.fpr");
    	fileLength = testFile.length();
    	
    	//uploadFilePath
    	 try (BufferedInputStream vulnFileStream = new BufferedInputStream(
                 						new FileInputStream("/Users/dmeurer/TEST/TEST-hub-fod-upload.fpr")))
    	 {
    		 
    		 while ( totalBytesRead < (int) fileLength )
    		 {
    			 int bytesRemaining = (int) (fileLength-totalBytesRead);
    			 if ( bytesRemaining < fragSize ) 
    			 {
    				 fragSize = bytesRemaining;
    			 }
    			 fragBytes = new byte[fragSize];
    			 int bytesRead = vulnFileStream.read(fragBytes, 0, fragSize);
    			 
    			 totalBytesRead += bytesRead;
    			 numberOfFrags++;
    			 
    			 System.out.println("Total Bytes Read: "+totalBytesRead+ " | Frag Number: "+numberOfFrags);
    			 
    			 HttpEntity<byte[]> request = new HttpEntity<byte[]>(fragBytes, headers);
    			 
    			 //TODO: Test with FPR PUT /api/v3/releases/{releaseId}/dynamic-scans/import-scan
    			 //TODO: offset, fileLength, importScanSessionId  ... frag needs to be -1 at the end
    			 ResponseEntity<String> response = restTemplate.exchange(
    					 appProps.getFodBaseURL() + FOD_API_URL_VERSION + RELEASES +"/"+releaseId+"/"+FPR_IMPORT
    					 +"?fragNo="+String.valueOf(numberOfFrags-1), 
							HttpMethod.PUT, request, String.class);
    				
    		 }
    	 }
    }
    
    /**
     * Used for authenticating in the case of a time out using the saved api credentials.
     * @throws Exception 
     */
    public void authenticate() throws Exception {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("scope", "https://hpfod.com/tenant")
                    .add("grant_type", "password")
                    .add("username", appProps.getFodTenantId()+"\\"+appProps.getFodUsername())
                    .add("password", appProps.getFodPassword())
                    .build();
         
            Request request = new Request.Builder()
                    .url(appProps.getFodBaseURL() + "/oauth/token")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful())
                throw new FoDConnectionException("Unexpected code " + response);
            
            logger.debug(appProps.getFodUsername().concat(" AUTHENTICATION to FoD SUCCEEDED"));
            
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            // Parse the Response
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(content).getAsJsonObject();
            this.token = obj.get("access_token").getAsString();
            
        } 
        catch(FoDConnectionException fce)
        {
        	if(fce.getMessage().contains(VulnerabilityReportConstants.FOD_UNAUTORIZED))
			{
				logger.error("FoD CONNECTION FAILED.  Please check the FoD URL, username, tenant, and password and try again.");
				logger.info("FoD URL="+appProps.getFodBaseURL());
				logger.info("FoD username="+appProps.getFodUsername());
				logger.info("FoD tennant="+appProps.getFodTenantId());
			}
        	else
        	{
        		logger.error("FoD Response was not successful with message: " + fce.getMessage());
        	}
        	throw fce;
        }
        
        catch(UnknownHostException uhe)
        {
        	logger.error("Unknown Host Error.  Are you connected to the internet?");
        	uhe.printStackTrace();
        	throw uhe;
        }
        
        catch (Exception e) {
        	logger.error("Authentication to FoD failed. Connection seems OK.");
            e.printStackTrace();
            throw e;
        }
    }



    /**
     * Creates a okHttp client to connect with.
     *
     * @return returns a client object
     */
    private OkHttpClient Create() {
        OkHttpClient.Builder baseClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

        //TODO: Proxy for FoD? If there's no proxy just create a normal client
        //if (proxy == null)
       return baseClient.build();

    }    
    
    public String getToken() {
        return token;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
