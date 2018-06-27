//
//  EHIComparable.h
//  Enterprise
//
//  Created by Ty Cobb on 4/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIComparable <NSObject>

/**
 @brief An identifer that can be used to compare two objects conforming to @c EHIComparable

 If the identifier is invalid, @c nil, when accessed, the called may throw an error.
*/

- (id)uid;

@end
