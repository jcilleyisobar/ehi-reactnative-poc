//
//  EHIListCollectionViewDelegateTrampoline.h
//  Enterprise
//
//  Created by Ty Cobb on 1/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListCollectionView.h"
#import "EHIRefreshControlViewModel.h"

#define EHIRefreshControlSectionNil (NSNotFound)

@interface EHIListCollectionViewDelegateTrampoline : NSObject <EHIListCollectionViewDelegate>

/** The delegate to trampoline calls to */
@property (weak  , nonatomic) id<EHIListCollectionViewDelegate> target;
/** The index of the refrersh control section, if it exists */
@property (assign, nonatomic) NSInteger refreshControlSection;
/** This collection view's refresh control, if any */
@property (weak  , nonatomic) EHIRefreshControlViewModel *refreshControl;

@end
