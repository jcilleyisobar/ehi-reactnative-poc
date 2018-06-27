//
//  EHIWKCurrentRentalInterfaceController.swift
//  Enterprise
//
//  Created by cgross on 11/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation

class EHIWKCurrentRentalInterfaceController: EHIWKInterfaceController {

    static let storyboardIdentifier = "EHIWKCurrentRentalInterfaceController"
	
	@IBOutlet var currentRentalTitle: WKInterfaceLabel!
	@IBOutlet var returnLabel: WKInterfaceLabel!
    @IBOutlet var vehicleInfoLabel: WKInterfaceLabel!
    @IBOutlet var licensePlaceLabel: WKInterfaceLabel!
    @IBOutlet var licensePlateInfoLabel: WKInterfaceLabel!
    @IBOutlet var dateAndTimeLabel: WKInterfaceLabel!
    @IBOutlet var dateAndTimeInfoLabel: WKInterfaceLabel!
    @IBOutlet var locationLabel: WKInterfaceLabel!
    @IBOutlet var locationInfoLabel: WKInterfaceLabel!
    
    var returnLocation: EHIWKLocation?

    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Localize titles
		self.currentRentalTitle.setText(EHIWKLocalizedString("watch_current_rental_title", fallback: "CURRENT RENTAL"))
		self.returnLabel.setText(EHIWKLocalizedString("watch_current_return_label", fallback: "Return Info"))
		self.dateAndTimeLabel.setText(EHIWKLocalizedString("watch_current_return_date_time_label", fallback: "Date & Time:"))
		self.locationLabel.setText(EHIWKLocalizedString("watch_current_return_location_label", fallback: "Location:"))
		self.licensePlaceLabel.setText(EHIWKLocalizedString("watch_current_license_plate_label", fallback: "License Plate Number:"))
    }
    
    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        
        EHIWKWatchConnectivityManager.sharedInstance.requestUserRentalsWithHandler { (rental, error) -> Void in
            if let rental = rental {
                self.vehicleInfoLabel.setText(rental.makeModel.get())
                self.licensePlateInfoLabel.setText(rental.licensePlate.get())
                self.locationInfoLabel.setText(rental.returnLocationName())
                self.dateAndTimeInfoLabel.setText(rental.displayReturnDate())
                
                self.returnLocation = rental.returnLocation.get()
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
        let roadsideItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_roadside_assistance", fallback: "Roadside Assistance"), selector: Selector("callRoadsideAssistance"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_04_assistance"))
        let branchItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_branch_details", fallback: "Branch details"), selector: Selector("branchDetails"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_01_navigation_b"))
        let extendItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_extend_rental", fallback: "Extend Rental"), selector: Selector("callExtend"), imageOrIcon: EHIWKImageOrIcon.Image("menu_icon_02_call"))
        
        return [callItem, roadsideItem, branchItem, extendItem]
    }
    
	
    // MARK:- Actions
    func callSupport() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.CONTACT_US)
        EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("CallUs", screen: "WA:CurrentRental")
    }
    
    func callRoadsideAssistance() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.ROADSIDE_ASSISTANCE)
        EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("RoadsideAssisstance", screen: "WA:CurrentRental")
    }
    
    func branchDetails() {
        EHIWKWatchConnectivityManager.sharedInstance.screenForAnalytics = "WA:CurrentRental"
        self.presentControllerWithName(EHIWKLocationDetailsInterfaceController.storyboardIdentifier, context: returnLocation)
    }
    
    func callExtend() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.CONTACT_US)
        EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("ExtendRental", screen: "WA:CurrentRental")
    }
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WA:Dashboard"
    }
    
    override func state() -> String? {
        return "CurrentRental"
    }

}