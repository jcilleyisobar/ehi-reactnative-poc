//
//  EHIWKWayfindingTableRowController.swift
//  Enterprise
//
//  Created by cgross on 12/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import WatchKit

class EHIWKWayfindingTableRowController: NSObject {
    @IBOutlet var iconView: WKInterfaceImage?
    @IBOutlet var textLabel: WKInterfaceLabel?
    
    func styleWithWayfinding(wayfinding: EHIWKLocationWayfinding)
    {
        self.textLabel?.setText(wayfinding.text.get())
        self.loadImage(NSURL(string: wayfinding.iconUrl.get()!))
    }
    
    func loadImage(imageUrl: NSURL?) {
        guard let imageUrl = imageUrl else { return }
        
        NSURLSession.sharedSession().dataTaskWithURL(imageUrl, completionHandler: {(data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            if let image = UIImage(data: data!) {
                dispatch_async(dispatch_get_main_queue()) {
                    self.iconView?.setImage(image)
                }
            }
        }).resume()
    }
}
