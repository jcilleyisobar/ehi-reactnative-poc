//
//  EHIWKUpcomingReservationNotificationController.swift
//  Enterprise
//
//  Created by George Stuart on 10/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKUpcomingReservationNotificationController: WKUserNotificationInterfaceController {

	@IBOutlet var confirmationNumberLabel: WKInterfaceLabel!
	@IBOutlet var rentalLocationLabel: WKInterfaceLabel!
	@IBOutlet var rentalDateLabel: WKInterfaceLabel!
	
	static let dateFormatter = NSDateFormatter()
	
    override init() {
        // Initialize variables here.
        super.init()
        
        // Configure interface objects here.
		EHIWKUpcomingReservationNotificationController.dateFormatter.dateStyle = .MediumStyle
		EHIWKUpcomingReservationNotificationController.dateFormatter.timeStyle = .ShortStyle
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
		
//    override func didReceiveLocalNotification(localNotification: UILocalNotification, withCompletion completionHandler: ((WKUserNotificationInterfaceType) -> Void)) {
//        // This method is called when a local notification needs to be presented.
//        // Implement it if you use a dynamic notification interface.
//        // Populate your dynamic notification interface as quickly as possible.
//		if let rentalConfirmation = localNotification.userInfo?[EHINotificationRentalConfirmationNumberKey] as? String,
//			let rentalDate = localNotification.userInfo?[EHINotificationRentalPickupDateKey] as? NSDate,
//			let rentalLocation = localNotification.userInfo?[EHINotificationRentalLocationNameKey] as? String {
//				self.confirmationNumberLabel.setText(rentalConfirmation)
//				self.rentalLocationLabel.setText(rentalLocation)
//				self.rentalDateLabel.setText(EHIWKUpcomingReservationNotificationController.dateFormatter.stringFromDate(rentalDate))
//				//
//				// After populating your dynamic notification interface call the completion block.
//				completionHandler(.Custom)
//		}
//		else {
//			// some data failed to be retrieved from the notification payload.
//			// show the standard notification
//			completionHandler(.Default)
//		}
//    }
}
