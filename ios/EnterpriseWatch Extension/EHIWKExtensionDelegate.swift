//
//  EHIWKExtensionDelegate.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit

class EHIWKExtensionDelegate: NSObject, WKExtensionDelegate {

    var interfaceState: EHIWKInterfaceState = .Initial
    
	override init() {
        super.init()
        // attempt to establish a connection to a paired phone
        EHIWKWatchConnectivityManager.prepareToLaunch()
	}
	
	func applicationDidBecomeActive() {
		EHIWKLog("DID BECOME ACTIVE")
//		EHILocalization.setStringBehavior(.DisplayRawKeys)
		
		NSNotificationCenter.defaultCenter().addObserverForName(EHIWKContextRefreshDataNotification, object: nil, queue: NSOperationQueue.mainQueue()) {
			(notif: NSNotification) -> Void in
			self.refreshInterfaceState()
		}
		
        refreshInterfaceState()
	}
    
    func refreshInterfaceState() {
        // Attempt to determine what interface we should show
        EHIWKWatchConnectivityManager.sharedInstance.clearRentalAndRequestUserRentalsWithHandler { (rental, error) -> Void in
            if let rental = rental,
                let isCurrent = rental.isCurrent.get() {
                    if isCurrent {
                        self.reloadInterfaceWithContext(.CurrentReservation)
                    }
                    else {
                        self.reloadInterfaceWithContext(.UpcomingReservation)
                    }
            }
            else if error != nil {
				dispatch_after(dispatch_time(DISPATCH_TIME_NOW, Int64(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), { () -> Void in
					self.refreshInterfaceState()
				})
                self.reloadInterfaceWithContext(.Initial)
            }
            else {
                self.reloadInterfaceWithContext(.NoReservation)
            }
        }
    }
	
	func applicationWillResignActive() {
        EHIWKLog("WILL RESIGN ACTIVE")
	}
    
    // MARK:- Interface loading
    
    func reloadInterfaceWithContext(context: EHIWKInterfaceState) {
        guard (self.interfaceState != context) else {
			NSNotificationCenter.defaultCenter().postNotificationName(EHIWKInterfaceRefreshDataNotification, object: nil)
            return
        }
        
        self.interfaceState = context
        
        let interfaceIds: [String]
        switch(context) {
        case .CurrentReservation:
            interfaceIds = [
                EHIWKCurrentRentalInterfaceController.storyboardIdentifier,
                EHIWKCustomerSupportInterfaceController.storyboardIdentifier]
        case .UpcomingReservation:
            interfaceIds = [
                EHIWKUpcomingReservationInterfaceController.storyboardIdentifier,
                EHIWKCustomerSupportInterfaceController.storyboardIdentifier]
        case .NoReservation:
            interfaceIds = [EHIWKWelcomeInterfaceController.storyboardIdentifier]
        case .Initial:
            interfaceIds = [EHIWKLaunchInterfaceController.storyboardIdentifier]
        }
        
        if interfaceIds.count > 0 {
            WKInterfaceController.reloadRootControllersWithNames(interfaceIds, contexts: nil)
        }
		
		NSNotificationCenter.defaultCenter().postNotificationName(EHIWKInterfaceRefreshDataNotification, object: nil)
    }
}
