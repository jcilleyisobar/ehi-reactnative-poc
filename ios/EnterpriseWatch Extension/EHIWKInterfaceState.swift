//
//  EHIWKInterfaceState.swift
//  Enterprise
//
//  Created by George Stuart on 10/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

let EHIWKInterfaceRefreshDataNotification = "EHIWKInterfaceRefreshDataNotification"
let EHIWKContextRefreshDataNotification = "EHIWKContextRefreshDataNotification"

enum EHIWKInterfaceState {
    case Initial
    case NoReservation
    case UpcomingReservation
    case CurrentReservation
}