//
//  EHIWKNetworkManager+Config.swift
//  Enterprise
//
//  Created by George Stuart on 10/18/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

extension EHIWKNetworkManager {
    func requestConfig(completionBlock: (responseObject: EHIWKConfigResponse) -> Void) {
        let base = "https://www-enterprise-msi-xqa2.ehiaws.com/enterprise-msi"
        let requestURL = NSURL(string: "\(base)/api/support/reservations/contact/\(NSLocale.ehi_region())")

        if let validURL = requestURL {
            let request = NSMutableURLRequest(URL: validURL)
            request.addValue("x5pyC6t7HmwNaubXAYioxRnSOJXauSbToCyj+mBJFDM=", forHTTPHeaderField: "Ehi-API-Key")
            request.addValue(NSLocale.ehi_region(), forHTTPHeaderField: "Country-Of-Residence-Code")
            processRequest(request, completion: completionBlock)
        }
    }
}