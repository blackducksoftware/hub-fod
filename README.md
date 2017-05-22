## Overview ##
Hub-FoD integration is a command line tool allowing the export of Hub Project Version component vulnerability information into a Fortify On Demand Application Release in the PDF format.  

## Where can I get the latest release? ##
Download the latest from release page: [https://github.com/blackducksoftware/hub-fod/releases] (https://github.com/blackducksoftware/hub-fod/releases)

## Usage ##
$ java -jar hub-fod-[VERSION].jar [OPTIONS: parameter=value]

Example:  
$ java -jar hub-fod-0.0.1.jar --hub.url=https://myhub.domain.com --hub.username=joe --hub.project=My Project --hub.project.version=3.4.5 --fod.username=joseph --fod.tenant.id=acme

Or just run the jar and answer the questions:  
$ java -jar hub-fod-0.0.1.jar

[OPTIONS]  
All options are optional, you will be prompted for any required parameters.  Alternatively, these parameters can be stored in an application.properties configuration file in the same directory as the jar file.

Required parameters: (if left unspecified, will prompt the user.)  
hub.url  
hub.username  
hub.password  
hub.project  
hub.project.version  
fod.username  
fod.password  
fod.tenant.id  

Optional Parameters  
hub.vulnerability.filters  
Comma delimited string of vulnerability Remediation status to filter the report. Possible values are:
NEW, NEEDS REVIEW, REMEDIATION REQUIRED, REMEDIATION COMPLETE, MITIGATED, PATCHED, IGNORED, DUPLICATE

Note, these are the ids (not name).  If you don't know the ids, no worries, the program will prompt you with names.  
fod.application.id  
fod.release.id  

proxy.host  
proxy.username  
proxy.password  
proxy.port  
proxy.ignore.hosts  

Default parameters.  Only specify if you want to override.  
fod.baseurl=https://www.hpfod.com  
fod.api.baseurl=https://api.hpfod.com  
output.folder=vuln-out  
output.html.filename=VulnerabilityReport.html  
output.pdf.filename=BlackDuckVulnerabilityReport.pdf  
hub.timeout=120  
logging.file=hub-fod-application.log  
report.notes= (blank by default, add custom notes here to be prepended to the summary notes of the FoD report)

DEBUG / VERBOSE MODE:
--verbose, --v  

## Application Mapping ##
To map Hub Project Versions to Fortify on Demand Application Releases, the Hub-FoD integration prompts the user to choose the Fortify on Demand Application and Release the first time it is run on a Hub Project Version.  The integration then stores the mapping in the Hub Project Version Notes field in the format: fod.app.release=[releaseid]. This allows the integration to run without prompting for subsequent runs. If this mapping is deleted in the Hub, the integration will simply prompt the user again.

