//
//  EHIWKNoReservationLandingInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKWelcomeInterfaceController: EHIWKInterfaceController {

	static let storyboardIdentifier = "EHIWKWelcomeInterfaceController"
	
	@IBOutlet var welcomeLabel: WKInterfaceLabel!
	@IBOutlet var haveReservationLabel: WKInterfaceLabel!
	@IBOutlet var signInLabel: WKInterfaceLabel!
	@IBOutlet var findNearbyButtonLabel: WKInterfaceLabel!

    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
		
		self.welcomeLabel.setText(EHIWKLocalizedString("watch_welcome_title", fallback: "Welcome!"))
		self.haveReservationLabel.setText(EHIWKLocalizedString("watch_welcome_have_reservation_label", fallback: "Have a reservation?"))
		self.signInLabel.setText(EHIWKLocalizedString("watch_welcome_sign_in_label", fallback: "Sign in on iPhone to see your rentals"))
		self.findNearbyButtonLabel.setText(EHIWKLocalizedString("watch_button_find_nearby", fallback: "Find Nearby"))
	}
    
    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
    
    // MARK:- Menu Items
    override func menuItems() -> [EHIWKInterfaceController.EHIWKMenuItem]? {
        return []
    }
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WA:Dashboard"
    }
    
    override func state() -> String? {
        return "NoRent"
    }
}
