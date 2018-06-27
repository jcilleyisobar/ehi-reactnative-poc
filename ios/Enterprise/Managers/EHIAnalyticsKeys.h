//
//  EHIAnalyticsKeys.h
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#
# pragma mark - Shared
#

#define EHIAnalyticsActionShowModal     @"ModalLaunch"
#define EHIAnalyticsActionGetDirections @"GetDirections"
#define EHIAnalyticsActionPhone         @"Phone"
#define EHIAnalyticsActionPassthrough   @""
#define EHIAnalyticsActionEmailOptIn    @"EmailOptIn"
#define EHIAnalyticsActionEmailOptOut   @"EmailOptOut"

#
# pragma mark - Macros
#

#define EHIAnalyticsMacroEventError                     @"Error"
#define EHIAnalyticsMacroEventDashboard                 @"Dashboard"
#define EHIAnalyticsMacroEventLocations                 @"Location Search"
#define EHIAnalyticsMacroEventSelectLocation            @"Location Selected"
#define EHIAnalyticsMacroEventDateTime                  @"Date/Time Selected"
#define EHIAnalyticsMacroEventLoadClasses               @"Class Loaded"
#define EHIAnalyticsMacroEventSelectClass               @"Class Selected"
#define EHIAnalyticsMacroEventExtrasSelected            @"Extras Selected"
#define EHIAnalyticsMacroEventLoadReview                @"Review Loaded"
#define EHIAnalyticsMacroEventConfirmation              @"Confirmation"
#define EHIAnalyticsMacroEventEnrollmentLoad            @"Enrollment:Load"
#define EHIAnalyticsMacroEventEnrollmentStepOne         @"Enrollment:Step1"
#define EHIAnalyticsMacroEventEnrollmentStepTwo         @"Enrollment:Step2"
#define EHIAnalyticsMacroEventEnrollmentStepThree       @"Enrollment:Step3"
#define EHIAnalyticsMacroEventPayNowSelected            @"Review:PayNowSelected"
#define EHIAnalyticsMacroEventPayLaterSelected          @"Review:PayLaterSelected"
#define EHIAnalyticsMacroEventEnrollmentComplete        @"App:Enrollment:Complete"
#define EHIAnalyticsMacroEventRateSelected              @"RateSelected"
#define EHIAnalyticsMacroEventLocationSelectDateTime    @"Location DateTime Filter Selected"
#define EHIAnalyticsMacroEventLocationClearDateTime     @"Location DateTime Filter Unselected"

#
# pragma mark - Dashboard
#

#define EHIAnalyticsDashStateCurrent       @"Current"
#define EHIAnalyticsDashStateUpcoming      @"Upcoming"
#define EHIAnalyticsDashStateNone          @"None"
#define EHIAnalyticsDashStateUnauth        @"Unauth"

#define EHIAnalyticsDashActionSearch                    @"Search"
#define EHIAnalyticsDashActionNearby                    @"NearbyLocations"
#define EHIAnalyticsDashActionAbandonded                @"AbandondedRental"
#define EHIAnalyticsDashActionPast                      @"PastRental"
#define EHIAnalyticsDashActionFavorites                 @"Favorite"
#define EHIAnalyticsDashActionClear                     @"ClearActivity"
#define EHIAnalyticsDashActionJoin                      @"JoinNow"
#define EHIAnalyticsDashActionDetails                   @"ViewDetails"
#define EHIAnalyticsDashActionExtend                    @"ExtendRental"
#define EHIAnalyticsDashActionInstructions              @"ReturnInstructions"
#define EHIAnalyticsDashActionWkndSpecial               @"Wknd:GetStarted"
#define EHIAnalyticsDashActionRateVehicle               @"RateVehicle"
#define EHIAnalyticsDashActionFindGasStations           @"GasStation"
#define EHIAnalyticsActionRentalAssistantEnable         @"Enable"
#define EHIAnalyticsDashActionNotificationsNotNow       @"NotNow"
#define EHIAnalyticsDashActionDataCollectionContinue    @"Continue"
#define EHIAnalyticsDashActionDataCollectionChange      @"ChangeTrackingSetting"


#
# pragma mark - Weekend Special
#

#define EHIAnalyticsWkndPromoScreenModal            @"PromotionModal"
#define EHIAnalyticsWkndPromoStateModal             @"WeekendSpecial"
#define EHIAnalyticsWkndPromoModalActionLearnMore   @"LearnMore"
#define EHIAnalyticsWkndPromoModalActionClose       @"Close"

#define EHIAnalyticsWkndPromoDetailsActionStartRes  @"StartReservation"
#define EHIAnalyticsWkndPromoDetailsActionTerms     @"T&C"

#
# pragma mark - Locations
#

#define EHIAnalyticsLocActionNearby             @"FindNearby"
#define EHIAnalyticsLocActionLocation           @"SelectLocation"
#define EHIAnalyticsLocActionCity               @"SelectCity"
#define EHIAnalyticsLocActionDetail             @"LocationDetail"
#define EHIAnalyticsLocActionNoLocations        @"NoLocations"
#define EHIAnalyticsLocActionCallUs             @"CallUs"
#define EHIAnalyticsLocActionPin                @"LocationPin"
#define EHIAnalyticsLocActionModal              @"SelectLocationModal"
#define EHIAnalyticsLocActionFavorite           @"AddFav"
#define EHIAnalyticsLocActionAboutAfterHours    @"AboutAfterHours"
#define EHIAnalyticsLocActionShowHours          @"ShowHours"
#define EHIAnalyticsLocActionHideHours          @"Hidehours"
#define EHIAnalyticsLocActionModal              @"SelectLocationModal"
#define EHIAnalyticsLocationListHeader          @"LocationListHeader"
#define EHIAnalyticsSearchOpenLocations         @"SelectaDifferentLocation"

#
# pragma mark - Locations Search No Result Modal
#

#define EHIAnalyticsLocSearchNoResultActionEdit         @"EditFilters"
#define EHIAnalyticsLocSearchNoResultActionClearFilters @"ClearAllFilters"

#
# pragma mark - Reservation
#

#define EHIAnalyticsResActionDone                   @"Done"
#define EHIAnalyticsResActionSelectReturn           @"ReturnDiffLoc"
#define EHIAnalyticsResActionDeleteReturn           @"DeleteReturnLoc"
#define EHIAnalyticsResActionExpandCid              @"ExpandCID"
#define EHIAnalyticsResActionContinue               @"Continue"

#define EHIAnalyticsResActionFees                   @"TaxesAndFees"
#define EHIAnalyticsResActionModalLaunch            @"ModalLaunch"
#define EHIAnalyticsResActionSelectClass            @"SelectClass"
#define EHIAnalyticsResActionAvailability           @"CallForAvailability"
#define EHIAnalyticsResActionTotalCost              @"TotalCost"

#define EHIAnalyticsResActionShowPoints             @"ViewPoints"
#define EHIAnalyticsResActionHidePoints             @"HidePoints"
#define EHIAnalyticsResActionRedeemPoints           @"RedeemPoints"
#define EHIAnalyticsResActionSavePoints             @"SavePoints"
#define EHIAnalyticsResActionRemovePoints           @"RemovePoints"
#define EHIAnalyticsResActionRedemptionDaysMinus    @"Minus"
#define EHIAnalyticsResActionRedemptionDaysPlus     @"Plus"
#define EHIAnalyticsResActionRedemptionShowDetails  @"ShowDetails"
#define EHIAnalyticsResActionRedemptionHideDetails  @"HideDetails"

#define EHIAnalyticsResStatePaymentOptionsModal     @"PaymentsOptions:Modal"
#define EHIAnalyticsResActionPaymentOptionPayNow    @"PayNow"
#define EHIAnalyticsResActionPaymentOptionPayLater  @"PayLater"
#define EHIAnalyticsResActionPaymentOptionRedeem    @"Redeem"

#define EHIAnalyticsResActionAddPaymentMethod       @"AddPaymentMethod"
#define EHIAnalyticsResActionReviewSelectPayLater   @"SelectPayLater"
#define EHIAnalyticsResActionReviewSelectPayNow     @"SelectPayNow"
#define EHIAnalyticsResActionReviewRemoveCreditCard @"RemoveCC"

#define EHIAnalyticsResActionAddCreditCardScan      @"ScanCC"
#define EHIAnalyticsResActionAddCreditCardAdd       @"AddCard"
#define EHIAnalyticsResActionPrepayPolicy           @"PrePayPolicy"

#define EHIAnalyticsResActionSelectExtra            @"Select"
#define EHIAnalyticsResActionDeselectExtra          @"Unselect"
#define EHIAnalyticsResActionExpandExtra            @"Expand"
#define EHIAnalyticsResActionShowMore               @"ShowMore"

#define EHIAnalyticsResActionAddFlight              @"AddFlightInformation"
#define EHIAnalyticsResActionSaveFlight             @"FlightInfoSaved"
#define EHIAnalyticsResActionNoFlight               @"NoFlightInfo"
#define EHIAnalyticsResActionChangeLoc              @"ChangeLocation"
#define EHIAnalyticsResActionChangeDate             @"ChangeDate"
#define EHIAnalyticsResActionChangeVehicle          @"ChangeVehicle"
#define EHIAnalyticsResActionChangeExtras           @"ChangeExtras"
#define EHIAnalyticsResActionChangeCancel           @"ChangeCancel"
#define EHIAnalyticsResActionChangeAccept           @"ChangeContinue"
#define EHIAnalyticsResActionUpgradeNow             @"UpgradeNow"
#define EHIAnalyticsResActionBookRental             @"BookRental"

#define EHIAnalyticsResActionReturnHome             @"ReturnHome"
#define EHIAnalyticsResActionCancelReset            @"CancelReset"
#define EHIAnalyticsResActionTerms                  @"T&C"
#define EHIAnalyticsResActionCallUs                 @"CallUs"
#define EHIAnalyticsResActionConfirm                @"Yes"

#define EHIAnalyticsResActionAddQuickPickupDetails  @"AddQuickPickupDetails"
#define EHIAnalyticsResActionModify                 @"ModifyReservation"

#define EHIAnalyticsResStateSuccessful              @"Successful"
#define EHIAnalyticsResStateAbandon                 @"Abandon"
#define EHIAnalyticsResStateCancel                  @"Cancel"

#define EHIAnalyticsResCancelActionYes              @"Yes"
#define EHIAnalyticsResCancelActionNo               @"No"

#define EHIAnalyticsResChangeHeaderPayNow           @"PayNow:Header"
#define EHIAnalyticsResChangeHeaderPayLater         @"PayLater:Header"
#define EHIAnalyticsResChangeBodyPayNow             @"PayNow:Body"
#define EHIAnalyticsResChangeBodyPayLater           @"PayLater:Body"
#define EHIAnalyticsResChangePayLaterHelp           @"PayLaterHelp"

#define EHIAnalyticsResActionTCChangeLanguage       @"ModifyLanguage"
#define EHIAnalyticsResActionTCDone                 @"Done"

#define EHIAnalyticsResActionPreferredCard          @"CheckPreferredCard"
#define EHIAnalyticsResActionCreditCardTC           @"CheckT&C"

#
# pragma mark - Sign-in / Rewards
#

#define EHIAnalyticsUserStateSuccess            @"Successful"
#define EHIAnalyticsUserStateForgotEmail        @"ForgotEmail"
#define EHIAnalyticsUserStateForgotConfirmation @"ForgotConfirmation"
#define EHIAnalyticsUserStatePartialEnroll      @"completeenrollment"
#define EHIAnalyticsUserActionCreateAcc         @"CreateAccount"

#define EHIAnalyticsUserActionJoin              @"joineplus"
#define EHIAnalyticsUserActionRedeem            @"RedeemPoints"
#define EHIAnalyticsUserActionForgotPassword    @"Submit"
#define EHIAnalyticsECUserActionForgotPasswordUsername @"ForgotPWUsername"
#define EHIAnalyticsUserActionEnrollment        @"Enrollment"

#define EHIAnalyticsUserActionRememberOn        @"RememberOn"
#define EHIAnalyticsUserActionRememberOff       @"RememberOff"

#
# pragma mark - Enrollment
#

#define EHIAnalyticsEnrollmentNone                  @"None"
#define EHIAnalyticsEnrollmentNext                  @"Next"
#define EHIAnalyticsEnrollmentContinue              @"Continue"
#define EHIAnalyticsEnrollmentJoin                  @"Join"
#define EHIAnalyticsEnrollmentPromotionalEmail      @"PromoEmailBox"
#define EHIAnalyticsEnrollmentTerms                 @"T&CBox"
#define EHIAnalyticsEnrollmentAdditionalInfo        @"T&CAdditionalInfo"
#define EHIAnalyticsEnrollmentLearnMore             @"LearnMore"
#define EHIAnalyticsEnrollmentScanLicense           @"ScanLicense"
#define EHIAnalyticsEnrollmentProfileNoMatch        @"NoMatch"
#define EHIAnalyticsEnrollmentProfileNonLoyalty     @"PF:NonLoyalty"
#define EHIAnalyticsEnrollmentProfileEmerald        @"PF:EmeraldClub"
#define EHIAnalyticsEnrollmentProfileEPlus          @"EPMatch"
#define EHIAnalyticsEnrollmentKeepAddress           @"KeepAddress"
#define EHIAnalyticsEnrollmentChangeAddress         @"ChangeAddress"

#
# pragma mark - Survey
#

#define EHIAnalyticsSurveyActionYes           @"Yes"
#define EHIAnalyticsSurveyActionNo            @"No"
#define EHIAnalyticsSurveyActionBack          @"Back"
#define EHIAnalyticsSurveyActionSend          @"SendSurvey"
#define EHIAnalyticsSurveyActionReadPolicy    @"PrivacyPolicy"

#
# pragma mark - Reward Benefits - Auth
#

#define EHIAnalyticsRewardBenefitsAuthActionAboutPoints     @"AboutPoints"
#define EHIAnalyticsRewardBenefitsAuthActionRes             @"StartReservation"
#define EHIAnalyticsRewardBenefitsAuthProgramDetails        @"EPProgramDetails"

#define EHIAnalyticsRewardBenefitsAuthActionTerms           @"T&C"
#define EHIAnalyticsRewardBenefitsAuthActionRequest         @"CallRequestPoints"
#define EHIAnalyticsRewardBenefitsAuthActionTransfer        @"TransferPoints"
#define EHIAnalyticsRewardBenefitsAuthActionHistory         @"ViewPointsHistory"

#define EHIAnalyticsRewardBenefitsAuthActionPlusTier        @"ExpandCollapsePlusTier"
#define EHIAnalyticsRewardBenefitsAuthActionSilverTier      @"ExpandCollapseSilverTier"
#define EHIAnalyticsRewardBenefitsAuthActionGoldTier        @"ExpandCollapseGoldTier"
#define EHIAnalyticsRewardBenefitsAuthActionPlatinumTier    @"ExpandCollapsePlatinumTier"
#define EHIAnalyticsRewardBenefitsAuthActionProgramDetails  @"ProgramDetails"

#
# pragma mark - Reward Benefits - Unauth
#

#define EHIAnalyticsRewardBenefitsUnauthActionSignIn        @"SignIn"
#define EHIAnalyticsRewardBenefitsUnauthActionJoin          @"Join"
#define EHIAnalyticsRewardBenefitsUnauthActionLearnMore     @"AboutEP"
#define EHIAnalyticsRewardBenefitsUnauthAnimation1          @"Home:Animation1"
#define EHIAnalyticsRewardBenefitsUnauthAnimation2          @"Home:Animation2"
#define EHIAnalyticsRewardBenefitsUnauthAnimation3          @"Home:Animation3"
#define EHIAnalyticsRewardBenefitsUnauthAnimation4          @"Home:Animation4"
#define EHIAnalyticsRewardBenefitsUnauthAnimation5          @"Home:Animation5"
#define EHIAnalyticsRewardBenefitsUnauthAnimation6          @"Home:Animation6"

#
# pragma mark - Menu
#

#define EHIAnalyticsMenuActionWkndSpecial @"MenuWkndPromo:GetStarted"

#define EHIAnalyticsMenuActionShow        @"MenuShow"
#define EHIAnalyticsMenuActionHide        @"MenuHide"
#define EHIAnalyticsMenuActionDashboard   @"MenuHome"
#define EHIAnalyticsMenuActionRewards     @"MenuRewards"
#define EHIAnalyticsMenuActionRentals     @"MenuMyRentals"
#define EHIAnalyticsMenuActionLocations   @"MenuLocations"
#define EHIAnalyticsMenuActionStartRental @"MenuStartRental"
#define EHIAnalyticsMenuActionSupport     @"MenuCustomerSupport"
#define EHIAnalyticsMenuActionSettings    @"MenuSettings"
#define EHIAnalyticsMenuActionFeedback    @"MenuFeedback"
#define EHIAnalyticsMenuActionProfile     @"MenuMyProfile"
#define EHIAnalyticsMenuActionSignIn      @"MenuSignIn"
#define EHIAnalyticsMenuActionSignOut     @"MenuSignOut"

#
# pragma mark - My Rentals
#

#define EHIAnalyticsRentalsActionLookup    @"Lookup"
#define EHIAnalyticsRentalsActionReceipt   @"ViewReceipt"
#define EHIAnalyticsRentalsActionFind      @"FindRental"
#define EHIAnalyticsRentalsActionContactUs @"ContactUs"
#define EHIAnalyticsRentalsActionSignIn    @"SignIn"

#define EHIAnalyticsRentalsStateUnauth     @"Unauth"
#define EHIAnalyticsRentalsStateUpcoming   @"UpcomingRentals"
#define EHIAnalyticsRentalsStatePast       @"PastRentals"

#
# pragma mark - Profile
#

#define EHIAnalyticsProfileActionAddCreditCard      @"AddCreditCard"
#define EHIAnalyticsProfileActionChangePassword     @"ChangePassword"
#define EHIAnalyticsProfileActionEditPaymentOptions @"EditPaymentOptions"

#
# pragma mark - Add Credit Card
#

#define EHIAnalyticsProfileActionSavePreferredCard  @"SaveCard"
#define EHIAnalyticsProfileActionTermsAndConditions @"T&CCheck"

#
# pragma mark - Edit Payments
#

#define EHIAnalyticsEditPaymentsActionEditBilling       @"EditBillingNumber"
#define EHIAnalyticsEditPaymentsActionRemoveBilling     @"RemoveBillingNumber"

#define EHIAnalyticsEditPaymentsActionEditCreditCard    @"EditCreditCard"
#define EHIAnalyticsEditPaymentsActionRemoveCreditCard  @"RemoveCreditCard"

#define EHIAnalyticsEditPaymentMethodActionDefault      @"CheckboxDefaultPayment"
#define EHIAnalyticsEditBillingNumberActionSave         @"SaveChanges"

#define EHIAnalyticsEditCreditCardActionSave            @"Save"
#define EHIAnalyticsEditCreditCardActionEditDate        @"EditDate"

#define EHIAnalyticsDeleteBillingModalActionDelete      @"Delete"
#define EHIAnalyticsDeleteBillingModalActionClose       @"Close"

#define EHIAnalyticsLocationLockedModalActionCallUs     @"CallUs"
#define EHIAnalyticsLocationLockedModalActionClose      @"Close"

#define EHIAnalyticsLocationLockedModalActionLock       @"Lock"

#
# pragma mark - Receipt
#

#define EHIAnalyticsReceiptActionSaveToPhotos   @"SaveToPhotos"
#define EHIAnalyticsReceiptActionPhoneLink      @"PhoneLink"

#
# pragma mark - Welcome
#

#define EHIAnalyticsWelcomeScreen       @"Welcome"
#define EHIAnalyticsWelcomeState        @"Home"
#define EHIAnalyticsWelcomeActionSignin @"Signin"
#define EHIAnalyticsWelcomeActionSkip   @"SkipContinue"
#define EHIAnalyticsWelcomeActionJoin   @"Join Now"

#
# pragma mark - Corp Flow
#

#define EHIAnalyticsCorpFlowActionSignin   @"CorpResLogin"
#define EHIAnalyticsCorpFlowActionPurpose  @"CorpResPurpose"
#define EHIAnalyticsCorpFlowActionProfile  @"CorpResProfileError"
#define EHIAnalyticsCorpFlowActionDelivery @"CorpResDeliveryInfo"

#define EHIAnalyticsCorpFlowActionSubmitAdditionInfo  @"Submit"
#define EHIAnalyticsCorpFlowActionSubmitPIN           @"Submit"

#
# pragma mark - Confirmation Join Modal
#

#define EHIAnalyticsConfirmationJoinActionJoin          @"Join"
#define EHIAnalyticsConfirmationJoinActionAddCalendar   @"AddToCalendar"

#
# pragma mark - Filters
#

#define EHIAnalyticsActionApplyFilter       @"ApplyFilter"
#define EHIAnalyticsActionResetFilter       @"ResetFilter"
#define EHIAnalyticsActionClearAllFilters   @"ClearAllFilters"
#define EHIAnalyticsActionClearDate         @"DateUnselect"
#define EHIAnalyticsActionClearTime         @"TimeUnselect"

#define EHIAnalyticsActionSelectDate         @"DateSelect"
#define EHIAnalyticsActionSelectTime         @"TimeSelect"

#
# pragma mark - Call Support
#

#define EHIAnalyticsActionCallSupport @"CallSupport"

#
# pragma mark - Customer Support
#

#define EHIAnalyticsStateCustomerSupportCall             @"CallEnterprise"
#define EHIAnalyticsStateCustomerSupportMoreHelp         @"MoreHelp"
#define EHIAnalyticsActionCustomerService                @"CustomerService"
#define EHIAnalyticsActionRoadsideAssistance             @"RoadsideAssistance"
#define EHIAnalyticsActionEnterprisePlus                 @"EnterprisePlus"
#define EHIAnalyticsActionCustomersDisabilities          @"CustomersDisabilities"
#define EHIAnalyticsActionSendMessage                    @"SendMessage"
#define EHIAnalyticsActionFAQs                           @"FAQs"

#
# pragma mark - Notifications
#

#define EHIAnalyticsNotificationState               @"Prompt"
#define EHIAnalyticsActionNotificationAllow         @"Allow"
#define EHIAnalyticsActionNotificationDontAllow     @"DontAllow"

#define EHIAnalyticsActionNotificationRemindPickup  @"RemindPU"
#define EHIAnalyticsActionNotificationRemindDropOff @"RemindDO"

#define EHIAnalyticsActionNotificationDashboard             @"Dashboard"
#define EHIAnalyticsActionNotificationCallBranch            @"Call"
#define EHIAnalyticsActionNotificationGetDirections         @"Directions"
#define EHIAnalyticsActionNotificationFindGasStations       @"GasStations"
#define EHIAnalyticsActionNotificationAfterHourInstructions @"ReturnInstructions"
#define EHIAnalyticsActionNotificationTerminalDirections    @"TerminalDirections"

#
# pragma mark - Settings
#

#define EHIAnalyticsSettingsActionDataCollection            @"DataCollection"
#define EHIAnalyticsSettingsActionSaveSearchHistory         @"SaveSearchHistory"
#define EHIAnalyticsSettingsActionPreferredCreditCard       @"PreferredCreditCard"
#define EHIAnalyticsSettingsActionAutoSave                  @"AutoSave"
#define EHIAnalyticsSettingsActionCleanPersonalData         @"ClearPersonalData"
#define EHIAnalyticsSettingsActionCleanHistoricalData       @"ClearHistoricalData"
#define EHIAnalyticsSettingsActionCleanHistoricalDataYes    @"ClearHistoricalDataYES"
#define EHIAnalyticsSettingsActionCleanHistoricalDataNo     @"ClearHistoricalDataNO"

#
# pragma mark - Debug
#

#define EHIAnalyticsDebugMenu @"Debug"
#define EHIAnalyticsDebugMenuPushNotificationEvent @"PushNotificationMarker"
