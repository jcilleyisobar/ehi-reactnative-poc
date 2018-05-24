//
//  JSONProtocols.swift
//  Enterprise
//
//  Created by George Stuart on 11/10/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

protocol JSONType {}

protocol JSONStringConvertible: JSONType {
	static func convertFromString(string: String) -> Self?
}

extension Double: JSONStringConvertible {
	static func convertFromString(string: String) -> Double? {
		return Double(string)
	}
}
extension Float: JSONStringConvertible {
	static func convertFromString(string: String) -> Float? {
		return Float(string)
	}
}
extension Int: JSONStringConvertible {
	static func convertFromString(string: String) -> Int? {
		return Int(string)
	}
}
