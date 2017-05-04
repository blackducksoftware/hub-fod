/**
 * hub-fod
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.fod;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.blackducksoftware.integration.hub.fod.domain.FoDApplication;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplicationRelease;
import com.blackducksoftware.integration.hub.fod.service.FoDImporterService;
import com.blackducksoftware.integration.hub.fod.service.VulnerabilityReportService;
import com.blackducksoftware.integration.hub.fod.utils.ConsoleUtils;


@SpringBootApplication
public class Application implements CommandLineRunner{
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	VulnerabilityReportService vulnReportService;
	
	@Autowired
	FoDImporterService fodImporter;
	
	@Autowired
	HubFoDConfigManager configurationManager;
	
	private final HubFoDConfigProperties appProps;
	
	public Application(HubFoDConfigProperties appProps) {
		this.appProps = appProps;
	}
	
	
	@Override
	public void run(String... args) {
			
		try
		{
			logger.info("STARTING Hub + Fortify on Demand Integration");
			configurationManager.processUsage(args);
			
			//Authenticate to FoD
			fodImporter.foDAuthenticate();
		
			// Generate the Vulnerability PDF
			logger.info("Generating Vulnerability Report for " + appProps.getHubProject() + " " +appProps.getHubProjectVersion());
			vulnReportService.generateVulnerabilityReport();
			
			
			//If FoD app was not specified or mapped, have the user choose
			if(appProps.getFodReleaseId().isEmpty())
			{
				System.out.println("");
				logger.info("FoD Application mapping not found");
				System.out.println("------------------------------------------------");
				System.out.println("Please choose the Fortify on Demand Application:");
				// Output the list of apps
				int i=1;
				List<FoDApplication> fodAppList = fodImporter.getFodApplications();
				for(FoDApplication fodApp : fodAppList)
				{
					System.out.println("("+i+") "+fodApp.getApplicationName());
					i++;
				}
				
				String appSelection = ConsoleUtils.readLine("Select #:");
				appProps.setFodApplicationId(String.valueOf(fodAppList.get(Integer.parseInt(appSelection)-1).getApplicationId()));
				
				System.out.println("");
				// based on the app choice, figure out which release
				List<FoDApplicationRelease> fodAppReleaseList = fodImporter.getFodApplicationReleases(fodAppList.get(Integer.parseInt(appSelection)-1).getApplicationId());
				// If there is only 1 release, assume that's the one
				if(fodAppReleaseList.size() == 1)
				{
					logger.info(fodAppReleaseList.get(0).getReleaseName() + " is the only release, and will be auto-selected.");
					appProps.setFodReleaseId(fodAppReleaseList.get(0).getReleaseId());
				}
				else // If more than 1 release exists, user needs to choose
				{
					
					System.out.println("Which " + fodAppList.get(Integer.parseInt(appSelection)-1).getApplicationName()+ " release?");
					int r=1;
					// Output the list of releases for this application
					for(FoDApplicationRelease fodAppRelease : fodAppReleaseList)
					{
						System.out.println("("+r+") "+fodAppRelease.getReleaseName());	
						r++;
					}
					String releaseSelection = ConsoleUtils.readLine("Select #:");
					System.out.println("");
					appProps.setFodReleaseId(fodAppReleaseList.get(Integer.parseInt(releaseSelection)-1).getReleaseId());
				}

				// Store the mapping in the Hub
				logger.info("Storing FoD Application mapping for future imports (Stored in the Hub Version Notes field)");
				vulnReportService.storeFodHubMapping(appProps.getFodReleaseId());
			}
			
			// TODO: Try and get the app and release NAME (can't if already mapped currently)
			logger.info("Importing PDF into Fortify On Demand");
			
			fodImporter.importVulnerabilityPDF();
			
			logger.info("Completed Successfully! [URL??]");
			
		}

		// Catch all exceptions and stop
		catch (Exception e)
		{
			logger.error("Hub-FoD could not complete successfully due to "+ e.getClass().getSimpleName() + ": " +e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {

		new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args);

	}
	
	

}
