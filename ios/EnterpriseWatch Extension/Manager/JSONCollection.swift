//
//  JSONCollection.swift
//
//  Created by George Stuart on 11/16/15.
//

import Foundation

protocol JSONCollection: JSONType {
	func encode<T>() -> Array<T>?
	static func decode(value: Any) -> JSONCollection?
}

protocol NSJSONCollection: JSONCollection {
	func encode<T: NSObject>() -> Array<T>?
}

extension Array: NSJSONCollection {
	func encode<T>() -> Array<T>? {
		var encodedObjects = [T]()
		for item in self {
			if
				let jsonItem = item as? JSONPrimitive,
				let item = jsonItem as? T {
					encodedObjects.append(item)
			}
			else if
				let jsonItem = item as? JSONEnumeration,
				let rawValue = jsonItem.rawValue as? T {
					encodedObjects.append(rawValue)
			}
			else if
				let jsonItem = item as? NSJSONCollection,
				let object = jsonItem.encode() as? T {
					encodedObjects.append(object)
			}
			else if
				let jsonItem = item as? JSONObject,
				let object = jsonItem.encode() as? T {
					encodedObjects.append(object)
			}
		}
		return encodedObjects;
	}
	
	static func decode(value: Any) -> JSONCollection? {
		if
			let valid = value as? [Element] {
				return valid
		}
		else if
			let valid = value as? [[String: AnyObject]] {
				var newArray = [Element]()
				for item in valid {
					if
						let elementType = Element.self as? JSONObject.Type,
						let object = elementType.init(item) as? Element {
							newArray.append(object)
					}
					else if
						let elementType = Element.self as? JSONCollection.Type,
						let object = elementType.decode(item) as? Element {
							newArray.append(object)
					}
				}
				return newArray
		}
		
		return nil
	}
}
