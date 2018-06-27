//
//  EHIWKLocationWayfindingsViewController.swift
//  Enterprise
//
//  Created by cgross on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit
import Foundation


class EHIWKLocationWayfindingsViewController: EHIWKInterfaceController {
    
    static let storyboardIdentifier = "EHIWKLocationWayfindingsViewController"

    @IBOutlet var titleLabel: WKInterfaceLabel!
    @IBOutlet var wayfindingsTable: WKInterfaceTable!

    enum TableRowType: String {
        case Wayfinding = "wayfindingTableRow"
    }
    
    var wayfindings: [EHIWKLocationWayfinding] = []
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        self.titleLabel.setText(EHIWKLocalizedString("watch_terminal_directions_title", fallback: "TERMINAL DIRECTIONS"))

        self.wayfindings = context as! [EHIWKLocationWayfinding]
    }
    
    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        configureWayfindingsTable()
    }
    
    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
    
    private func configureWayfindingsTable()
    {
        self.wayfindingsTable?.setNumberOfRows(self.wayfindings.count, withRowType: TableRowType.Wayfinding.rawValue)
        
        for (var index = 0; index < self.wayfindings.count; index++) {
            let row = self.wayfindingsTable?.rowControllerAtIndex(index) as? EHIWKWayfindingTableRowController
            row?.styleWithWayfinding(self.wayfindings[index])
        }
    }
    
    // MARK:- Analytics
    override func screen() -> String {
        return "WA:UpcomingRental"
    }
    
    override func state() -> String? {
        return "TerminalDirections"
    }
}