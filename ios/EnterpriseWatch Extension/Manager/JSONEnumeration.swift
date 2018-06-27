//
//  JSONEnumeration.swift
//
//  Created by George Stuart on 11/16/15.
//

import Foundation

protocol JSONEnumeration: JSONType, JSONStringConvertible {
	init?(rawValue: String)
	var rawValue: String { get }
}

extension JSONEnumeration {
	init?(_ text: String) {
		self.init(rawValue: text)
	}

	static func convertFromString(string: String) -> Self? {
		return Self(rawValue: string)
	}
}