//
//  EHIWKLocationsResponse.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKLocationsResponse: EHIWKResponse {
	let radius =    JSONField<Double>("radiusUsedInKilometers")
	let locations = JSONField<[EHIWKLocation]>("locationsResult")
}