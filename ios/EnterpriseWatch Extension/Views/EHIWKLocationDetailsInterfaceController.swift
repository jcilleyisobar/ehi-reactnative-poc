//
//  EHIWKLocationDetailsInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKLocationDetailsInterfaceController: EHIWKInterfaceController {

    static let storyboardIdentifier = "EHIWKLocationDetailsInterfaceController"
    
	@IBOutlet var nameLabel: WKInterfaceLabel!
	@IBOutlet var locationMap: WKInterfaceMap!
	@IBOutlet var callButtonLabel: WKInterfaceLabel!
	private var location: EHIWKLocation?
	
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
		
		self.callButtonLabel.setText(EHIWKLocalizedString("watch_button_call_us", fallback: "Call Branch"))
		
		if let location = context as? EHIWKLocation {
			self.nameLabel.setText("\(location.name)")
			
			if let latitude = location.latitude.get(),
				let longitude = location.longitude.get() {
					self.location = location
					
					let coords = CLLocationCoordinate2DMake(latitude, longitude)
					let region = MKCoordinateRegionMake(coords, MKCoordinateSpanMake(0.05, 0.05))
					
					self.locationMap.setRegion(region)
					self.locationMap.addAnnotation(coords, withImageNamed: location.annotationImageName, centerOffset: CGPointMake(0, 0))
			}
			else {
				self.locationMap.setHidden(true)
			}
            
		}
	}

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
	
	@IBAction func didTouchCallButton() {
		if let location = self.location,
			let phoneURL = NSURL(string: "tel:\(location.phoneNumber)") {
			WKExtension.sharedExtension().openSystemURL(phoneURL)
            EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState("CallBranch")
		}
	}
    
    // MARK:- Menu Items
    override func menuItems() -> [EHIWKInterfaceController.EHIWKMenuItem]? {
        return []
    }
    
    // MARK:- Analytics
    override func state() -> String? {
        return "BranchDetail"
    }
}
