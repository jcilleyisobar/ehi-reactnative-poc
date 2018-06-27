//
//  EHIWKResponseMessage.swift
//  Enterprise
//
//  Created by George Stuart on 10/18/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKResponseMessage: JSON {
    var code = JSONField<String>("code")
    var message = JSONField<String>("message")
    var priority = JSONField<String>("error")
}