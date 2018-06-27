//
//  EHIWKBranchLocationTableRowController.swift
//  Enterprise
//
//  Created by George Stuart on 10/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit

class EHIWKBranchLocationTableRowController: NSObject {
	@IBOutlet var containerGroup: WKInterfaceGroup?
	
	@IBOutlet var branchNameLabel: WKInterfaceLabel?
	@IBOutlet var branchAddressLabel: WKInterfaceLabel?
	
	@IBOutlet var branchDistanceLabel: WKInterfaceLabel?
	@IBOutlet var branchDistanceUnitLabel: WKInterfaceLabel?

	static let numberFormatter = NSNumberFormatter()

	override class func initialize() {
		super.initialize()
		self.numberFormatter.numberStyle = .DecimalStyle
		self.numberFormatter.maximumFractionDigits = 1
	}
	
	func styleWithBranch(branchInfo: EHIWKLocation)
	{
        self.branchNameLabel?.setText(branchInfo.name.get())
		self.branchAddressLabel?.setText(branchInfo.addressLines.get()?[0])
		
		var formattedDistance: String = "--"
		if
			let distance = branchInfo.distance.get(),
			let formatted = EHIWKBranchLocationTableRowController.numberFormatter.stringFromNumber(distance) {
				formattedDistance = formatted
		}

        self.branchDistanceLabel?.setText(formattedDistance)
		self.branchDistanceUnitLabel?.setText(branchInfo.distanceUnit)
	}
}
