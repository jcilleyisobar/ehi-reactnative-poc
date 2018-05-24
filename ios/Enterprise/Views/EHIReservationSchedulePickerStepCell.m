//
//  EHIReservationSchedulePickerCell.m
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSchedulePickerStepCell.h"
#import "EHIReservationSchedulePickerStepViewModel.h"
#import "EHIArrowLayer.h"

@interface EHIReservationSchedulePickerStepCell ()
@property (strong, nonatomic) EHIReservationSchedulePickerStepViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) EHIArrowLayer *arrowLayer;
@end

@implementation EHIReservationSchedulePickerStepCell

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self invalidateArrowFrame];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.accessibilityIdentifier = EHIItineraryCalendarTimeKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationSchedulePickerStepViewModel *)model
{
    [MTRReactor autorun:self action:@selector(invalidateHighlightedState:)];
    
    model.bind.pair(
        source(model.text), dest(self, .titleLabel.text)
    );
}

- (void)invalidateHighlightedState:(MTRComputation *)computation
{
    BOOL isHighlighted = self.viewModel.isCurrentStep;
    
    UIView.animate(!computation.isFirstRun).duration(0.15).transform(^{
        self.titleLabel.textColor = isHighlighted ? [UIColor whiteColor] : [UIColor blackColor];
        self.contentView.backgroundColor = isHighlighted ? [UIColor ehi_darkGreenColor] : [UIColor ehi_graySpecialColor];
    }).start(nil);
    
    [self updateArrow];
}

# pragma mark - Interface Actions

- (IBAction)didRecognizeTapGesture:(UITapGestureRecognizer *)gesture
{
    [self.viewModel selectStep];
}

# pragma mark - EHIView

+ (BOOL)isReplaceable
{
    return YES;
}

- (void)updateArrow
{
    BOOL showsArrow = self.viewModel.isCurrentStep;
    
    // update the the arrow style un-animatedly
    [CALayer ehi_performUnanimated:^{
        self.clipsToBounds   = !showsArrow;
    }];
    
    // show the arrow animatedly
    self.arrowLayer.opacity = showsArrow ? 1.0f : 0.0f;
}

- (void)invalidateArrowFrame
{
    CGRect frame = self.arrowLayer.frame;
    frame.origin = (CGPoint){
        .x = (self.bounds.size.width - frame.size.width) / 2.0f,
        .y = CGRectGetMaxY(self.bounds)
    };
    
    self.arrowLayer.frame = frame;
}

# pragma mark - Accessors

- (EHIArrowLayer *)arrowLayer
{
    if(_arrowLayer) {
        return _arrowLayer;
    }
    
    // create the arrow layer with the default size
    EHIArrowLayer *arrowLayer = [EHIArrowLayer new];
    arrowLayer.fillColor = [UIColor ehi_darkGreenColor].CGColor;
    arrowLayer.direction = EHIArrowDirectionDown;

    arrowLayer.frame = (CGRect){
        .size = (CGSize){ .width = 23.0f, .height = 12.0f },
    };
    
    // insert and store it
    [self.layer insertSublayer:arrowLayer atIndex:0];
    _arrowLayer = arrowLayer;
    
    return _arrowLayer;
}

@end
