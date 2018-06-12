//
//  EHIWKWatchConnectivityManager.swift
//  Enterprise
//
//  Created by Michael Place on 10/18/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation
import WatchKit
import WatchConnectivity

class EHIWKWatchConnectivityManager: NSObject, WCSessionDelegate {
    static let sharedInstance = EHIWKWatchConnectivityManager()
    private var session: WCSession!
    private var rental: EHIWKRentalResponse?
    var screenForAnalytics: String?
    
    override private init() {
        self.session = WCSession.defaultSession()
        super.init()
    }
    
    static func prepareToLaunch() {
        if(WCSession.isSupported()) {
            // activation
            let session = self.sharedInstance.session
            session.delegate = self.sharedInstance
            session.activateSession()
        }
    }
    
    func clearRentalAndRequestUserRentalsWithHandler(handler: (rental: EHIWKRentalResponse?, error: NSError?) -> Void) {
//        self.rental = nil
        requestUserRentalsWithHandler(handler)
    }
    
    func requestUserRentalsWithHandler(handler: (rental: EHIWKRentalResponse?, error: NSError?) -> Void) {
		
		let context = self.session.receivedApplicationContext
		if let response = context[EHIWatchConnectivityRental] as? [String : AnyObject] {
			let userRental: EHIWKRentalResponse? = EHIWKRentalResponse(response)
			// cache for later
			self.rental = userRental;
			// call the handler
			handler(rental: userRental, error: nil)
		}
		else {
			handler(rental: nil, error: nil)
		}

        // if we have a rental, return immediately
//        if let rental = self.rental {
//            handler(rental: rental, error: nil)
//        }
//        // otherwise fetch
//        else {
//            self.session.sendMessage([:],
//                replyHandler: { replyMessage -> Void in
//                    if let response = replyMessage[EHIConnectivityResponse] as? [String : AnyObject] {
//                        let userRental: EHIWKRentalResponse? = EHIWKRentalResponse(response)
//                        // cache for later
//                        self.rental = userRental;
//                        // call the handler
//                        handler(rental: userRental, error: nil)
//                    }
//                    else {
//                        handler(rental: nil, error: nil)
//                    }
//                },
//                errorHandler: { error -> Void in
//                    handler(rental: nil, error: error)
//            })
//        }
    }
    
    func trackAnalyticsForState(state: String?, screen: String) {
        self.screenForAnalytics = screen
        self.trackAnalyticsForState(state)
    }
    
    func trackAnalyticsForState(state: String?) {
        guard let screen = self.screenForAnalytics, state = state else { return }
        
        print("Watch: Analytics: \(screen), \(state)")
        
        let context = ["screen" : screen, "state" : state];
        self.session.sendMessage(["analytics" : context],
            replyHandler: nil,
            errorHandler: nil)
    }
    
    // MARK:- WCSessionDelegate
	
    func session(session: WCSession, didReceiveMessage message: [String : AnyObject]) {
        print("did receieve message")
    }
    
    func session(session: WCSession, didReceiveMessage message: [String : AnyObject], replyHandler: ([String : AnyObject]) -> Void) {
        let response = ["Greeting" : "Read you loud and clear"]
        replyHandler(response)
    }
    
    func session(session: WCSession, didReceiveApplicationContext applicationContext: [String : AnyObject]) {
        print("did receive application context")
		NSNotificationCenter.defaultCenter().postNotificationName(EHIWKContextRefreshDataNotification, object: nil)
    }
}
