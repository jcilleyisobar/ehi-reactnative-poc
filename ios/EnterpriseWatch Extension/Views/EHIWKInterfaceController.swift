//
//  EHIWKInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/9/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation

@objc class EHIWKInterfaceController: WKInterfaceController {
	
	@IBOutlet var activityIndicator: WKInterfaceImage?
	private var notificationObserver: AnyObject?
	
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)

		self.addMenuItems()
    }
    
    override func willActivate() {
        super.willActivate()
        
        self.updateAnalyticsForScreen()
    }
	
    override func didAppear() {
        // This method is called when watch view controller is visible to user
        super.didAppear()
        
		self.notificationObserver = NSNotificationCenter.defaultCenter()
			.addObserverForName(EHIWKInterfaceRefreshDataNotification,
				object: nil,
				queue: NSOperationQueue.mainQueue()) { (notif: NSNotification) -> Void in
					self.reloadNotification(notif)
		};
		self.reloadData()
    }
    
    func updateAnalyticsForScreen() {
        // update screen for analytics
        if self.screen() != nil {
            EHIWKWatchConnectivityManager.sharedInstance.screenForAnalytics = self.screen()
        }
        
        if self.state() != nil {
            EHIWKWatchConnectivityManager.sharedInstance.trackAnalyticsForState(self.state())
        }
    }
	
	override func willDisappear() {
		super.willDisappear()
		
		if let notificationObserver = self.notificationObserver {
			NSNotificationCenter.defaultCenter().removeObserver(notificationObserver)
			self.notificationObserver = nil
		}
	}
	
    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
	
	// MARK:- Notification methods
	func reloadData() {
		// subclasses should override to respond to reload notifications
	}
	
	final private func reloadNotification(notif: NSNotification) {
		// perform any common actions on notification before letting subclasses respond
		// ...
		EHIWKLog("RELOAD NOTIFICATION")
		self.reloadData()
	}
	
    // MARK:- Convenience
    func getExtensionDelegate() -> EHIWKExtensionDelegate? {
        if let delegate = WKExtension.sharedExtension().delegate as? EHIWKExtensionDelegate {
            return delegate
        }
        return nil
    }
	
	func noReservationInterface()
	{
		getExtensionDelegate()?.reloadInterfaceWithContext(.NoReservation)
	}
	
	func upcomingReservationInterface()
	{
		getExtensionDelegate()?.reloadInterfaceWithContext(.UpcomingReservation)
	}
	
	func currentReservationInterface()
	{
		getExtensionDelegate()?.reloadInterfaceWithContext(.CurrentReservation)
	}
	
	// MARK:- LocalNotification Handlers
	override func handleActionWithIdentifier(identifier: String?, forLocalNotification localNotification: UILocalNotification) {
		
	}
	
	// MARK:- Activity Indicator methods (no-op if animatable image not installed)
	final func startProgressAndHideView(viewToHide: WKInterfaceObject) {
		if let progress = self.activityIndicator {
			viewToHide.setHidden(true)
			progress.setHidden(false)
		}
	}
		
	final func stopProgressAndShowView(viewToShow: WKInterfaceObject) {
		if let progress = self.activityIndicator {
			progress.setHidden(true)
			viewToShow.setHidden(false)
		}
	}
	
	// MARK:- Menu builder methods
	func menuItems() -> [EHIWKMenuItem]? { return nil }

	enum EHIWKImageOrIcon {
		case Image(String)
		case Icon(WKMenuItemIcon)
	}
	struct EHIWKMenuItem {
		let title: String
		let selector: Selector
		let imageOrIcon: EHIWKImageOrIcon
		
		init(title: String, selector: Selector, imageOrIcon: EHIWKImageOrIcon) {
			self.title = title
			self.selector = selector
			self.imageOrIcon = imageOrIcon
		}
	}
	final func addMenuItems() {
		if let menuItems = self.menuItems() {
			for item in menuItems {
				switch(item.imageOrIcon) {
				case .Image(let imageName):
					self.addMenuItemWithImageNamed(imageName, title: item.title, action: item.selector)
				case .Icon(let icon):
					self.addMenuItemWithItemIcon(icon, title: item.title, action: item.selector)
				}
			}
		}
		else
		{
			#if DEBUG
			// default menu items if subclass does not return either something
			self.addMenuItemWithItemIcon(.Accept, title: "No Res", action: Selector.init("noReservationInterface"))
			self.addMenuItemWithItemIcon(.Info, title: "Current", action: Selector.init("currentReservationInterface"))
			self.addMenuItemWithItemIcon(.More, title: "Upcoming", action: Selector.init("upcomingReservationInterface"))
			#endif
		}
	}
    
    func screen() -> String? {
        return nil
    }
    
    func state() -> String? {
        return nil
    }
}
