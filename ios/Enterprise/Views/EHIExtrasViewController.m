//
//  EHIExtrasViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIExtrasViewController.h"
#import "EHIExtrasViewModel.h"
#import "EHIExtrasExtraCell.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHIExtrasFixedExtraCell.h"
#import "EHIPlacardCell.h"
#import "EHIExtrasTermsCell.h"
#import "EHIReservationPriceButton.h"
#import "EHIReservationConfirmationFooter.h"
#import "EHICarClassCell.h"
#import "EHIClassSelectViewController.h"
#import "EHIActionButton.h"
#import "EHIListCollectionView.h"
#import "EHIRestorableConstraint.h"

@interface EHIExtrasViewController () <EHIListCollectionViewDelegate, EHICarClassCellActions, EHIExtrasExtraCellActions>
@property (strong, nonatomic) EHIExtrasViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIReservationConfirmationFooter *confirmationFooter;
@property (weak  , nonatomic) IBOutlet UILabel *loadingLabel;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
// animation properties
@property (weak  , nonatomic) IBOutlet UIView *loadingContainer;
@property (weak  , nonatomic) IBOutlet UIView *carClassPlaceholder;
// display link properties
@property (strong, nonatomic) CADisplayLink *displayLink;
@property (assign, nonatomic) CFTimeInterval initialTime;
@property (assign, nonatomic) CGFloat initialOffset;
@end

@implementation EHIExtrasViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionView];
}

//
// Helpers
//

- (void)configureCollectionView
{
    // inset for confirmation footer
    self.collectionView.contentInset = (UIEdgeInsets) { .bottom = self.confirmationFooter.frame.size.height };
    
    [self.collectionView.sections construct:@{
        @(EHIExtrasSectionCarClass)             : EHICarClassCell.class,
        @(EHIExtrasSectionPlacard)              : EHIPlacardCell.class,
        @(EHIExtrasSectionMandatory)            : EHIExtrasFixedExtraCell.class,
        @(EHIExtrasSectionIncluded)             : EHIExtrasFixedExtraCell.class,
        @(EHIExtrasSectionEquipment)            : EHIExtrasExtraCell.class,
        @(EHIExtrasSectionFuel)                 : EHIExtrasExtraCell.class,
        @(EHIExtrasSectionProtection)           : EHIExtrasExtraCell.class,
        @(EHIExtrasSectionTermsAndConditions)   : EHIExtrasTermsCell.class,
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
        
        EHISectionHeaderModel *model = [self.viewModel headerForSection:section.index];
        if(model) {
            model.style    = EHISectionHeaderStyleWrapText;
            [section.header setIsDynamicallySized:YES];

            section.header.klass = [EHISectionHeader class];
            section.header.model = model;
        }
    }
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.confirmationFooter.accessibilityIdentifier = EHIReservationFlowContinueKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIExtrasViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateExtras:)];
    [MTRReactor autorun:self action:@selector(invalidateExpandedState:)];
    [MTRReactor autorun:self action:@selector(invalidateInitialLoading:)];
    [MTRReactor autorun:self action:@selector(invalidateFooterLoading:)];

    EHIListDataSourceSection *car      = self.collectionView.sections[EHIExtrasSectionCarClass];
    EHIListDataSourceSection *placard  = self.collectionView.sections[EHIExtrasSectionPlacard];
    EHIListDataSourceSection *terms    = self.collectionView.sections[EHIExtrasSectionTermsAndConditions];

    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.placardModel)      : dest(placard, .model),
        source(model.carClassModel)     : dest(car,  .model),
        source(model.priceSummary)      : dest(self, .confirmationFooter.price),
        source(model.loadingTitle)      : dest(self, .loadingLabel.text),
        source(model.buttonTitle)       : dest(self, .confirmationFooter.attributedTitle),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
        source(model.termsModel)        : dest(terms, .model),
        source(model.priceSubtitleType) : dest(self, .confirmationFooter.priceSubtitleType)
    });
}

- (void)invalidateInitialLoading:(MTRComputation *)computation
{
    BOOL isLoading = self.viewModel.isLoading;
    
    // loading indicator
    UIView.animate(!computation.isFirstRun).duration(0.2).transform(^{
        self.loadingContainer.alpha = isLoading ? 1.0 : 0.0;
    }).start(nil);
    
    // prepare layout for extras animation
    if(computation.isFirstRun && isLoading) {
        [self insertCarClassPlaceholder];
        
        CGFloat translationY = CGRectGetMaxY(self.view.bounds);
        self.confirmationFooter.layer.transform = CATransform3DMakeTranslation(0.0, translationY, 0.0);
    }
    
    // extras did load
    if(!computation.isFirstRun && !isLoading) {
        self.collectionView.alpha = 0.0;
        
        // give collection view a chance to layout it's cells
        dispatch_after_seconds(0.01, ^{
            self.collectionView.alpha = 1.0;

            // place extras just above the car class placeholder
            CGSize contentSize = self.collectionView.collectionViewLayout.collectionViewContentSize;
            CGFloat offset = contentSize.height - self.carClassPlaceholder.frame.size.height;
            self.collectionView.contentOffset = (CGPoint){ .y = offset };
            
            [self scrollExtrasIntoView];
        });
    }
}

- (void)invalidateFooterLoading:(MTRComputation *)computation
{
    BOOL priceIsLoading = self.viewModel.isLoading || self.viewModel.priceIsLoading;
    
    self.confirmationFooter.enabled   = !priceIsLoading && !self.viewModel.shouldDisableContinueButton;
    self.confirmationFooter.isLoading = priceIsLoading;
    
    NSAttributedString *attributedTitle     = self.viewModel.buttonTitle;
    self.confirmationFooter.attributedTitle = attributedTitle;
    self.confirmationFooter.priceType = self.viewModel.priceType;
}

- (void)invalidateExtras:(MTRComputation *)computation
{
    // don't animate extras in until we have fetched extras
    if(!self.viewModel.carExtras) {
        return;
    }
  
    for(EHIExtrasSection section=EHIExtrasSectionIncluded ; section<=EHIExtrasSectionProtection ; section++) {
        self.collectionView.sections[section].models = [self.viewModel extrasInSection:section];
    }
}

- (void)invalidateExpandedState:(MTRComputation *)computation
{
    EHIListCollectionView *collectionView = self.collectionView;
    NSIndexPath *indexPath = self.viewModel.justChangedToggle ? self.viewModel.selectedTogglePath : self.viewModel.selectedPath;
    
    [collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        // compute the future frame of the cell
        CGRect expectedFrame = [collectionView ehi_expectedFrameForCellAtIndexPath:indexPath];
        // compute the height of the currently expanded cell
        CGFloat expandedCellHeight = [collectionView cellForItemAtIndexPath:self.viewModel.lastSelectedPath].frame.size.height;
        // compute the expected height of the currently expanded cell
        CGFloat expandedCellExpectedHeight = [collectionView ehi_expectedFrameForCellAtIndexPath:self.viewModel.lastSelectedPath].size.height;
        
        // determine the bottom position of the future frame and the current viewport
        CGFloat bottomOffset = CGRectGetMaxY(collectionView.bounds);
        CGFloat expectedBottomOffset = CGRectGetMaxY(expectedFrame);
 
        // if the cell extends below the viewport, scroll it on-screen
        if(expectedBottomOffset > bottomOffset) {
            CGFloat lastExpandedCellOffset = expandedCellHeight - expandedCellExpectedHeight;
            CGFloat offsetHeight = expectedBottomOffset - bottomOffset - lastExpandedCellOffset;
            if(offsetHeight < 0) {
                offsetHeight = 0;
            }
            
            CGPoint updatedOffset = CGPointOffset(collectionView.contentOffset, 0.0f, offsetHeight);
            [collectionView setContentOffset:updatedOffset animated:YES];
        }
    } completion:nil];
}

//
// Helpers
//

- (void)insertCarClassPlaceholder
{
    // placeholder variables
    EHIViewModel *model       = self.viewModel.carClassPlaceholderModel;
    EHILayoutMetrics *metrics = [EHICarClassCell metrics];
    
    // create placeholder car class cell
    EHICarClassCell *placeholderCell = [EHICarClassCell ehi_instanceFromNib];
    [placeholderCell updateWithModel:model];
    [self.carClassPlaceholder addSubview:placeholderCell];

    // determine placeholder height
    CGSize result = [EHICarClassCell dynamicSizeForContainerSize:self.collectionView.bounds.size metrics:metrics model:model];

    // constrain into our placeholder container
    [placeholderCell mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.trailing.equalTo(self.carClassPlaceholder);
    }];

    [self.carClassPlaceholder mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(result.height));
    }];
    
    // rerun reactions with properly sized cell (which loads guaranteed cached car class image)
    [placeholderCell registerReactions:model];
}

- (void)scrollExtrasIntoView
{
    self.displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(displayLinkTicked)];
    [self.displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
}

- (void)displayLinkTicked
{
    // run until we get an initial time
    if(self.initialTime == 0) {
        self.initialTime = self.displayLink.timestamp;
        self.initialOffset = self.collectionView.contentOffset.y;
        return;
    }

    CFTimeInterval timeDelta = self.displayLink.timestamp - self.initialTime;
    
    CGFloat offsetY = ehi_quadraticEaseOut(timeDelta, self.initialOffset, -self.initialOffset, EHIExtrasScrollInAnimationDuration);
    self.collectionView.contentOffset = (CGPoint){ .y = offsetY };
    
    if(timeDelta >= EHIExtrasScrollInAnimationDuration) {
        [self extrasDidScrollIntoView];
    }
}

- (void)extrasDidScrollIntoView
{
    // set end state
    self.collectionView.contentOffset = CGPointZero;
    
    // animate final views in
    UIView.animate(YES).duration(0.2).option(UIViewAnimationOptionCurveEaseOut).transform(^{
        self.carClassPlaceholder.alpha = 0.0;
    }).start(nil);
    
    UIView.animate(YES).duration(0.3).option(UIViewAnimationOptionCurveEaseOut).transform(^{
        self.confirmationFooter.layer.transform = CATransform3DIdentity;
    }).start(nil);
    
    // reset variables
    self.initialTime   = 0;
    self.initialOffset = 0;
    
    // clean up the display link
    [self.displayLink invalidate];
    [self setDisplayLink:nil];
}

# pragma mark - Actions

- (IBAction)didSelectConfirmationButton:(UIButton *)button
{
    [self.viewModel finishUpdatingExtras];
}

# pragma mark - EHICarClassCellActions

- (void)didTapPriceButtonForCarClassCell:(EHICarClassCell *)cell
{
    [self.viewModel finishUpdatingExtras];
}

# pragma mark - EHIExtrasExtraCellActions

- (void)didChangeAmountForExtrasCell:(EHIExtrasExtraCell *)cell
{
    [self.viewModel didChangeQuantityOfExtras];
}

- (void)didInvalidateHeightForExtrasCell:(EHIExtrasExtraCell *)cell
{
    self.viewModel.selectedTogglePath = [self.collectionView indexPathForCell:cell];
    [self invalidateExpandedState:nil];
}

- (void)didSelectArrowButtonForExtrasCell:(EHIExtrasExtraCell *)cell
{
    [self.viewModel selectExtraAtIndexPath:[self.collectionView indexPathForCell:cell]];
}

# pragma mark -  UICollectionView

- (void)collectionView:(UICollectionView *)collectionView didDequeueCell:(EHICollectionViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHIExtrasSectionPlacard) {
        cell.layer.zPosition = 1000;
    }
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.section == EHIExtrasSectionIncluded || indexPath.section == EHIExtrasSectionMandatory) {
        [self.viewModel showDetailsForExtraAtIndexPath:indexPath];
    }
}

# pragma mark - Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return [controller isKindOfClass:[EHIClassSelectViewController class]];
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    NSArray *animations = [super animationsForTransitionToViewController:controller isEntering:isEntering];
    
    return animations.concat(@[
        EHINavigationAnimation.target(self.view)
        .alpha(1.0)
        .duration(EHITransitionAnimationDuration)
        .delay(EHIClassSelectAnimationPhase1Duration + EHIClassSelectAnimationPhase2Duration)
    ]);
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationExtras state:EHIScreenReservationExtras];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationExtras;
}

@end
