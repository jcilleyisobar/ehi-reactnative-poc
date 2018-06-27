//
//  EHIWKNetworkManager.swift
//  Enterprise
//
//  Created by George Stuart on 10/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

class EHIWKNetworkManager: NSObject {
    static let sharedInstance = EHIWKNetworkManager()
	
    let session = NSURLSession.sharedSession()
	
    private override init() {
        super.init()
    }
	
    func processRequest<T: JSONObject>(request: NSURLRequest, completion: (responseObject: T) -> Void) {
        guard (request.URL != nil)
            else {
                return
        }
		
		let task = self.session.dataTaskWithRequest(request) { (responseData, urlResponse, error) -> Void in
			if let validData = responseData {
				do{
					if let jsonObject = try NSJSONSerialization.JSONObjectWithData(validData, options: NSJSONReadingOptions.MutableContainers) as? Dictionary<String,AnyObject>,
                        let resultObject = T(jsonObject) {
							completion(responseObject: resultObject)
					}
				} catch let error {
					print(error)
				}
			}
        }
		
		task.resume()
    }
}