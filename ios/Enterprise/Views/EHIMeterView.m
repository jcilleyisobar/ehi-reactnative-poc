//
//  EHIMeterView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIMeterView.h"
#import "EHIMeterLayer.h"

@implementation EHIMeterView

- (void)setFill:(CGFloat)fill
{
    [self.meterLayer setFillPercent:fill animated:YES];
    
    [self setNeedsDisplay];
}

- (void)setMeterData:(EHIMeterData)meterData
{
    self.meterLayer.meterData = meterData;
    
    [self setNeedsDisplay];
}

- (EHIMeterLayer *)meterLayer
{
    return (EHIMeterLayer *)self.layer;
}

+ (Class)layerClass
{
    return [EHIMeterLayer class];
}

@end
