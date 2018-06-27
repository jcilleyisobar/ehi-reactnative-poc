//
//  EHIArrowBorderView.h
//  Enterprise
//
//  Created by Marcelo Rodrigues on 22/06/2018.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIView.h"
#import "EHIArrowBorderLayer.h"

@interface EHIArrowBorderView : EHIView
@property (assign, nonatomic) EHIArrowBorderLayerSide side;
@property (strong, nonatomic) UIColor *fillColor;
@property (strong, nonatomic) UIColor *strokeColor;
@end
