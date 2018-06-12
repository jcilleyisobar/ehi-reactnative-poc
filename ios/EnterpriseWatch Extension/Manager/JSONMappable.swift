//
//  JSONMappable.swift
//  Enterprise
//
//  Created by George Stuart on 10/15/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

protocol JSONMappable {
	var jsonKey: String { get }
	func getEncoded() -> Any?
	func setDecoded(value: Any?)
}
