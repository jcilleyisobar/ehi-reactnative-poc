//
//  EHIBarButtonItemSpinner.h
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 7/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@interface EHIBarButtonItemSpinner : UIBarButtonItem

+ (EHIBarButtonItemSpinner *)create;

@property (assign, nonatomic) BOOL isAnimating;

@end
