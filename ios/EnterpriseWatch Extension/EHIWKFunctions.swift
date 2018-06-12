//
//  EHIWKFunctions.swift
//  Enterprise
//
//  Created by George Stuart on 12/1/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

import Foundation

// MARK:- Localized Strings
@warn_unused_result func EHIWKLocalizedString(key: String, fallback: String) -> String
{
    return EHILocalization.localizeKey(key, fallback: fallback)
}

// MARK:- Custom fonts
@warn_unused_result func ehi_heavyFontWithSize(points: CGFloat) -> UIFont? {
    return UIFont(name: "SourceSansPro-Black", size: points)
}

@warn_unused_result func ehi_boldFontWithSize(points: CGFloat) -> UIFont? {
    return UIFont(name: "SourceSansPro-Bold", size: points)
}

@warn_unused_result func ehi_regularFontWithSize(points: CGFloat) -> UIFont? {
    return UIFont(name: "SourceSansPro-Regular", size: points)
}

@warn_unused_result func ehi_lightFontWithSize(points: CGFloat) -> UIFont? {
    return UIFont(name: "SourceSansPro-Light", size: points)
}

// MARK:- Logging
func EHIWKLog(format: String, args: CVarArgType...) {
	#if DEBUG
		func wrapVarArg(valist: CVaListPointer) {
			NSLogv(format, valist)
		}

	withVaList(args, wrapVarArg)
	#endif
}