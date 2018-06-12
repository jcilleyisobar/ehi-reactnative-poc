//
//  EHIMapTransformer.h
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHIMapTransformer : NSValueTransformer

/** The default value if forward lookup fails */
@property (strong, nonatomic) id defaultValue;
/** The default value if reverse lookup fails */
@property (strong, nonatomic) id reverseDefaultValue;

/**
 @brief Instantiates a new transformer for the specified map.
 
 This transformer is reversible, but your map should be a bijection if you want the
 reverse mapping to work properly.
 
 @param map          The dictionary this transformer encapsulates
 @param defaultValue The default value if a no mapping is found in the dictionary
 
 @return A new value transformer
*/

- (instancetype)initWithMap:(NSDictionary *)map;

@end
