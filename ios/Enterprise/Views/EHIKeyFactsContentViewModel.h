//
//  EHIKeyFactsContentViewModel.h
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIKeyFactsContentViewModel : EHIViewModel <MTRReactive>

//if the header is set then the cell is not linkable
@property (copy, nonatomic) NSString *contentText;

@property (copy  , nonatomic) NSAttributedString *linkText;
@property (copy  , nonatomic) NSString *exclusionText;
@property (assign, nonatomic) BOOL hasExclusion;

@property (assign, nonatomic) BOOL hasBlackDivider;

- (void)selectLink;
- (void)selectExclusions;

+ (instancetype)modelWithContent:(NSString *)content;

@end

EHIAnnotatable(EHIKeyFactsContentViewModel);