//
//  EHIOnboardingViewController.m
//  Enterprise
//
//  Created by Stu Buchbinder on 12/9/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import <SpriteKit/SpriteKit.h>
#import "EHIOnboardingViewController.h"
#import "EHIOnboardingViewModel.h"
#import "EHIOnboardingScene.h"
#import "EHIOnboardingBenefitsView.h"
#import "EHIOnboardingJoinNowView.h"
#import "EHIActivityIndicator.h"
#import "car_sprites.h"

typedef NS_ENUM (NSInteger, EHIOnboardingScrollDirection) {
    EHIOnboardingScrollDirectionRight,
    EHIOnboardingScrollDirectionLeft
};

@interface EHIOnboardingViewController () <UIScrollViewDelegate>
@property (strong, nonatomic) EHIOnboardingViewModel *viewModel;
@property (strong, nonatomic) NSMutableArray *scenes;
@property (weak  , nonatomic) IBOutlet UIImageView *carImageView;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UIPageControl *pageControl;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *carCenterConstraint;
@end

@implementation EHIOnboardingViewController

static const int EHIOnboardingNumberOfScenes = 4;

- (instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIOnboardingViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
        
    self.pageControl.pageIndicatorTintColor        = [UIColor ehi_grayColor5];
    self.pageControl.currentPageIndicatorTintColor = [UIColor ehi_blackSpecialColor2];
    
    [self.loadingIndicator startAnimating];
    
    [self addScenes];
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)addScenes
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self setupOnboardingScenes];
        [self setupCarScene];
        [self.loadingIndicator stopAnimating];
        
        self.viewModel.scenes = self.scenes;
        self.pageControl.numberOfPages = self.scenes.count;
    });
}

# pragma mark - Helpers

- (NSMutableArray *)scenes
{
    if(!_scenes) {
        _scenes = @[].mutableCopy;
    }
    
    return _scenes;
}

- (CGRect)sceneRect
{
	CGSize size = (CGSize) {
		.width  = CGRectGetWidth(self.view.frame),
		.height = CGRectGetHeight(self.scrollView.frame)
	};

	return (CGRect) {
		.origin = CGPointZero,
		.size   = size
	};
}

- (SKView *)sceneContainer
{
    SKView *skView = [[SKView alloc] initWithFrame:self.sceneRect];
    skView.contentMode     = UIViewContentModeScaleAspectFit;
    skView.backgroundColor = [UIColor whiteColor];
    skView.allowsTransparency = YES;

    return skView;
}

- (CGRect)titleFrameForScene:(EHIOnboardingScene *)scene
{
    CGFloat padding     = 14.0f;
    CGPoint sceneCenter = [scene convertPointToView:scene.position];
    CGFloat sceneWidth  = CGRectGetWidth(scene.firstNode.frame);
    CGFloat sceneTop    = sceneCenter.y / 2;

	CGSize size = (CGSize) {
		.width  = sceneWidth,
		.height = sceneTop + padding
	};

    return (CGRect) {
		.origin = CGPointZero,
		.size   = size
	};
}

- (CGFloat)verticalOffsetForTitle:(UILabel *)title frame:(CGRect)frame
{
	CGFloat padding     = 12.0f;
    CGFloat titleHeight = CGRectGetHeight(title.frame);
    CGFloat offset      = CGRectGetHeight(frame) - titleHeight - padding;

    return offset;
}

- (int)nextPageIndex:(EHIOnboardingScrollDirection)direction
{
    int currentScene = (int)self.viewModel.currentSceneIndex;
    int nextPage = (direction == EHIOnboardingScrollDirectionLeft) ? currentScene + 1 : currentScene - 1;

	BOOL onFirstPage = nextPage <= 0;
	BOOL onLastPage  = nextPage > self.pageControl.numberOfPages - 1;
    if(onFirstPage) {
        nextPage = 0;
    } else if(onLastPage) {
        nextPage = (int)self.pageControl.numberOfPages - 1;
    }

    return nextPage;
}

# pragma mark - Scene Title

- (void)addSceneTitleToView:(SKView *)sceneView;
{
    EHIOnboardingScene *scene = (EHIOnboardingScene *)sceneView.scene;
    CGRect frame = [self titleFrameForScene:scene];

    UILabel *title = [[UILabel alloc] initWithFrame:frame];
    title.backgroundColor = [UIColor clearColor];
    title.textAlignment   = NSTextAlignmentCenter;
    title.lineBreakMode   = NSLineBreakByWordWrapping;
    title.numberOfLines   = 0;
    title.attributedText  = scene.title;
    title.adjustsFontSizeToFitWidth = YES;
    [title sizeThatFits:frame.size];
    
    [sceneView addSubview:title];
    
    CGFloat xOffset = (CGRectGetWidth(self.sceneRect) / 2) - (CGRectGetWidth(frame) / 2);
    CGFloat yOffset = [self verticalOffsetForTitle:title frame:frame];
    title.frame = CGRectOffset(title.frame, xOffset, yOffset);
}

# pragma mark - Scene Initialization

- (void)setupCarScene
{
    EHIOnboardingScene *firstScene = (EHIOnboardingScene *)self.scenes.firstObject;
    SKNode *node = [firstScene childNodeWithName:@"layer0"];
    
    self.carImageView.frame  = node.frame;
    self.carImageView.center = self.scrollView.center;
    self.carImageView.animationImages   = CAR_IMAGE_ANIMATION;
    self.carImageView.animationDuration = 0.65f;

    [self.carImageView startAnimating];
}

- (void)setupOnboardingScenes
{
    [self initializeSceneWithType:EHIOnboardingSceneViewTypeBalloon];
    [self initializeSceneWithType:EHIOnboardingSceneViewTypeCards];
    [self initializeSceneWithType:EHIOnboardingSceneViewTypeSun];
    [self initializeSceneWithType:EHIOnboardingSceneViewTypeFerrisWheel];
    
	BOOL isRewards = self.viewModel.isRewards;
	if(isRewards) {
        [self initializeView:[EHIOnboardingBenefitsView ehi_instanceFromNib]];
        [self initializeView:[EHIOnboardingJoinNowView ehi_instanceFromNib]];
	}

    [self refreshContentSize];
}

- (void)initializeSceneWithType:(EHIOnboardingSceneViewType)type
{
    EHIOnboardingSceneViewModel *model = [[EHIOnboardingSceneViewModel alloc] initWithType:type];
	model.welcomeScreen = self.viewModel.isWelcome;

    EHIOnboardingScene *scene = [[EHIOnboardingScene alloc] initWithSize:self.scrollView.bounds.size model:model];

	SKView *view = [self sceneContainer];
	[view presentScene:scene];

	[self addSceneTitleToView:view];

	view.frame = CGRectOffset(view.frame, CGRectGetWidth(self.sceneRect) * self.scenes.count, 0);

    [self.scrollView addSubview:view];
    [self.scenes addObject:scene];
}

- (void)initializeView:(UIView *)view
{
    view.frame = CGRectMake(
		CGRectGetWidth(self.sceneRect) * self.scenes.count,
		0,
		CGRectGetWidth(self.view.frame),
		CGRectGetHeight(self.scrollView.frame) - EHIMediumPadding);

    [self.scrollView addSubview:view];
    [self.scenes addObject:view];
}

- (void)refreshContentSize
{
	CGSize size = (CGSize) {
		.width  = CGRectGetWidth(self.sceneRect) * self.scenes.count,
		.height = CGRectGetHeight(self.sceneRect)
	};

	[self.scrollView setContentSize:size];
}

# pragma mark - Animation

-(void)animateCar:(EHIOnboardingScrollDirection)direction
{
    CGFloat offset = self.scrollView.contentOffset.x / (CGRectGetWidth(self.scrollView.frame) * EHIOnboardingNumberOfScenes);
    
    self.carImageView.hidden = offset >= 1.0;
    
    CGFloat deltaX;
	BOOL scrollingOnLastScene = direction == EHIOnboardingScrollDirectionRight && [self nextPageIndex:direction] == EHIOnboardingNumberOfScenes - 1;
    if([self nextPageIndex:direction] >= EHIOnboardingNumberOfScenes) {
        // user scrolls to benefits scene from ferris wheel scene (scene 4)
        // limit maximum offset to 1.0 and subtract 75% to clamp scroll backward
        CGFloat maxOffset = 0.75;
        
        offset = MIN(offset, 1);
        offset -= maxOffset;
        
        // determine percentage of backward movement
        // car moves 25% / scenes for the first 4 scenes
        CGFloat percentPerScene = 0.25f;
        CGFloat percent = offset / percentPerScene;
        deltaX = (maxOffset * CGRectGetWidth(self.carImageView.frame)) * (1 - percent);
        
        self.carImageView.alpha = 1 - percent;

    } else if(scrollingOnLastScene) {
        // user scrolls from benefits to ferris wheel scene (scene 4)
        offset = 1.0f - offset;
        CGFloat percent = offset / 0.25f;
        deltaX = (CGRectGetWidth(self.carImageView.frame) * 0.75f) * percent;
        
        self.carImageView.alpha = percent;

    } else {
        // onboarding scenes (1-4)
        // clamp offsets to 0.0 - 0.75
        CGFloat maxOffset = 0.75;
        CGFloat minOffset = 0.0;
        offset = MAX(minOffset, MIN(maxOffset, offset));
        deltaX = CGRectGetWidth(self.carImageView.frame) * offset;
    }
    
    self.carCenterConstraint.constant = deltaX;
}

- (void)animateScenes:(EHIOnboardingScrollDirection)direction
{
    CGFloat offset = self.scrollView.contentOffset.x / (CGRectGetWidth(self.scrollView.frame) * EHIOnboardingNumberOfScenes);
    CGFloat sceneOffset = (offset / (1.0f / (CGFloat)EHIOnboardingNumberOfScenes)) - self.pageControl.currentPage;
    
    int currentSceneIndex = (int)self.viewModel.currentSceneIndex;

	BOOL scrollingToLastPage = direction == EHIOnboardingScrollDirectionLeft && currentSceneIndex < self.scenes.count - 1;
	BOOL scrollingToNextPage = direction == EHIOnboardingScrollDirectionRight && currentSceneIndex > 0;
    if(scrollingToLastPage){
        [self animateSceneAtIndex:currentSceneIndex+1 offset:sceneOffset];
        [self animateSceneAtIndex:currentSceneIndex offset:-sceneOffset];
    } else if(scrollingToNextPage) {
        sceneOffset = fabs(sceneOffset);
        [self animateSceneAtIndex:currentSceneIndex-1 offset:sceneOffset - 1.0f];
        [self animateSceneAtIndex:currentSceneIndex offset:1 - sceneOffset];
    }
}

- (void)animateSceneAtIndex:(int)index offset:(CGFloat)offset
{
    UIView *scene = [self.scenes ehi_safelyAccess:index];
    if([scene isKindOfClass:EHIOnboardingScene.class]) {
        [(EHIOnboardingScene *)scene updateLayerOffset:offset];
    }
}

- (void)updateSceneIndex:(int)index
{
    self.viewModel.currentSceneIndex = index;
    self.pageControl.currentPage     = index;
    [self animateSceneAtIndex:index offset:0];
}

# pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    BOOL right = [scrollView.panGestureRecognizer translationInView:scrollView.superview].x > 0;
    EHIOnboardingScrollDirection direction = right
        ? EHIOnboardingScrollDirectionRight
        : EHIOnboardingScrollDirectionLeft;
    
    [self animateCar:direction];
    [self animateScenes:direction];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    int index = (int)floor(scrollView.contentOffset.x / CGRectGetWidth(scrollView.frame));
    [self updateSceneIndex:index];
}

# pragma mark - Memory Management

- (void)dealloc
{
    [self unload];
}

- (void)unload
{
    NSArray *subviews = self.scrollView.subviews;
    
    (subviews.select(SKView.class) ?: @[]).each(^(SKView* view, int index) {
        [(EHIOnboardingScene *)view.scene unload];
        [view presentScene:nil];
        [view removeFromSuperview];
        view = nil;
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenOnboarding;
}

@end
