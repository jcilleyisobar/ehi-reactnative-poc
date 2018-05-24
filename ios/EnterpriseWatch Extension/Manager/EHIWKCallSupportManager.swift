//
//  EHIWKCallSupportManager.swift
//  Enterprise
//
//  Created by cgross on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation
import WatchKit

class EHIWKCallSupportManager: NSObject {
    static let sharedInstance = EHIWKCallSupportManager()
    
    private var phoneMap: [EHIWKPhoneType: String] = [:]
    
    func fetchConfigDataIfNecessary(completionBlock: (phoneMap: [EHIWKPhoneType: String]) -> Void) {
        if !phoneMap.isEmpty {
            completionBlock(phoneMap: phoneMap)
            return
        }
        
        EHIWKNetworkManager.sharedInstance.requestConfig { (responseObject: EHIWKConfigResponse) -> Void in
            if let supportPhones = responseObject.supportPhoneNumbers.get() {
                for phone in supportPhones {
                    if let type = phone.phoneType.get() {
                        self.phoneMap[type] = phone.phoneNumber.get()
                    }
                }
            }
            
            completionBlock(phoneMap: self.phoneMap)
        }
    }
    
    func callPhoneNumber(type: EHIWKPhoneType) {
        fetchConfigDataIfNecessary { (phoneMap) -> Void in
            if let phoneNumber = phoneMap[type],
                let phoneURL = NSURL(string: "tel:\(phoneNumber)") {
                    WKExtension.sharedExtension().openSystemURL(phoneURL)
            }
        }
    }
}
