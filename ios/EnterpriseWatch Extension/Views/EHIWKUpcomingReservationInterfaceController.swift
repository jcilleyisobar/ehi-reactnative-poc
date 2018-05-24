//
//  EHIWKUpcomingReservationInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKUpcomingReservationInterfaceController: EHIWKInterfaceController {

	static let storyboardIdentifier = "EHIWKUpcomingReservationInterfaceController"
	
	@IBOutlet var upcomingRentalTitle: WKInterfaceLabel!
	@IBOutlet var greetingLabel: WKInterfaceLabel!
	@IBOutlet var pickUpLabel: WKInterfaceLabel!
	@IBOutlet var dateLabel: WKInterfaceLabel!
	@IBOutlet var dateInfoLabel: WKInterfaceLabel!
	@IBOutlet var locationLabel: WKInterfaceLabel!
	@IBOutlet var locationInfoLabel: WKInterfaceLabel!
	@IBOutlet var confirmationNumberLabel: WKInterfaceLabel!
	@IBOutlet var confirmationNumberInfoLabel: WKInterfaceLabel!
	
    var pickupLocation: EHIWKLocation?

    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Localize our titles
		self.upcomingRentalTitle.setText(EHIWKLocalizedString("watch_upcoming_rental_title", fallback: "UPCOMING RENTAL"))
		self.greetingLabel.setText(EHIWKLocalizedString("watch_upcoming_see_you_at_pickup_label", fallback: "See you at pick-up!"))
		self.pickUpLabel.setText(EHIWKLocalizedString("watch_upcoming_pickup_info_label", fallback: "Pick-up Info"))
		self.dateLabel.setText(EHIWKLocalizedString("watch_upcoming_date_time_label", fallback: "Date & Time:"))
		self.locationLabel.setText(EHIWKLocalizedString("watch_upcoming_location_label", fallback: "Location:"))
		self.confirmationNumberLabel.setText(EHIWKLocalizedString("watch_upcoming_confirmation_number_label", fallback: "Confirmation Number:"))
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        
        EHIWKWatchConnectivityManager.sharedInstance.requestUserRentalsWithHandler { (rental, error) -> Void in
            if let rental = rental {
                self.dateInfoLabel.setText(rental.displayPickupDate())
                self.locationInfoLabel.setText(rental.pickupLocationName())
                self.confirmationNumberInfoLabel.setText(rental.confirmationNumber.get())
                
                self.pickupLocation = rental.pickupLocation.get()
            }
        }

    }
    
    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

    
    // MARK:- Menu Items
    override func menuItems() -> [EHIWKMenuItem]? {
		let callItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_call_us", fallback: "Call us"), selector: Selector("callSupport"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_02_call"))
		let branchItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_branch_details", fallback: "Branch details"), selector: Selector("branchDetails"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_01_navigation_b"))
        let terminalItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_terminal_directions", fallback: "Terminal direction"), selector: Selector("terminalDirection"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_03_flight"))

        return [callItem, branchItem, terminalItem]
    }
    
    
    // MARK:- Actions
    func callSupport() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.CONTACT_US)
        EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("CustomerSupport", screen: "WA:UpcomingRental")
    }
    
    func branchDetails() {
        EHIWKWatchConnectivityManager.sharedInstance.screenForAnalytics = "WA:UpcomingRental"
        self.presentControllerWithName(EHIWKLocationDetailsInterfaceController.storyboardIdentifier, context: pickupLocation)
    }
    
    func terminalDirection() {
        self.presentControllerWithName(EHIWKLocationWayfindingsViewController.storyboardIdentifier, context: pickupLocation?.wayfindings.get())
    }
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WA:Dashboard"
    }

    override func state() -> String? {
        return "UpcomingRental"
    }
}
