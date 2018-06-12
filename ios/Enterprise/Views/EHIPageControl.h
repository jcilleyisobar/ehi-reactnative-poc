//
//  EHIPageControl.h
//  Enterprise
//
//  Created by Ty Cobb on 3/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHIPageControl : UIView
@property (assign, nonatomic) NSInteger numberOfPages;
@property (assign, nonatomic) NSInteger currentPage;
@property (assign, nonatomic) BOOL automaticallyResizesToFitWidth;
@property (assign, nonatomic) BOOL hidesForSinglePage;
@end
