//
//  EHIWKLocation.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKLocation: JSON {
	let name =         JSONField<String>("locationNameTranslation")
	let addressLines = JSONField<[String]>("addressLines")
	let distance =     JSONField<Double>("calculatedDistance")
	let openSundays =  JSONField<Bool>("openSundays")
    let brand =        JSONField<EHIWKLocationBrand>("brand")
    let type =         JSONField<EHIWKLocationType>("locationType")
	let phoneNumber =  JSONField<String>("phoneNumber")
	let latitude =     JSONField<Double>("latitude")
	let longitude =    JSONField<Double>("longitude")
    let wayfindings =  JSONField<[EHIWKLocationWayfinding]>("wayfindings")

	let distanceUnit = EHIWKLocalizedString("metrics_miles", fallback: "mi")
    
    var annotationImageName: String {
        if let brand = self.brand.get() {
            switch brand {
                case .ALAMO:
                return "map_pin_alamo"
                case .NATIONAL:
                return "map_pin_national"
            default:
                break
            }
        }
        
        if let type = self.type.get() {
            switch type {
            case .AIRPORT:
                return "map_pin_airports"
            case .PORT:
                return "map_pin_port"
            case .TRAIN:
                return "map_pin_rail"
            default:
                break
            }
        }
        
        return "map_pin_standard"
    }
}