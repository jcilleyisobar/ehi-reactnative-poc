//
//  EHIWKLaunchInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKLaunchInterfaceController: EHIWKInterfaceController {

	static let storyboardIdentifier = "EHIWKLaunchInterfaceController"
	
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)        
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
		EHIWKLog("WILL ACTIVATE: lauch interface controller")

//        getExtensionDelegate()?.refreshInterfaceState()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

	// MARK:- Notification Action Handling
//	override func handleActionWithIdentifier(identifier: String?, forLocalNotification localNotification: UILocalNotification) {
//		if let identifier = identifier {
//			switch (identifier) {
//			case EHINotificationActionLocationInfo:
//				if let name = localNotification.userInfo?[EHINotificationRentalLocationNameKey],
//                    let latitude = localNotification.userInfo?[EHINotificationRentalLocationLatitudeKey],
//                    let longitude = localNotification.userInfo?[EHINotificationRentalLocationLongitudeKey],
//                    let phoneNumber = localNotification.userInfo?[EHINotificationLocationPhoneKey] {
//                        let location = EHIWKLocation([:])
//                        location?.name.set(name)
//                        location?.latitude.set(latitude.doubleValue)
//                        location?.longitude.set(longitude.doubleValue)
//                        location?.phoneNumber.set(phoneNumber)
//
//                        self.presentControllerWithName(EHIWKLocationDetailsInterfaceController.storyboardIdentifier, context: location)
//                }
//
//			case EHINotificationActionTripInfo:
//                // interface state will show this info anyway
//				break;
//            default:
//                break;
//			}
//		}
//	}
}
