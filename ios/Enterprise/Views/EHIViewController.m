//
//  EHIViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewController.h"
#import "EHIBarButtonItem.h"
#import "EHIModalOverlayTransitioningDelegate.h"
#import "EHILabel.h"
#import "EHISecurityManager.h"

@interface EHIViewController () <EHIKeyboardResponder, UIGestureRecognizerDelegate, EHIModalOverlayTransitioningCallbacks>
@property (assign, nonatomic) BOOL didLayoutSubviews;
@property (assign, nonatomic) BOOL shouldBecomeReady;
@property (assign, nonatomic) BOOL isReady;
@property (assign, nonatomic) BOOL shouldInvalidateAnalyticsContext;
@property (strong, nonatomic) id<UIViewControllerTransitioningDelegate> customTransitioningDelegate;
@property (strong, nonatomic) UITapGestureRecognizer *keyboardDismissalRecognizer;
@property (strong, nonatomic) UIView *bottomViewBelowSafeArea;
@end

@implementation EHIViewController

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    // use dynamic view model if provided
    if([attributes.userObject isKindOfClass:[EHIViewModel class]]) {
        self.viewModel = attributes.userObject;
    }
    // update our static view model with whatever we might have
    else if(attributes.userObject != nil) {
        [self.viewModel updateWithModel:attributes.userObject];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // customize the navigation item
    [self updateNavigationItem:self.navigationItem];
    
    // give subviews a chance to set the accessibilities identifiers
    [self registerAccessibilityIdentifiers];
    
    self.view.backgroundColor = self.backgroundColor ?: [UIColor ehi_greenColor];
    
    [self addBottomLineIfNeeded];
    [self addButtonBelowSafeAreaIfNeeded];
}

- (void)setBackgroundColor:(UIColor *)backgroundColor
{
    _backgroundColor = backgroundColor;
    
    self.view.backgroundColor = backgroundColor;
}

- (void)addBottomLineIfNeeded
{
    if(@available(iOS 11, *)) {
        if(self.needsBottomLine) {
            UIView *whiteLine = [UIView new];
            whiteLine.backgroundColor = UIColor.whiteColor;
            
            [self.view insertSubview:whiteLine aboveSubview:self.view.subviews.lastObject];
            
            CGFloat viewHeight = 1;
            [whiteLine mas_makeConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(@(viewHeight));
                make.leading.equalTo(self.view);
                make.trailing.equalTo(self.view);
                make.bottom.equalTo(self.view.mas_safeAreaLayoutGuideBottom).offset(viewHeight);
            }];
        }
    }
}

- (void)addButtonBelowSafeAreaIfNeeded
{
    if(@available(iOS 11, *)) {
        if(self.keyboardSupportedActionButton) {
            self.bottomViewBelowSafeArea = [UIView new];
            
            [self.view insertSubview:self.bottomViewBelowSafeArea belowSubview:self.keyboardSupportedActionButton];
            
            CGFloat viewHeight = 34;
            [self.bottomViewBelowSafeArea mas_makeConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(@(viewHeight));
                make.leading.equalTo(self.view);
                make.trailing.equalTo(self.view);
                make.bottom.equalTo(self.view.mas_bottom);
            }];
        }
    }
}

- (void)invalidateViewBelowSafeArea:(BOOL)isDisabled
{
    UIView.animate(YES).duration(0.25).transform(^{
        if(isDisabled) {
            [self.bottomViewBelowSafeArea setBackgroundColor:[UIColor ehi_grayColor4]];
        } else {
            [self.bottomViewBelowSafeArea setBackgroundColor:[UIColor ehi_greenColor]];
        }
    }).start(nil);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    // tell our view model that its visible
    self.viewModel.isActive = YES;
    // set should become ready once per view cycle
    self.shouldBecomeReady = YES;
    
    // register for keyboard notifications if our subclass has opted in
    if(self.requiresKeyboardSupport) {
        [NSNotificationCenter ehi_observeKeyboardNotifications:YES forTarget:self];
    }
    
    // -viewDidLayoutSubviews may not be called on subsequent appearances after
    // the first, so we'll register here to guarantee we have reactions hooked up
    if(self.didLayoutSubviews) {
        [self registerReactions:self.viewModel];
    }

    // kick-off the analytics lifecycle when we appear, if we should do so automatically
    if(self.automaticallyInvalidatesAnalyticsContext) {
        [self invalidateAnalyticsContext];
    }
    
    // enable content hiding when coming on screen
    [self toggleSecureContentIfNeeded:YES];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];

    // mark that we've laid out after the first run
    BOOL shouldRegisterReactions = !self.didLayoutSubviews;
    self.didLayoutSubviews = YES;
    
    // on first presentation, don't register until after laying out subviews to register
    // as late as possible. some view components (like map views) behave strangely if you
    // start trying to update them before the initial layout pass
    if(shouldRegisterReactions) {
        [self registerReactions:self.viewModel];
    }

    // only call this once per appear/disappear cycle, right before viewDidAppear
    if(self.shouldBecomeReady && !self.isReady) {
        [self willBecomeReady];
    }
}

- (void)willBecomeReady
{
    self.shouldBecomeReady = NO;
    
    if(self.keyboardSupportedActionButton) {
        // remove the anchoring bottom constraint on the action button
        NSArray *constraints = [self.keyboardSupportedActionButton constraintsAffectingLayoutForAxis:UILayoutConstraintAxisVertical];
        [self.view removeConstraint:constraints.find(^(NSLayoutConstraint *constraint) {
            return (constraint.firstItem  == self.keyboardSupportedActionButton
                || constraint.firstItem   == self.bottomLayoutGuide)
                && (constraint.secondItem == self.keyboardSupportedActionButton
                || constraint.secondItem  == self.bottomLayoutGuide);
        })];
        
        // create a matching masonry constraint that we can update with keyboard activity
        [self.keyboardSupportedActionButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.mas_bottomLayoutGuide);
        }];
    }
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
   
    self.isReady = YES;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    // disable content hiding when about to move off screen
    [self toggleSecureContentIfNeeded:NO];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];

    // un-register for keyboard notifications if our subclass has registered a scroll view
    if(self.requiresKeyboardSupport) {
        [NSNotificationCenter ehi_observeKeyboardNotifications:NO forTarget:self];
    }
    
    // tell our view model we're no longer visible
    self.viewModel.isActive = NO;
    // reset the readiness cycle
    self.isReady = NO;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers { }

# pragma mark - Setters

- (void)setViewModel:(EHIViewModel *)viewModel
{
    if(_viewModel == viewModel) {
        return;
    }
    
    _viewModel = viewModel;
    
    // changing our view model after we're ready requires re-activating the view model manually
    if(self.isReady) {
        [self.viewModel setIsActive:YES];
        [self registerReactions:self.viewModel];
        [self invalidateAnalyticsContext];
    }
}

# pragma mark - Keyboard

- (BOOL)requiresKeyboardSupport
{
    // if the subclass returns a scroll view, default to YES
    return [self keyboardSupportedScrollView] != nil;
}

- (UIScrollView *)keyboardSupportedScrollView
{
    return nil;
}

- (UIButton *)keyboardSupportedActionButton
{
    return nil;
}

- (void)keyboardWillShow:(NSNotification *)notification
{
    // if a subclass has opted in, set up the tap gesture recognizer for the keyboard
    if(self.requiresKeyboardSupport) {
        [self.view addGestureRecognizer:self.keyboardDismissalRecognizer];
    }
    
    [self applyKeyboardInsets:YES forNotification:notification];
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    // if a subclass has opted in, remove the tap gesture recognizer for the keyboard
    if(self.requiresKeyboardSupport) {
        [self.view removeGestureRecognizer:self.keyboardDismissalRecognizer];
    }
    
    [self applyKeyboardInsets:NO forNotification:notification];
}

- (BOOL)shouldDismissKeyboardForTouch:(UITouch *)touch
{
    return YES;
}

- (void)applyKeyboardInsets:(BOOL)shouldInset forNotification:(NSNotification *)notification
{
    CGFloat additionalInset = 0.f;
    
    if(self.keyboardSupportedActionButton) {
        // increase the scroll view inset by the height of the button
        additionalInset = self.keyboardSupportedActionButton.bounds.size.height;
        
        // offset the action button's bottom constraint by the height of the keyboard overlap
        [self.keyboardSupportedActionButton mas_updateConstraints:^(MASConstraintMaker *make) {
            CGFloat overlap = [notification ehi_keyboardOverlapInView:self.view];
            if(@available(iOS 11, *)) {
                make.bottom.equalTo(self.view.mas_bottom).offset(-overlap);
            } else {
                make.bottom.equalTo(self.mas_bottomLayoutGuide).offset(-overlap);
            }
        }];
    }
    
    // animate the scroll view insets
    [self.keyboardSupportedScrollView ehi_animateKeyboardNotification:notification additionalInset:additionalInset animations:^{
        [self.view layoutIfNeeded];
    }];
}

# pragma mark - Keyboard Gesture

- (UITapGestureRecognizer *)keyboardDismissalRecognizer
{
    if(!_keyboardDismissalRecognizer) {
        _keyboardDismissalRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapGestureRecognizer:)];
        _keyboardDismissalRecognizer.cancelsTouchesInView = NO;
        _keyboardDismissalRecognizer.delegate = self;
    }
    
    return _keyboardDismissalRecognizer;
}

- (void)handleTapGestureRecognizer:(UITapGestureRecognizer *)tapGesture
{
    if(tapGesture.state == UIGestureRecognizerStateRecognized) {
        // dismiss the keyboard
        [self.view endEditing:YES];
        // handles cases where the navigation bar contains a search field
        [self.navigationController.navigationBar endEditing:YES];
    }
}

//
// UIGestureRecognizerDelegate
//

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
    if([touch.view isKindOfClass:[UITextField class]] || [touch.view isKindOfClass:[UIButton class]]) {
        return NO;
    } else {
        return [self shouldDismissKeyboardForTouch:touch];
    }
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRequireFailureOfGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    return YES;
}

# pragma mark - Security

- (BOOL)hasSecureContent
{
    return YES;
}

- (void)toggleSecureContentIfNeeded:(BOOL)visible
{
    if(self.hasSecureContent) {
        [EHISecurityManager sharedInstance].shouldHideContent = visible;
    }
}

# pragma mark - Subclassing Hooks

- (void)registerReactions:(id)model
{
    
}

- (void)updateNavigationItem:(UINavigationItem *)item
{
    // replace default back button with our own
    if(self.navigationController.viewControllers.count > 1) {
        item.hidesBackButton    = YES;
        item.leftBarButtonItem = [EHIBarButtonItem backButtonWithTarget:self action:@selector(didTapBackButton:)];
    }
    
    // create a custom title label for multi-line support
    EHILabel *titleLabel = [[EHILabel alloc] initWithFrame:(CGRect){
        .size.height = 44.0f
    }];
    
    titleLabel.font = [UIFont ehi_fontWithStyle:EHIFontStyleRegular size:18.0f];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.numberOfLines = 2;
    
    item.titleView = titleLabel;
}

- (void)setTitle:(NSString *)title
{
    [super setTitle:title];
    
    // update the title view if it's our custom multi-line label
    if ([self.navigationItem.titleView isKindOfClass:[EHILabel class]]) {
        [(EHILabel *)self.navigationItem.titleView setText:title];
        [(EHILabel *)self.navigationItem.titleView sizeToFit];
    }
}

- (BOOL)showsPhoneButton
{
    return NO;
}

# pragma mark - Analytics

- (BOOL)automaticallyInvalidatesAnalyticsContext
{
    return YES;
}

- (void)invalidateAnalyticsContext
{
    self.shouldInvalidateAnalyticsContext = NO;
    
    // allow subclasses to configure analytics context
    [self prepareToUpdateAnalyticsContext];
    
    // update the context
    EHIAnalyticsContext *context = [EHIAnalytics context];
    [self updateAnalyticsContext:context];
    
    // after any/all deferrals complete, finish updating the context
    [self didUpdateAnalyticsContext];
}

- (void)prepareToUpdateAnalyticsContext
{
    
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [self.viewModel updateAnalyticsContext:context];
}

- (void)didUpdateAnalyticsContext
{
    
}

# pragma mark - Interface Actions

- (void)didTapBackButton:(UIButton *)button
{
    // tell the view model to navigate back
    [self.viewModel navigateBack];
}

# pragma mark - Modal Transitions

- (UIModalPresentationStyle)modalPresentationStyle
{
    return self.customModalTransitionStyle != EHIModalTransitionStyleDefault ? UIModalPresentationCustom : [super modalPresentationStyle];
}

- (id<UIViewControllerTransitioningDelegate>)transitioningDelegate
{
    return self.customModalTransitionStyle != EHIModalTransitionStyleDefault ? self.customTransitioningDelegate : [super transitioningDelegate];
}

- (id<UIViewControllerTransitioningDelegate>)customTransitioningDelegate
{
    if(_customTransitioningDelegate) {
        return _customTransitioningDelegate;
    }
  
    if(self.customModalTransitionStyle != EHIModalTransitionStyleDefault) {
        EHIModalOverlayTransitioningDelegate *transition = [EHIModalOverlayTransitioningDelegate new];
        transition.forcesMaximumHeight = self.customModalTransitionStyle == EHIModalTransitionStyleOverlayFullscreen;
        transition.needsAutoDismiss  = [self needsAutoDismiss];
        transition.callback = self;
        _customTransitioningDelegate = transition;
    }
    
    return _customTransitioningDelegate;
}

# pragma mark - EHIModalOverlayTransitioningCallbacks

- (void)overlayTransitionDidTapOverlayContainer:(EHIModalOverlayTransitioningDelegate *)delegate
{

}

- (BOOL)needsAutoDismiss
{
    return NO;
}

# pragma mark - Modal Presentation Hooks

- (void)dismissViewControllerAnimated:(BOOL)flag completion:(void (^)(void))completion
{
    // mark our presenter as needing an analytics update
    EHIViewController *presenter = [self presenter];
    presenter.shouldInvalidateAnalyticsContext = YES;
    
    [super dismissViewControllerAnimated:flag completion:^{
        ehi_call(completion)();
       
        // if the presenter still needs to run the analytics cycle after dimissal, then kick it off
        if(presenter.shouldInvalidateAnalyticsContext) {
            [presenter invalidateAnalyticsContext];
        }
    }];
}

- (EHIViewController *)presenter
{
    // pull the top view controller of our presenters nav controller
    UIViewController *presenter = self;
    if(presenter.navigationController) {
        presenter = presenter.navigationController.topViewController;
    }
    
    // and return it, as long as it's our custom class
    return [presenter isKindOfClass:[EHIViewController class]] ? (EHIViewController *)presenter : nil;
}

# pragma mark - Navigation Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return NO;
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return @[];
}

# pragma mark - Status Bar

- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleLightContent;
}

# pragma mark - NAVViewController

+ (NSString *)storyboardName
{
    // default naming convention is "#{section}ViewController" -> "#{section}Storyboard"
    return [NSStringFromClass(self) stringByReplacingOccurrencesOfString:@"ViewController" withString:@"Storyboard"];
}

@end
