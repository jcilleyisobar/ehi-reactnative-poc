//
//  EHIWKRentalResponse.swift
//  Enterprise
//
//  Created by Michael Place on 10/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKRentalResponse: JSON {
    let pickupDate         = JSONField<String>("pickupDate")
    let returnDate         = JSONField<String>("returnDate")
    let confirmationNumber = JSONField<String>("confirmationNumber")
    let ticketNumber       = JSONField<String>("ticketNumber")
    let invoiceNumber      = JSONField<String>("invoiceNumber")
    let makeModel          = JSONField<String>("makeModel")
    let licensePlate       = JSONField<String>("licensePlate")
	let isCurrent          = JSONField<Bool>("isCurrent")

    let pickupLocation     = JSONField<EHIWKLocation>("pickupLocation")
    let returnLocation     = JSONField<EHIWKLocation>("returnLocation")
	
    
    // MARK:- Computed
    func pickupLocationName() -> String {
        return (self.pickupLocation.get()?.name.get())!
    }

    func returnLocationName() -> String {
        return (self.returnLocation.get()?.name.get())!
    }
    
    func displayReturnDate() -> String {
        return self.displayDate(self.returnDate.get())
    }
    
    func displayPickupDate() -> String {
        return self.displayDate(self.pickupDate.get())
    }
    
    private func displayDate(dateString: String?) -> String {
        var result :String = ""
        
        // attempt to create a date from our string
        if let date :NSDate = (dateString?.ehi_dateTime()) {
            
            // generate formatted date and time strings
            let dateString :String? = date.ehi_localizedMediumDateString()
            let timeString :String? = date.ehi_localizedTimeString()
            
            // if successful output the formatted result
            if let dateString = dateString, timeString = timeString {
                result = "\(dateString),\n" + "\(timeString)"
            }
        }
        
        return result
    }
    
}
