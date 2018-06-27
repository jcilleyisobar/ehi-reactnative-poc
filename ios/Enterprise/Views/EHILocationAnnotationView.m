//
//  EHILocationAnnotationView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"
#import "EHILocationAnnotationView.h"

@interface EHILocationAnnotationView ()
@property (strong, nonatomic) MTRComputation *computation;
@end

@implementation EHILocationAnnotationView

- (void)setAnnotation:(EHILocationAnnotation *)annotation
{
    BOOL didUpdateAnnotation = ![self.annotation isEqual:annotation];
    
    [super setAnnotation:annotation];
    
    if(didUpdateAnnotation) {
        // update the image in a reaction, in case favoriting changes our pin
        [MTRReactor autorun:^(MTRComputation *computation) {
            if(self.annotation == annotation) {
                [self invalidateImageAnimated:NO];
            }
        }];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
   
    // update to whatever image we're supposed to be showing here
    [self invalidateImageAnimated:NO];
}

- (CGPoint)centerOffset
{
    return (CGPoint) {
        .x = 0.0f,
        .y = -self.image.size.height * 0.5f
    };
}

//
// Helpers
//

- (void)invalidateImageAnimated:(BOOL)animated
{
    // grab the correct image name for our state
    NSString *imageName = self.selected ? self.annotation.selectedImageName : self.annotation.imageName;
    // and update the image as necessary
    UIView.animate(animated).transform(^{
        self.image = imageName ? [UIImage imageNamed:imageName] : nil;
    }).start(nil);
}

@end
