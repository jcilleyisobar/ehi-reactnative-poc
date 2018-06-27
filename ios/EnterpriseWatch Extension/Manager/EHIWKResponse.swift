//
//  EHIWKResponse.swift
//  Enterprise
//
//  Created by George Stuart on 10/18/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKResponse: JSON {
    var messages = JSONField<[EHIWKResponseMessage]>("messages")
}