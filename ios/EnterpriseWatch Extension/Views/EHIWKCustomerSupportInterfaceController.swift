//
//  EHIWKCustomerSupportInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/17/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation

class EHIWKCustomerSupportInterfaceController: EHIWKInterfaceController {

	static let storyboardIdentifier = "EHIWKCustomerSupportInterfaceController"
	
    @IBOutlet var contentGroup: WKInterfaceGroup!

	@IBOutlet var callSupportButton: WKInterfaceButton!
	@IBOutlet var callRoadsideButton: WKInterfaceButton!
	@IBOutlet var callEPlusButton: WKInterfaceButton!
	
	@IBOutlet var customerSupportTitleLabel: WKInterfaceLabel!
	@IBOutlet var callSupportButtonLabel: WKInterfaceLabel!
	@IBOutlet var callRoadsideButtonLabel: WKInterfaceLabel!
	@IBOutlet var callEPlusButtonLabel: WKInterfaceLabel!
		
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        self.startProgressAndHideView(self.contentGroup)
		
		// Localize our titles
		self.customerSupportTitleLabel.setText(EHIWKLocalizedString("watch_customer_support_title", fallback: "CUSTOMER SUPPORT"))
		self.callSupportButtonLabel.setText(EHIWKLocalizedString("watch_button_call_support", fallback: "Call Customer Support"))
		self.callRoadsideButtonLabel.setText(EHIWKLocalizedString("watch_button_call_roadside", fallback: "Roadside Assistance"))
		self.callEPlusButtonLabel.setText(EHIWKLocalizedString("watch_button_call_eplus", fallback: "Call ePlus Support"))
		
        // Until we get some number back for these, they are hidden
		self.callSupportButton.setHidden(true)
		self.callRoadsideButton.setHidden(true)
		self.callEPlusButton.setHidden(true)
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
	
	// MARK:- Reload data
	override func reloadData() {
        EHIWKCallSupportManager.sharedInstance.fetchConfigDataIfNecessary { (phoneMap) -> Void in
            for (type, _) in phoneMap {
                switch(type) {
                case .CONTACT_US:
                    self.callSupportButton.setHidden(false)
                    break;
                case .DISABILITES:
                    break;
                case .DNR:
                    break;
                case .EPLUS:
                    self.callEPlusButton.setHidden(false)
                    break;
                case .ROADSIDE_ASSISTANCE:
                    self.callRoadsideButton.setHidden(false);
                    break;
                }
            }

            self.stopProgressAndShowView(self.contentGroup)
        }
	}
	
	// MARK:- Actions
	@IBAction func didTouchCallSupport() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.CONTACT_US)
	}
	
	@IBAction func didTouchCallRoadside() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.ROADSIDE_ASSISTANCE)
	}

	@IBAction func didTouchCallEPlus() {
        EHIWKCallSupportManager.sharedInstance.callPhoneNumber(EHIWKPhoneType.EPLUS)
	}
    
    // MARK:- Menu Items
    override func menuItems() -> [EHIWKInterfaceController.EHIWKMenuItem]? {
        return []
    }
}
