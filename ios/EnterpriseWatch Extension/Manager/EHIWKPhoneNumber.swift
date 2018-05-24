//
//  EHIWKPhoneNumber.swift
//  Enterprise
//
//  Created by George Stuart on 10/18/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKPhoneNumber: JSON {
    var phoneNumber = JSONField<String>("phone_number")
    var phoneType =   JSONField<EHIWKPhoneType>("phone_type", decoder: {
        (value: Any?) -> EHIWKPhoneType? in
        if let rawValue = value as? String,
            let enumValue = EHIWKPhoneType(rawValue: rawValue) {
                return enumValue
        }
        return nil
    })
}