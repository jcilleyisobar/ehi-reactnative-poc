//
//  EHICollectionTitleView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionReusableView.h"

@interface EHICollectionTitleView : EHICollectionReusableView
/** The label backing this collection view title */
@property (weak  , nonatomic, readonly) UILabel *titleLabel;
@end