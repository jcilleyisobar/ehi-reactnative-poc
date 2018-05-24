//
//  JSONPrimitive.swift
//  Enterprise
//
//  Created by George Stuart on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

protocol JSONPrimitive: JSONType {}
extension String: JSONPrimitive {}
extension Bool: JSONPrimitive {}
extension Double: JSONPrimitive{}
extension Float: JSONPrimitive {}
extension Int: JSONPrimitive {}
