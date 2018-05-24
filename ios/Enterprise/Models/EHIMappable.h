//
//  EHIMappable.h
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHIMappable <NSObject>

/**
 @brief Maps all values in the collection according to the block 
 
 The expected behavior is that the type of collection doesn't change through mapping,
 and if the collection contains more than simply values, such as a dictionary, then only the
 values are changed.
 
 If a nil value is returned from the block, it should be removed from the collection. Thus,
 this is more of a 'map/filter'.
 
 @param block The block to map the values.
 
 @return A new collection containing the mapped values
*/

- (instancetype)map:(id(^)(id))block;

@end
