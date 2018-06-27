//
//  EHIDashboardQuickstartCell.m
//  Enterprise
//
//  Created by Ty Cobb on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardQuickstartCell.h"
#import "EHIDashboardQuickstartViewModel.h"
#import "EHIButton.h"

typedef NS_ENUM(NSUInteger, EHIDashboardQuickstartCellState) {
    EHIDashboardQuickstartCellStateDefault,
    EHIDashboardQuickstartCellStateDeleteShowing,
    EHIDashboardQuickstartCellStateDeleted
};

@interface EHIDashboardQuickstartCell () <UIGestureRecognizerDelegate>
@property (strong, nonatomic) EHIDashboardQuickstartViewModel *viewModel;
@property (assign, nonatomic) EHIDashboardQuickstartCellState viewState;
@property (strong, nonatomic) UIPanGestureRecognizer *swipeToDeleteGesture;
@property (assign, nonatomic) CGPoint panStartPoint;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *typeNameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet UILabel *deletedLabel;
@property (strong, nonatomic) IBOutlet EHIButton *deleteButton;
@property (weak  , nonatomic) IBOutlet EHIButton *undoButton;
@end

@implementation EHIDashboardQuickstartCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIDashboardQuickstartViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
 
    // invoke setter side effects
    self.viewState = EHIDashboardQuickstartCellStateDefault;
    
    // pan gesture for swipe to delete
    self.swipeToDeleteGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(didPan:)];
    self.swipeToDeleteGesture.delegate = self;
    
    [self.contentContainer addGestureRecognizer:self.swipeToDeleteGesture];
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.viewState = self.viewModel.isDeleted ? EHIDashboardQuickstartCellStateDeleted : EHIDashboardQuickstartCellStateDefault;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    UIView *hitView = [super hitTest:point withEvent:event];
    
    // ignore hits when deleted and those that don't touch undo button
    if(self.viewModel.isDeleted && ![hitView isEqual:self.undoButton]) {
        return nil;
    }
    
    return hitView;
}

# pragma mark - Register Reactions

- (void)registerReactions:(EHIDashboardQuickstartViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.typeName)      : dest(self, .typeNameLabel.text),
        source(model.title)         : dest(self, .titleLabel.text),
        source(model.subtitle)      : dest(self, .subtitleLabel.text),
        source(model.iconName)      : dest(self, .iconImageView.ehi_imageName),
        source(model.deletedTitle)  : dest(self, .deletedLabel.text),
        source(model.undoTitle)     : dest(self, .undoButton.ehi_title),
        source(model.alignsIconLeft) : ^(NSNumber *alignsIconLeft) {
            self.iconImageView.contentMode = alignsIconLeft.boolValue ? UIViewContentModeLeft : UIViewContentModeCenter;
        },
    });
}

# pragma mark - Gestures

- (void)didPan:(UIPanGestureRecognizer *)gesture
{
    switch(gesture.state) {
        case UIGestureRecognizerStateBegan:
            [self panGestureDidBegin:gesture]; break;
        case UIGestureRecognizerStateChanged:
            [self panGestureDidChange:gesture]; break;
        case UIGestureRecognizerStateEnded:
            [self panGestureDidEnd:gesture]; break;
        default:
            break;
    }
}

//
// Helpers
//

- (void)panGestureDidBegin:(UIPanGestureRecognizer *)pan
{
    CGPoint startPoint = [pan translationInView:self.contentContainer];

    // adjust start point if view has already been translated
    if(self.viewState == EHIDashboardQuickstartCellStateDeleteShowing) {
        startPoint.x += self.deleteButton.frame.size.width;
    }
    
    self.panStartPoint = startPoint;
}

- (void)panGestureDidChange:(UIPanGestureRecognizer *)pan
{
    CGFloat deltaX = [pan translationInView:self.contentContainer].x - self.panStartPoint.x;
    deltaX = deltaX > 0 ? 0.0 : deltaX;
    
    // start crossfade when view is half way off screen
    CGFloat crossfadeArea = self.contentContainer.frame.size.width / 2;
    CGFloat fade = (fabs(deltaX) - crossfadeArea) / crossfadeArea;
    
    self.deletedLabel.alpha = fade;
    self.undoButton.alpha = fade;
    self.deleteButton.alpha = 1.0 - fade;
    self.contentContainer.layer.transform = CATransform3DMakeTranslation(deltaX, 0.0, 0.0);
}

- (void)panGestureDidEnd:(UIPanGestureRecognizer *)pan
{
    CGFloat velocityX = [pan velocityInView:self.contentContainer].x;
    CGFloat deltaX = [pan translationInView:self.contentContainer].x - self.panStartPoint.x;
    
    BOOL isFastPan = fabs(velocityX) > 500.0;
    BOOL isLeftPan = velocityX < 0.0;
    BOOL isHalfViewPan = fabs(deltaX) > self.contentContainer.frame.size.width / 2;
    BOOL didRevealDelete = fabs(deltaX) > self.deleteButton.frame.size.width;
    
    // delete if panning left AND covered half the view OR panned quickly
    if(isLeftPan && (isHalfViewPan || isFastPan)) {
        // complete animation by sliding view off screen
        [self setViewState:EHIDashboardQuickstartCellStateDeleted animated:YES];
        
        // delete from local cache
        [self.viewModel deleteQuickstart];
    }
    // or show delete if it has been fully revealed
    else if (didRevealDelete) {
        [self setViewState:EHIDashboardQuickstartCellStateDeleteShowing animated:YES];
    }
    // otherwise, return to default position
    else {
        [self setViewState:EHIDashboardQuickstartCellStateDefault animated:YES];
    }
}

# pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    if([gestureRecognizer isEqual:self.swipeToDeleteGesture]) {
        CGPoint velocity = [self.swipeToDeleteGesture velocityInView:self.contentContainer];
        
        // both directions if deleted button is showing
        CGFloat velocityX = self.viewState == EHIDashboardQuickstartCellStateDeleteShowing
            ? fabs(velocity.x) // both directions
            : -1 * velocity.x; // left only
        
        // yes if left horizontal pan with speed greater than y direction
        return velocityX > fabs(velocity.y);
    }
    
    return [super gestureRecognizerShouldBegin:gestureRecognizer];
}

# pragma mark - Actions

- (IBAction)didTapDeleteButton:(id)sender
{
    [self setViewState:EHIDashboardQuickstartCellStateDeleted animated:YES];
    
    [self.viewModel deleteQuickstart];
}

- (IBAction)didTapUndoButton:(id)sender
{
    [self setViewState:EHIDashboardQuickstartCellStateDefault animated:YES];
    
    [self.viewModel undoDelete];
}

# pragma mark - Layout

- (void)setViewState:(EHIDashboardQuickstartCellState)viewState
{
    [self setViewState:viewState animated:NO];
}

- (void)setViewState:(EHIDashboardQuickstartCellState)viewState animated:(BOOL)animated
{
    _viewState = viewState;
    
    BOOL deleted = viewState == EHIDashboardQuickstartCellStateDeleted;

    CATransform3D transform;
    switch (viewState) {
        case EHIDashboardQuickstartCellStateDefault:
            transform = CATransform3DIdentity; break;
        case EHIDashboardQuickstartCellStateDeleteShowing:
            transform = CATransform3DMakeTranslation(-1 * self.deleteButton.frame.size.width, 0.0, 0.0); break;
        case EHIDashboardQuickstartCellStateDeleted:
            transform = CATransform3DMakeTranslation(-1 * self.contentContainer.frame.size.width, 0.0, 0.0); break;
    }
    
    UIView.animate(animated).duration(0.3).transform(^{
        self.contentContainer.layer.transform = transform;
        self.deletedLabel.alpha = deleted ? 1.0 : 0.0;
        self.undoButton.alpha = deleted ? 1.0 : 0.0;
        self.deleteButton.alpha = deleted ? 0.0 : 1.0;
    }).start(nil);
}

- (CGSize)intrinsicContentSize
{
    CGRect bottomFrame = [self.bottomView convertRect:self.bottomView.bounds toView:self];
    
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHILightPadding,
    };
}

- (UIView *)bottomView
{
    return self.viewModel.subtitle ? self.subtitleLabel : self.titleLabel;
}

@end
