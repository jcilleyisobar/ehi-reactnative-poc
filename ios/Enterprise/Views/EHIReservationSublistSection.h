//
//  EHIReservationSublistSection.h
//  Enterprise
//
//  Created by Ty Cobb on 4/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface EHIReservationSublistSection : NSObject

/** The title for this sublist section */
@property (copy, nonatomic) NSString *title;
/** The list of models to render in this sublist section */
@property (copy, nonatomic) NSArray *models;

+ (instancetype)sectionWithTitle:(NSString *)title models:(NSArray *)models;

@end
