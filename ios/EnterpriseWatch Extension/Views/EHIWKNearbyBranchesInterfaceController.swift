//
//  EHIWKNearbyBranchesInterfaceController.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKNearbyBranchesInterfaceController: EHIWKInterfaceController, CLLocationManagerDelegate {

	static let storyboardIdentifier = "EHIWKNearbyBranchesInterfaceController"
	
	enum TableRowType: String {
		case Branch = "branchTableRow"
	}
	
	var locationManager: CLLocationManager!
	var nearbyBranches: [EHIWKLocation] = []
	@IBOutlet var nearbyBranchesTable: WKInterfaceTable!
	
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
		self.reloadLocations()
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
		self.configureNearbyBranchesTable()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
	
	// MARK:- Menu Items
	override func menuItems() -> [EHIWKMenuItem]? {
		let reloadItem = EHIWKMenuItem(title: EHIWKLocalizedString("watch_menu_item_refresh", fallback: "Refresh"), selector: Selector("onRefresh"), imageOrIcon: EHIWKImageOrIcon.Icon(WKMenuItemIcon.Repeat))
		return [reloadItem]
	}
    
    func onRefresh() {
        self.reloadLocations()
    }
	
	// MARK:- WKInterfaceTable
	override func table(table: WKInterfaceTable, didSelectRowAtIndex rowIndex: Int) {
		self.presentControllerWithName(EHIWKLocationDetailsInterfaceController.storyboardIdentifier, context: self.nearbyBranches[rowIndex])
	}
	
	// MARK:- CLLocationManagerDelegate
	func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
		if let location = locations.first {
			// call the spatial search
			EHIWKNetworkManager.sharedInstance.requestNearbyLocations(location) { (responseObject: EHIWKLocationsResponse) -> Void in
				if let locations = responseObject.locations.get() {
					#if DEBUG
					print("response received and processed")
					#endif
					dispatch_async(dispatch_get_main_queue(), { () -> Void in
						self.nearbyBranches.removeAll()
						self.nearbyBranches.appendContentsOf(locations)
						self.configureNearbyBranchesTable()
						self.stopProgressAndShowView(self.nearbyBranchesTable)
					})
				}				
			}
			
			self.locationManager.stopUpdatingLocation()
		}
	}
	
	func locationManager(manager: CLLocationManager, didFailWithError error: NSError) {
		let defaultButton = WKAlertAction(title: EHIWKLocalizedString("watch_alert_action_ok", fallback: "OK"), style: WKAlertActionStyle.Default) {
			// no op
		}
		
		self.presentAlertControllerWithTitle(EHIWKLocalizedString("watch_alert_title_no_location", fallback: "Unable to determine location"), message: nil, preferredStyle: WKAlertControllerStyle.Alert, actions: [defaultButton])
	}
	
	// MARK:- Private Methods
	private func reloadLocations() {
		self.startProgressAndHideView(self.nearbyBranchesTable)
		
		self.locationManager = CLLocationManager()
		self.locationManager.delegate = self;
		self.locationManager.requestWhenInUseAuthorization()
		
		self.locationManager.requestLocation()
	}
	
	private func configureNearbyBranchesTable()
	{
		self.nearbyBranchesTable?.setNumberOfRows(self.nearbyBranches.count, withRowType: TableRowType.Branch.rawValue)
		
		for (var index = 0; index < self.nearbyBranches.count; index++) {
			let row = self.nearbyBranchesTable?.rowControllerAtIndex(index) as? EHIWKBranchLocationTableRowController
			row?.styleWithBranch(self.nearbyBranches[index])
		}
		
		if self.nearbyBranches.count > 0 {
			self.stopProgressAndShowView(self.nearbyBranchesTable)
		}
	}
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WA:NoRent"
    }
    
    override func state() -> String? {
        return "NearbyBranches"
    }
}
