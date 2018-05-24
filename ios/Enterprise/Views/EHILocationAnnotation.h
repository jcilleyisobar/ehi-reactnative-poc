//
//  EHILocationAnnotation.h
//  Enterprise
//
//  Created by Ty Cobb on 2/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import MapKit;

#import "EHILocation.h"

@interface EHILocationAnnotation : NSObject <MKAnnotation>

/** The location backing this annotation */
@property (strong, nonatomic, readonly) EHILocation *location;
/** The name of the image to display, reactive */
@property (copy  , nonatomic, readonly) NSString *imageName;
/** The name of the image to display when selected, reactive */
@property (copy  , nonatomic, readonly) NSString *selectedImageName;
/** The geo-coordinate of this annotation, factoring in the offset */ 
@property (assign, nonatomic, readonly) CLLocationCoordinate2D coordinate;
/** The amount to offset the location's default coordinate by */
@property (assign, nonatomic) CLLocationCoordinate2D offset;

/**
 Initializes a new annotation for the location data model
 @param location The location for the annotation
*/

- (instancetype)initWithLocation:(EHILocation *)location;

@end
