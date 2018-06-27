//
//  EHILocationAnnotationView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationAnnotation.h"

@interface EHILocationAnnotationView : MKAnnotationView
@property (strong, nonatomic) EHILocationAnnotation *annotation;
@end
