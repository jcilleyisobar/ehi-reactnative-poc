//
//  EHIWKGlanceController.swift
//  Enterprise
//
//  Created by Michael Place on 10/20/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKGlanceController: EHIWKInterfaceController {

    static let storyboardIdentifier = "EHIWKGlanceController"
    
    @IBOutlet var bodyGroup: WKInterfaceGroup!
    @IBOutlet var authGroup: WKInterfaceGroup!
    @IBOutlet var unauthGroup: WKInterfaceGroup!
    
    @IBOutlet var icon: WKInterfaceImage!

    // unauth outlets
    @IBOutlet var rentTodayLabel: WKInterfaceLabel!
    @IBOutlet var worldwideLocationsLabel: WKInterfaceLabel!
    
    // auth outlets
    @IBOutlet var titleLabel: WKInterfaceLabel!
    @IBOutlet var infoLabel: WKInterfaceLabel!
    @IBOutlet var typeTitleLabel: WKInterfaceLabel!
    @IBOutlet var dateValueLabel: WKInterfaceLabel!
    @IBOutlet var locationValueLabel: WKInterfaceLabel!

    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Configure interface objects here.
    }

    override func didAppear() {
        // This method is called when watch view controller is about to be visible to user
        super.didAppear()
        
        reloadData()
    }
    
    override func reloadData() {
        self.startProgressAndHideView(self.bodyGroup)
        
        self.icon.setImageNamed("eapp")
        
        EHIWKWatchConnectivityManager.sharedInstance.clearRentalAndRequestUserRentalsWithHandler { (rental, error) -> Void in
            if let rental = rental {
                // populate with a current rental
                if let isCurrent = rental.isCurrent.get() where isCurrent {
                    self.icon.setImageNamed("icon_return")

                    self.titleLabel.setText(EHIWKLocalizedString("watch_current_rental_title", fallback: "CURRENT RENTAL"))
                    self.infoLabel.setText(EHIWKLocalizedString("watch_return_info_label", fallback: "Return Info"))
                    self.typeTitleLabel.setText(EHIWKLocalizedString("watch_rental_return_label", fallback: "Rental return"))
                    self.dateValueLabel.setText(rental.displayReturnDate())
                    self.locationValueLabel.setText(rental.returnLocationName())
                    
                    EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("CurrentRental")
                }
                    // populate with an upcoming rental
                else {
                    self.icon.setImageNamed("icon_pickup")

					self.titleLabel.setText(EHIWKLocalizedString("watch_upcoming_rental_title", fallback: "UPCOMING RENTAL"))
                    self.infoLabel.setText(EHIWKLocalizedString("watch_pickup_info_label", fallback: "Pick-up Info"))
                    self.typeTitleLabel.setText(EHIWKLocalizedString("watch_rental_pickup_label", fallback: "Rental pick-up"))
                    self.dateValueLabel.setText(rental.displayPickupDate())
                    self.locationValueLabel.setText(rental.pickupLocationName())
                    
                    EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("UpcomingRental")
                }
                
                // show the authenticated group
                self.unauthGroup.setHidden(true)
                self.authGroup.setHidden(false)
            }
            else if error != nil {
                EHIWKLog("GLANCE RELOAD ERROR")
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, Int64(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), { () -> Void in
                    self.reloadData()
                })
                return
            }
                // no rentals to show
            else {
                self.titleLabel.setText("Enterprise".uppercaseString)
                
                self.rentTodayLabel.setText(EHIWKLocalizedString("watch_glance_rent_today_label", fallback: "Rent an Enterprise vehicle today"))
                self.worldwideLocationsLabel.setText(EHIWKLocalizedString("watch_glance_worldwide_locations", fallback: "More than 7,200 locations worldwide"))
                
                // show the unauthenticated group
                self.authGroup.setHidden(true)
                self.unauthGroup.setHidden(false)
                
                EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("NoRent")
            }
            
            self.stopProgressAndShowView(self.bodyGroup)
        }
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WG:Dashboard"
    }
}
