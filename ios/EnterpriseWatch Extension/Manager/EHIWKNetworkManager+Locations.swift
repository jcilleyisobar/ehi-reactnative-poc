//
//  EHIWKNetworkManager+Locations.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation

extension EHIWKNetworkManager {
	func requestNearbyLocations(location: CLLocation, completionBlock: (responseObject: EHIWKLocationsResponse) -> Void) {
		let base = "https://qa-solr5-ehil.ctmsp.com/solr-locations-search/search/location/mobile"
		let latitude = location.coordinate.latitude
		let longitude = location.coordinate.longitude
		let requestURL = NSURL(string: "\(base)/spatial/\(latitude)/\(longitude)")
		
		if let validURL = requestURL {
			let request = NSURLRequest(URL: validURL)
			processRequest(request, completion: completionBlock)
		}
		
//		EHINetworkRequest *request =
//			[EHINetworkRequest get:@"%@/spatial/%.2f/%.2f", self.searchBase, region.center.latitude, region.center.longitude];
//		
//		[request parameters:^(EHINetworkRequest *request) {
//		request[@"locale"]   = NSLocale.ehi_identifier;
//		
//		// encode the radius if anything other than default is specified (always round up)
//		if(region.radius != 0.0) {
//		request[@"radius"] = @((int)(region.radius / 1000.0) + 1);
//		}
//		
//		if(filterQuery.activeFilters) {
//		// encode the filters into the parameters if they exist
//		[filterQuery encodeWithRequest:request];
//		// if we're filtering, don't show any non-enterprise locations
//		request[@"brand"] = @"ENTERPRISE";
//		}
//		}];
	}
}