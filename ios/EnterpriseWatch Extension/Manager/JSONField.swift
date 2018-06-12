//
//  JSONField.swift
//
//  Created by George Stuart on 10/15/15.
//

import Foundation

infix operator <- { associativity right precedence 90 }
func <-<T>(inout left: JSONField<T>, right: Optional<T>) {
	if  let validRight = right {
		left.set(validRight)
	}
}

func <-<T>(inout left: Optional<T>, right: JSONField<T>) {
	left = right.get()
}

class JSONField<T: JSONType>: JSONMappable, CustomStringConvertible {
	let jsonKey: String
	private var value: T?
	private let decoder: ((value: Any?) -> T?)?
	private let encoder: ((value: T?) -> AnyObject?)?
	
	init(_ key: String, decoder: ((value: Any?) -> T?)? = nil, encoder: ((value: T?) -> AnyObject?)? = nil) {
		jsonKey = key
		self.decoder = decoder
		self.encoder = encoder
	}
	
	func get() -> T? {
		return value
	}
	
	func set(newValue: T?) {
		self.value = newValue
	}
	
	func getEncoded() -> Any? {
		if
			let encoder = self.encoder {
				return encoder(value: self.value)
		}
		else if
			let objectValue = self.value as? JSONPrimitive {
				return objectValue
		}
		else if
			let objectValue = self.value as? JSONEnumeration {
				return objectValue.rawValue
		}
		else if
			let objectValue = self.value as? JSONObject {
				return objectValue.encode()
		}
		else if
			let objectValue = self.value as? NSJSONCollection {
				return objectValue.encode()
		}
		return value;
	}
	
	func setDecoded(newValue: Any?) {
		if
			let decoder = self.decoder {
				self.value = decoder(value: newValue)
		}
		else if
			let newValue = newValue as? T {
				self.value = newValue
		}
		else if
			let newValue = newValue as? String,
			let objectType = T.self as? JSONStringConvertible.Type {
				self.value = objectType.convertFromString(newValue) as? T
		}
		else if
			let newValue = newValue as? [String: AnyObject],
			let objectType = T.self as? JSONObject.Type {
				self.value = objectType.init(newValue) as? T
		}
		else if
			let newValue = newValue,
			let objectType = T.self as? JSONCollection.Type {
				self.value = objectType.decode(newValue) as? T
		}
			
		else {
			print("Skipping \(self.jsonKey)")
		}
	}
	
	var description: String {
		if value is CustomStringConvertible {
			return (value as! CustomStringConvertible).description
		}
		else if value is String {
			return value as! String
		}
		else if value is JSONEnumeration {
			return (value as! JSONEnumeration).rawValue
		}
		else {
			return "No description available"
		}
	}
}