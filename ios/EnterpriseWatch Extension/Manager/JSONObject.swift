//
//  JSONObject.swift
//
//  Created by George Stuart on 10/15/15.
//

import Foundation

protocol JSONObject: JSONType {
	init()
	init?(_ dictionary: Dictionary<String, AnyObject>)
	func encode() -> Dictionary<String, AnyObject>?
	func decode(dictionary: Dictionary<String, AnyObject>)
}

extension JSONObject {
	
	func decode(dictionary: Dictionary<String, AnyObject>) {
		var mirror: Mirror? = Mirror(reflecting: self)
		repeat {
			if let realMirror = mirror {
				for (_, propertyValue) in realMirror.children {
					if let property = propertyValue as? JSONMappable {
						let value = dictionary[property.jsonKey]
						property.setDecoded(value)
					}
				}
				mirror = realMirror.superclassMirror()
			}
		} while (mirror != nil)
	}
	
	func encode() -> Dictionary<String, AnyObject>? {
		var dictionary: Dictionary<String, AnyObject> = [:]
		var mirror: Mirror? = Mirror(reflecting: self)
		repeat {
			if let realMirror = mirror {
				for (_, propertyValue) in realMirror.children {
					if let property = propertyValue as? JSONMappable {
						let encodedProperty = property.getEncoded()
						dictionary[property.jsonKey] = encodedProperty as? NSObject
					}
				}
				mirror = realMirror.superclassMirror()
			}
		} while (mirror != nil)
		
		if dictionary.isEmpty {
			return nil
		}
		return dictionary
	}
}

class JSON: NSObject, JSONObject {
	required override init() {}
	required init?(_ dictionary: Dictionary<String, AnyObject>) {
		super.init()
		self.decode(dictionary)
	}
	func toDictionary() -> [String: AnyObject]? {
		return self.encode()
	}
}