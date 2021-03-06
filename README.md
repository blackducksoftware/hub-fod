<p align="center">
  <img width="25%" height="25%" src="https://www.blackducksoftware.com/sites/default/files/images/Logos/BD-S.png">
</p>

## Overview ##
Hub-FoD integration is a command line tool allowing the export of Hub Project Version component vulnerability information into a Fortify On Demand Application Release in the PDF format.

## Build ##

[![Build Status](https://travis-ci.org/blackducksoftware/hub-fod.svg?branch=master)](https://travis-ci.org/blackducksoftware/hub-fod) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/hub-fod/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/blackducksoftware/hub-fod/branches/master) 

## Where can I get the latest release? ##
Download the latest from [release page](https://github.com/blackducksoftware/hub-fod/releases)

## Notes ##
* Supported for Black Duck 4.8.x and up only.  Black Duck 4.7.x will produce 400 errors.  
* See the latest [hub-fod documentation](https://blackducksoftware.atlassian.net/wiki/spaces/PARTNERS/pages/328597509/Black+Duck+Fortify+on+Demand+plugin) for updates.

## Usage ##
$ java -jar hub-fod-[VERSION].jar [OPTIONS: parameter=value]

Example:  
$ java -jar hub-fod-1.1.2.jar --hub.url=https://myhub.domain.com --hub.username=joe --hub.project=My Project --hub.project.version=3.4.5 --fod.username=joseph --fod.tenant.id=acme

Or just run the jar and answer the questions:  
$ java -jar hub-fod-1.1.2.jar

[OPTIONS]  
All options are optional, you will be prompted for any required parameters.  Alternatively, these parameters can be stored in an application.properties configuration file in the same directory as the jar file.

Required parameters: (if left unspecified, will prompt the user.)  
hub.url   
hub.project  
hub.project.version

Black Duck authentication can either use username/password or api token.  If left unspecified, hub-fod will ask you which authentication method to use:   
hub.username   
hub.password  

hub.apiToken   
  
FoD Authentication can use either username/password or api key/secret.  If left unspecified, hub-fod will ask you which authentication method to use:    
fod.username  
fod.password  
fod.tenant.id  

fod.client.id   
fod.client.secret


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
fod.baseurl=https:/ams.fortify.com
fod.api.baseurl=https://api.ams.fortify.com  
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

