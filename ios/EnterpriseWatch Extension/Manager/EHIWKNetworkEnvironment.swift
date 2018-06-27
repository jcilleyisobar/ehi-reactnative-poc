//
//  EHIWKNetworkEnvironment.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

//enum EHIWKEnvironmentType {
//	case XQA1 = ["services": "asdf"]
//	case XQA2 = ["services": "asdf"]
//	case DEV1 = ["services": "asdf"]
//	case DEV2 = ["services": "asdf"]
//	case PROD = ["services": "asdf"]
//}

//struct EHIWKNetworkEnvironmentDetails: DictionaryLiteralConvertible, RawRepresentable {
//	let services: String
//	let servicesApiVersion: String
//	let servicesApiKey: String
//	let search: String
//	let searchApiKey: String
//	
//	typealias Key = String
//	typealias Value = String
//
//	/// Create an instance initialized with `elements`.
//	init(dictionaryLiteral elements: (String, String)...)
//	{
//		for (key,value) in elements {
//			switch(key) {
//				case "services":
//					self.services = value
//					break;
//				case "servicesApiVersion":
//					self.servicesApiVersion = value
//					break;
//				case "servicesApiKey":
//					self.servicesApiKey = value
//					break;
//				case "search":
//					self.search = value
//					break;
//				case "searchApiKey":
//					self.searchApiKey = value
//					break;
//			}
//		}
//	}
//
//	init?(rawValue: Dictionary<String,String>) {
//		self.services = rawValue["services"]!
//		self.servicesApiVersion = rawValue["servicesApiVersion"]!
//		self.servicesApiKey = rawValue["servicesApiKey"]!
//		self.search = rawValue["search"]!
//		self.searchApiKey = rawValue["searchApiKey"]!
//	}
//	
//	var rawValue: Dictionary<String,String> {
//		var dictionary = [String: String]()
//		
//		dictionary["services"] = self.services
//		dictionary["servicesApiVersion"] = self.servicesApiVersion
//		dictionary["servicesApiKey"] = self.servicesApiKey
//		dictionary["search"] = self.search
//		dictionary["searchApiKey"] = self.searchApiKey
//		
//		return dictionary
//	}
//}

//switch(type) {
//case EHIEnvironmentTypeQa1:
//	self.services           = @"https://www-enterprise-msi-xqa1.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://qa-solr5-ehil.ctmsp.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//case EHIEnvironmentTypeQa2:
//	self.services           = @"https://www-enterprise-msi-xqa2.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://qa-solr5-ehil.ctmsp.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//case EHIEnvironmentTypeDev1:
//	self.services           = @"https://www-enterprise-msi-int1.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://qa-solr5-ehil.ctmsp.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//case EHIEnvironmentTypeDev2:
//	self.services           = @"https://www-enterprise-msi-int2.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://qa-solr5-ehil.ctmsp.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//case EHIEnvironmentTypeBeta:
//	self.services           = @"https://www-enterprise-msi.enterprise.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://locations.enterprise.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//case EHIEnvironmentTypeProd:
//	self.services           = @"https://www-enterprise-msi.enterprise.ehiaws.com/enterprise-msi";
//	self.servicesApiVersion = 1;
//	self.servicesApiKey     = @"x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=";
//	self.search             = @"https://locations.enterprise.com/solr-locations-search/search/location/mobile";
//	self.searchApiKey       = @"bd6dfa74-8881-4db5-8268-1de81dd504e8";
//	break;
//}