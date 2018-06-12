package com.ehi.enterprise.android.utils.analytics;

public class EHIAnalytics {

    /*

                                                                            ......up
                                                              .....JeY757=?`....Jg F
                                                .....xvp  #71.....,JFJMMMMM JMMMMM F
                                  ..Y=775a.   F.....gq F  F MMMMMd:JFJMMMMM JMMMMM F
                   ......        .5.MMMMN,,h. F.MMMMMd F  F MMMMMd:JFJMMMMM JMMMNM F
              M7=?i.....,Ta.    J`JMMMMMMMN,4,F.MMMMMd F  F MMMMMd;JFJTMMM%. MM#F.Y5
              M MMMMMMMMMm,?,  J'JMMMMMMMMMN,4F.MMMMMd F  b ?dMMM eTYM WMNb .MMd%J
              M MMMMMMMMMMM,J2.F.MMMMMMMMMMMN b. MM#F.BY  ?d JMMM F  JrJMMM.JM##.F
              M MMMMMMMMMMMM,4d`JMMMM=.,,MMMMrJM MM#FJF    J JMNM F   N NMNNMMM$J
              M 7XMMM ,.7MMMN F MMMM @ Jr,MMNN M MM#FJF    J JM#M F   J,JMMMM#M.F
              ?4:JMMM F?,,MMMr .MMNFJ`  4 dMMM.M MM#FJF    J JM#M F    b MMMMMFJ`
               J:JMMM F d.dMMb JMMM`M   JrJMMM2J MM#FJF    J JM#M F    J,JMM#M F
               J:JMMM F .F.MMM JMMM N    F.MMMFJ MM#FJF    J JM#M b..,.db MMMFJ%
               J:JMMM F  F MMM JMMM.W   .FJMMMFJ MM#FJb,.zTW JM#M M%.Je.F MMMFJ+.
               J:JMMM F  F MMM .MMMFJ,  J!JMMMFJ MM#FJM gdrJ JMNM M MN#.$ MMMb. F
               J:JMMM F .F.MMM  MMMN h .F.MMMd`M MM#FJF.MMF, JMNM...M## MMMMM#M F
               J:JMMM F J`JMM#..JMMMb 5Y.MMM## ?.MMNh.gdMMF.MMMMMMMM### dMMMN#M F
               J:JMMM bJ',MMMFJb.MMMMMNMMMMMMF JMMMMMMMMMMF.MMMMMMMMM## dMMMM#M F
              gT!JMMM...MMMMM FJ,?MMMMMMMMMN@.FJMMMMMMMMMMF.MMNMMMMMMN# TTTY7=i.F
              M MMMMMMMMMMMN$J` 4,?MMMMMMMM#.MFJMMMMMMNMkMF YY7=1&..,&vYTY77?`
              M MMMMMMMMMMN5J^   T,,HMMMMK5.DJF,71?....&vYT=77?`
              M MMMMMMMMRD.J!     .5u,11.,Y` .7??
              M WYY771&.,Y!
              W7Y7=?!

                                             ``..
                                           ``.
                                          ``.-`
                                          ``.-.
                                         ``..-::`
                                 `-:/+oooooooo/`
                                 `.:ssyyo++oo+/`
                                    -+ossssssso/:::--.``
                                  `-oysyyyysoossyyyyyyso+:-`
                               .:+syyyyyyyyyhyhhhhhhhhyyyso+/-`
                            `-+syyyyyhhhhhyyyyyyyyyyyyyyyso+++/-
                           .+yyssyyyyyyyyyyyyyyyyyyyyyssso++++++:`
                          :oyyssssssssyyyyysssooooossoo++++++++++:
                         :+syyyyyyyyyyyyyyysoo++ooossssooooo++++++-
                        .+++osyyyyyyyyyyysso+//++oooossssssooo++++/`
                        -+///+osyyyyyyyssso+////++ooosssssssooo++++-
                        .+:::/++oossssssssso++/+oossooooooosssoo++o/
                        `+/::/++syyyyyyyyyyyssossoooo+:---.-/oooo+++.
          ..             //::/++syyyyyyyyyyyyyyssooo:-.`/ss-`.+oo++o:
        `/+:`            -+///+oyyys+:.-+++syyyssso/--ssdNNh/ `+oo++/
        :o/.`             /o+/+oyy+:-+/smds-/yys+oo/--ymNNNNd. :oo+++`
        -o/-`             .so+osys:-:smNNNmy`/ss++o+--+ydmmho.`/oo+++-
        `o+:`              :yossso/::sdNNNNd.`+oo+o+/:::+os:../ooooo+:
 .//-..-+o/:.`              ososss+/::+shh++`-oyyso++o+/////+ossooooo+
`so+/-.../+:.`              .soossoso+////::/ososyoooo++osyyssssoooooo
-hys+//////:.`               :yyyyyyyyysoooooosyyyyoooosssyyyyssssoos+
+ss+/-...:/:.`````...``       oyyyyyhhyyyyshhhysysooossyhdhsyyyysssss-
/yys+:::///-````/++///+/::---:/shhhhhhhhhyyhdmmmmh++/dNNdsssyyyyyssso`
 +ss+:....-.`.-:oooooo+++++ooo++osyyyhhhhhhysydmNmhdhhdyooosyyssssos/
 `ohs+///:--.:+yyysooooooo+oooo++++oyyhhhhyyysssyyhhysooooossssso+/s.                 ``
   `.--::-.`   .+hhyyssooooooooooooo+oyyhyyyys+//oysyssooooo+/o..`/s/               `-:.`
                 `:+shhyysssssooooooosossyyso+///+/:::+/:-oo``o..:sss/.```        `-++:.
                     `-/oyyhhyyyyyysys+/++/``` `:- `. `+`.oo--o-.+ssoo+oo++//:-``:/++-`````.--..```
                          `.--:/shhhyo+:::-`/+ .` ``.``+`:/+`/+.//ssoo++/++++++:-:/++::----:+////:-`
                                 .shs++:.-.:/:-. `.`. :--/:--:--:oysoooooossoo///+ososyyso-.......`
                                  .yo++...-+/:/``.```.-.--..-::+osssssssso++/:.:+yys++++/..-:::-`
                                  `so+/:-.--:+/`` `.:.-://+ooooooo````````       .:++/:-.` `.-:/:.
                                  .s+++/.-:+sss/:/+oooosssoooooooo-                      `.-.`
                                  -so+oooosyyyyyyyyyyyyssssooooooo/                       `-:.
                                  :sssyyyhhhhhhhhhhhyyyyyysssoooooo`
                                  +ssyyhhhhhhhhhhhhhhhhhhhysssooooo.
                                  sssyhhhyhhhhhhhhhhhhhhhhyysssoooo:
                                 -ysyyhhhyhhhhhhhhhhhhhhhhyyyyssooo+
                                 +syyhhhyhhhhhhhhhhyhhhhhhhyyyssoooo.
                                `ooyhhyyhhhhhhyo:-...-+yhhhhyyyssooo:
                                -++oyyyhhhhhs/`        .+yhhyyysoooo+`
                                +sssysyyyyy+`            :yyyyyyysooo.
                                +oyhyyysss+`              /yyyyyysooo.
                                ++syyysso+`               `+yyysooooo.
                                ooyyysso+.                 `oyyysssoo.
                              ..:syyyyso.                   `+sssso/:`.
                            ./so/:::::---                   ./::::::/+o/.
                          `:o++ooosooooo+                   /ssssoo+////+:`
                         `+oooo++//+ssss/                   +yyso++++////+/`
                        .osoo+++/+oossss.                   -yyyso+++oooooo+`
                       `osssssssoo+oss/`                     .oysssssooooooo+
                       /yyyysssssssso`                         /yysssssssssss-
                       shyyyyysssssy-                          `yyyyyyyyyysyy/
                       -yhhhhyyyyys-                            .oyhhhhhhhys/`
                        `:osyyyso-                                .:/+++/:-`

     */

    public static final String NOT_AVAILABLE = "Not available";

    public enum CustomDimensions {
        CUSTOM_DIMENSION_LANGUAGE,
        CUSTOM_DIMENSION_LOCATION_SERVICE,
        CUSTOM_DIMENSION_APP_COUNTRY,
        CUSTOM_DIMENSION_LOYALTY_TIER,
        CUSTOM_DIMENSION_LOYALTY_POINT,
        CUSTOM_DIMENSION_LOYALTY_ACCOUNT_TYPE,
        CUSTOM_DIMENSION_CUSTOMER_ID,
        CUSTOM_DIMENSION_VISITOR_TYPE,
        CUSTOM_DIMENSION_SESSION_SOURCE,
        CUSTOM_DIMENSION_PAY_SYSTEM_ENABLED, // can be Android or Samsung Pay
        CUSTOM_DIMENSION_PUSH_NOTIFICATION_ENABLED
    }

    public enum MacroEvent {
        MACRO_LOCATION_SEARCH("Location Search"),
        MACRO_LOCATION_SELECTED("Location Selected"),
        MACRO_DATE_TIME_SELECTED("Date/Time Selected"),
        MACRO_CLASS_LOADED("Class Loaded"),
        MACRO_CHOOSE_YOUR_RATE("ChooseRateLoad"),
        MACRO_RATE_SELECTED("RateSelected"),
        MACRO_CLASS_SELECTED("Class Selected"),
        MACRO_EXTRAS_SELECTED("Extras Selected"),
        MACRO_REVIEW_LOADED("Review Loaded"),
        MACRO_CONFIRMATION("Confirmation"),
        MACRO_ENROLLMENT_LOAD("Enrollment:Load"),
        MACRO_ENROLLMENT_STEP_1("Enrollment:Step1"),
        MACRO_ENROLLMENT_STEP_2("Enrollment:Step2"),
        MACRO_ENROLLMENT_STEP_3("Enrollment:Step3"),
        MACRO_REVIEW_PAYNOW("Review:PayNowSelected"),
        MACRO_REVIEW_PAYLATER("Review:PayLaterSelected"),
        MACRO_ERROR("Error"),
        MACRO_ENROLLMENT_COMPLETE("App:Enrollment:Complete"),
        MACRO_FILTER_DATE_TIME_SELECTED("Location DateTime Filter Selected"),
        MACRO_FILTER_DATE_TIME_UNSELECTED("Location DateTime Filter Unselected");

        public final String value;

        MacroEvent(final String newValue) {
            value = newValue;
        }
    }

    public enum Screen {
        SCREEN_WELCOME("Welcome"),
        SCREEN_DASHBOARD("Dashboard"),
        SCREEN_LOCATIONS("Locations"),
        SCREEN_MENU("Menu"),
        SCREEN_ERROR("Error"),
        SCREEN_SIGN_IN("Signin"),
        SCREEN_EPLUS("ePlus"),
        SCREEN_MY_PROFILE("MyProfile"),
        EDIT_PAYMENTS("Edit Payments"),
        BILLING_CODE_DELETION("BillingCodeDeleteValidation"),
        CREDIT_CARD_DELETION("CreditCardDeletionValidation"),
        TERMS_AND_CONDITIONS("TermsandConditions"),
        LOCATION_CANT_BE_MODIFIED("LocationCantBeModified"),
        EDIT_BILLING_NUMBER("EditBillingNumber"),
        EDIT_CREDIT_CARD("EditCreditCard"),
        SCREEN_SETTINGS("Settings"),
        SCREEN_MY_RENTALS("MyRentals"),
        SCREEN_RESERVATION("Reservation"),
        SCREEN_SELECT_CREDIT_CARD("SelectCreditCard"),
        SCREEN_DATE_TIME("DateTime"),
        SCREEN_CLASS("Class"),
        SCREEN_EXTRAS("Extras"),
        SCREEN_CORP_RES("CorpRes"),
        SCREEN_PHONE("Phone"),
        SCREEN_NOTIFICATION("Notification"),
        SCREEN_RATES("Rates"),
        SCREEN_LOCATION("Location"),
        SCREEN_PROMOTION_MODAL("PromotionModal"),
        SCREEN_PROMOTION_DETAILS("PromotionDetail"),
        SCREEN_DATA_COLLECTION_REMINDER("DataCollectionReminderModal"),
        SCREEN_CORPORATE("Corporate"),
        SCREEN_ENROLL_STEP_1("EnrollModalStep1"),
        SCREEN_ENROLL_STEP_2("EnrollModalStep2"),
        SCREEN_ENROLL_STEP_3("EnrollModalStep3"),
        SCREEN_ENROLL_LONG_FORM("EnrollLongForm"),
        SCREEN_ENROLL_CONFIRMATION("EnrollModalConfirmation"),
        SCREEN_REWARDS("RewardsandBenefits"),
        SCREEN_REWARDS_UNAUTH("RewardsUnauth"),
        SCREEN_REWARDS_AUTH("RewardsAuth"),
        SCREEN_FORESEE("Foresee"),
        SCREEN_HELP_CUSTOMER_SUPPORT("Help&CustomerSupport"),
        SCREEN_DEBUG("Debug");

        public final String value;

        Screen(final String newValue) {
            value = newValue;
        }
    }

    public enum ScreenUrl {
        SCREEN_URL_ERROR("ErrorDialog");

        public final String value;

        ScreenUrl(final String newValue) {
            value = newValue;
        }
    }

    public enum State {
        STATE_PROMPT("Prompt"),
        STATE_NOTIFICATION("Notification"),
        STATE_UNAUTH("Unauth"),
        STATE_UPCOMING("Upcoming"),
        STATE_CURRENT("Current"),
        STATE_NONE("None"),
        STATE_SEARCH("Search"),
        STATE_SEARCH_FILTER("SearchFilter"),
        STATE_SEARCH_FILTER_NO_RESULTS("SearchFilterNoResults"),
        STATE_SEARCH_NO_LOCATIONS("Search:NoLocations"),
        STATE_MAP("Map"),
        STATE_LIST("List"),
        STATE_HOME("Home"),
        STATE_CANCELLATION("Cancellation"),
        STATE_WAYFINDING("WayFinding"),
        STATE_POLICY("Policy"),
        STATE_POLICIES("Policies"),
        STATE_SCREEN("Screen"),
        STATE_SYSTEM("System"),
        STATE_VALIDATION("Validation"),
        STATE_FORGOT_PASSWORD("ForgotPassword"),
        STATE_FORGOT_EMAIL("ForgotEmail"),
        STATE_EC_SIGN_IN("ECSignin"),
        STATE_JOIN_PLUS("JoinePlus"),
        STATE_SUCCESSFUL("Successful"),
        STATE_REWARD_PROGRESS("RewardsProgress"),
        STATE_EDIT_MEMBER_INFO("EditMemberInfo"),
        STATE_EDIT_DRIVER_INFO("EditDriverInfo"),
        STATE_UPCOMING_RENTALS("UpcomingRentals"),
        STATE_CURRENT_RENTALS("CurrentRentals"),
        STATE_PAST_RENTALS("PastRentals"),
        STATE_LOOKUP_RENTAL("LookupRental"),
        STATE_WIDGET("Widget"),
        STATE_CALENDAR("Calendar"),
        STATE_TIME_SELECT("TimeSelect"),
        STATE_LIST_CURRENCY("ListCurrency"),
        STATE_DETAILS_CURRENCY("DetailsCurrency"),
        STATE_FILTER("Filter"),
        STATE_FEES("Fees"),
        STATE_SUMMARY("Summary"),
        STATE_REVIEW("Review"),
        STATE_FLIGHT_INFO("FlightInfo"),
        STATE_DRIVER_INFO("DriverInfo"),
        STATE_CONFIRMATION("Confirmation"),
        STATE_ABANDON("Abandon"),
        STATE_LOGIN_MODAL("LoginModal"),
        STATE_PURPOSE_MODAL("PurposeModal"),
        STATE_PROFILE_ERROR_MODAL("ProfileErrorModal"),
        STATE_COLLECT_DELIVERY_INFO("CollectDeliveryInfo"),
        STATE_DETAILS("Details"),
        STATE_CHANGE_PASSWORD("ChangePassword"),
        STATE_MODAL("Modal"),
        STATE_CHOOSE_RATE("ChooseRate"),
        STATE_ADD_CREDIT_CARD("AddCreditCard"),
        STATE_CREDIT_CARD_DETAILS("CCDetails"),
        STATE_PAYMENT_METHOD("PaymentMethod"),
        STATE_REDEEM("Redeem"),
        STATE_WEEKEND_SPECIAL("WeekendSpecial"),
        STATE_WEEKEND_PROMO("WeekendPromo"),
        STATE_PAYMENT_OPTIONS_MODAL("PaymentOptions:Modal"),
        STATE_DATA_COLLECTION_REMINDER("DataCollectionReminder"),
        STATE_PIN("Pin"),
        STATE_NO_MATCH("NoMatch"),
        STATE_NON_LOYALTY("PF:NonLoyalty"),
        STATE_EMERALD_CLUB("PF:EmeraldClub"),
        STATE_EP_MATCH("EPMatch"),
        STATE_ABOUT_TIER_BENEFITS("AboutTierBenefits"),
        STATE_ABOUT_POINTS("AboutPoints"),
        STATE_PROGRAM_DETAILS("EPProgramDetails"),
        STATE_HOME_ANIMATION1("Home:Animation1"),
        STATE_HOME_ANIMATION2("Home:Animation2"),
        STATE_HOME_ANIMATION3("Home:Animation3"),
        STATE_HOME_ANIMATION4("Home:Animation4"),
        STATE_HOME_ANIMATION5("Home:Animation5"),
        STATE_HOME_ANIMATION6("Home:Animation6"),
        STATE_SURVEY_INVITE("SurveyInvite"),
        STATE_BANNER("Banner"),
        STATE_ADDITIONAL_INFO("AdditionalInfo"),
		STATE_RECEIPT("PastTripsReceipt"),
		STATE_CALL_ENTERPRISE("CallEnterprise"),
		STATE_MORE_HELP("MoreHelp"),
        STATE_PUSH_NOTIFICATION_MARKER("PushNotificationMarker"),
        STATE_ENROLL_MODAL("Confirmation:EnrollModal");

        public final String value;

        State(final String newValue) {
            value = newValue;
        }
    }

    public enum Motion {
        MOTION_TAP("Tap"),
        MOTION_SWIPE("Swipe"),
        MOTION_TYPE("Type"),
        MOTION_NONE("");

        public final String value;

        Motion(final String newValue) {
            value = newValue;
        }
    }

    public enum Action {
        ACTION_ALLOW("Allow"),
        ACTION_DO_NOT_ALLOW("DontAllow"),
        ACTION_NOTIFICATION_REMIND_PICKUP("RemindPU"),
        ACTION_NOTIFICATION_REMIND_DROPOFF("RemindDO"),
        ACTION_REUSE("Reuse"),
        ACTION_FAVORITE("Favorite"),
        ACTION_SEARCH_BOX("SearchBox"),
        ACTION_CLEAR_ACTIVITY("ClearActivity"),
        ACTION_JOIN_NOW("JoinNow"),
        ACTION_VIEW_DETAILS("ViewDetails"),
        ACTION_GET_DIRECTIONS("GetDirections"),
        ACTION_EXTEND_RENTAL("ExtendRental"),
        ACTION_RETURN_INSTRUCTIONS("ReturnInstructions"),
        ACTION_NEARBY("Nearby"),
        ACTION_LOCATION("Location"),
        ACTION_CITY("City"),
        ACTION_LOCATION_DETAIL("LocationDetail"),
        ACTION_LOCATION_DETAIL_MODAL("LocationDetailModal"),
        ACTION_APPLY_FILTER("ApplyFilter"),
        ACTION_CALL_US("CallUs"),
        ACTION_LOCATION_PIN("LocationPin"),
        ACTION_SELECT_LOCATION("SelectLocation"),
        ACTION_SELECT_LOCATION_MODAL("SelectLocationModal"),
        ACTION_SIGN_IN("Signin"),
        ACTION_SKIP_CONTINUE("SkipContinue"),
        ACTION_ADD_FAVORITE("AddFav"),
        ACTION_HOME("Home"),
        ACTION_MENU_HOME("MenuHome"),
        ACTION_MENU_EPLUS_REWARDS("MenuEPlusRewards"),
        ACTION_MENU_MY_RENTALS("MenuMyRentals"),
        ACTION_MENU_LOCATIONS("MenuLocations"),
        ACTION_MENU_START_RENTAL("MenuStartRental"),
        ACTION_MENU_SIGNIN("MenuSignin"),
        ACTION_MENU_CUSTOMER_SUPPORT("MenuCustomerSupport"),
        ACTION_MENU_SETTING("MenuSettings"),
        ACTION_PHONE("Phone"),
        ACTION_MENU_MY_PROFILE("MenuMyProfile"),
        ACTION_MENU_SIGN_OUT("MenuSignOut"),
        ACTION_MENU_SHARE_FEEDBACK("MenuFeedback"),
        ACTION_REDEEM_POINTS("RedeemPoints"),
        ACTION_LOOKUP("Lookup"),
        ACTION_FIND_RENTAL("FindRental"),
        ACTION_VIEW_RECEIPT("ViewReceipt"),
        ACTION_RETURN_DIFF_LOCATION("ReturnDiffLoc"),
        ACTION_DELETE_RETURN_LOCATION("DeleteReturnLoc"),
        ACTION_EXPAND_CID("ExpandCID"),
        ACTION_VIEW_CONTINUE("ViewContinue"),
        ACTION_SELECT_CLASS("SelectClass"),
        ACTION_TOTAL_COST("TotalCost"),
        ACTION_SELECT("Select"),
        ACTION_UNSELECT("Unselect"),
        ACTION_EXPAND("Expand"),
        ACTION_DONE("Done"),
        ACTION_BOOK_RENTAL("BookRental"),
        ACTION_RETURN_HOME("ReturnHome"),
        ACTION_CANCEL_RESET("CancelReset"),
        ACTION_YES("Yes"),
        ACTION_NO("NO"),
        ACTION_BACK("Back"),
        ACTION_MODAL_LAUNCH("ModalLaunch"),
        ACTION_TERMS("T&C"),
        ACTION_CONTACT_US("ContactUs"),
        ACTION_CALL_FOR_AVAILABILITY("CallForAvailability"),
        ACTION_TAXES_AND_FEES("TaxesAndFees"),
        ACTION_ADD_FLIGHT_INFO("AddFlightInformation"),
        ACTION_FLIGHT_INFO_SAVED("FlightInfoSaved"),
        ACTION_NO_FLIGHT_INFO("NoFlightInfo"),
        ACTION_CHANGE_LOCATION("ChangeLocation"),
        ACTION_CHANGE_DATE("ChangeDate"),
        ACTION_CHANGE_VEHICLE("ChangeVehicle"),
        ACTION_CHANGE_EXTRA("ChangeExtra"),
        ACTION_CHANGE_CANCEL("ChangeCancel"),
        ACTION_CHANGE_PASSWORD("ChangePassword"),
        ACTION_CHANGE_CONTINUE("ChangeContinue"),
        ACTION_UPGRADE_NOW("UpgradeNow"),
        ACTION_CALL("Call"),
        ACTION_ADD_QUICK_PICKUP_DETAILS("AddQuickPickupDetails"),
        ACTION_MODIFY_RESERVATION("ModifyReservation"),
        ACTION_MODIFY_LANGUAGE("ModifyLanguage"),
        ACTION_SHOW_POINTS("ShowPoints"),
        ACTION_HIDE_POINTS("HidePoints"),
        ACTION_SAVE_POINTS("SavePoints"),
        ACTION_REMOVE_POINTS("RemovePoints"),
        ACTION_MINUS("Minus"),
        ACTION_PLUS("Plus"),
        ACTION_SHOW_DETAILS("ShowDetails"),
        ACTION_HIDE_DETAILS("HideDetails"),
        ACTION_SHOW_MORE("ShowMore"),
        ACTION_SHOW_MENU("MenuShow"),
        ACTION_HIDE_MENU("MenuHide"),
        ACTION_PAY_NOW("PayNow"),
        ACTION_PAY_LATER("PayLater"),
        ACTION_REDEEM("Redeem"),
        ACTION_SCAN_CC("ScanCC"),
        ACTION_EDIT_CC_DATE("EditDate"),
        ACTION_SAVE_CC("Save"),
        ACTION_CHECK_DEFAULT_PAYMENT("CheckboxDefaultPayment"),
        ACTION_SAVE_BILLING_NUMBER("SaveChanges"),
        ACTION_ADD_CARD("AddCard"),
        ACTION_EDIT_BILLING_NUMBER("EditBillingNumber"),
        ACTION_REMOVE_BILLING_NUMBER("RemoveBillingNumber"),
        ACTION_EDIT_CREDIT_CARD("EditCreditCard"),
        ACTION_REMOVE_CREDIT_CARD("RemoveCreditCard"),
        ACTION_ADD_PAYMENT_METHOD("AddPaymentMethod"),
        ACTION_ADD_CREDIT_CARD("AddCreditCard"),
        ACTION_EDIT_PAYMENT_OPTIONS("EditPaymentOptions"),
        ACTION_SAVE_CARD("SaveCard"),
        ACTION_TERMS_CHECK("T&CCheck"),
        ACTION_CHECK_TERMS("CheckT&C"),
        ACTION_CHECK_PREFERRED_CARD("CheckPreferredCard"),
        ACTION_CONTINUE("Continue"),
        ACTION_EDIT_CARD("EditCard"),
        ACTION_SELECT_PAY_LATER("SelectPayLater"),
        ACTION_SELECT_PAY_NOW("SelectPayNow"),
        ACTION_REMOVE_CC("RemoveCC"),
        ACTION_PRE_PAY_POLICY("PrePayPolicy"),
        ACTION_EMAIL_OPT_IN("EmailOptIn"),
        ACTION_EMAIL_OPT_OUT("EmailOptOut"),
        ACTION_WKND_GET_STARTED("Wknd:GetStarted"),
        ACTION_MENU_WKND_PROMO_GET_STARTED("MenuWkndPromo:GetStarted"),
        ACTION_GET_STARTED("GetStarted"),
        ACTION_CLOSE_OR_X("CloseOrX"),
        ACTION_CLOSE("Close"),
        ACTION_DELETE("Delete"),
        ACTION_LEARN_MORE("LearnMore"),
        ACTION_SUBMIT("Submit"),
        ACTION_EC_FORGOT_PW_USERNAME("ForgotPWUsername"),
        ACTION_RATE_VEHICLE("RateVehicle"),
        ACTION_REMEMBER_ON("RememberOn"),
        ACTION_REMEMBER_OFF("RememberOff"),
        ACTION_NEXT("Next"),
        ACTION_JOIN("Join"),
        ACTION_JOIN_PLUS("JoinePlus"),
        ACTION_PROMO_EMAIL_BOX("PromoEmailBox"),
        ACTION_TERMS_BOX("T&CBox"),
        ACTION_TERMS_LINK("T&CAdditionalInfo"),
		ACTION_SAVE_TO_PHOTOS("SaveToPhotos"),
		ACTION_PHONE_LINK("PhoneLink"),
        ACTION_PAY_NOW_BODY("PayNow:Body"),
        ACTION_PAY_LATER_BODY("PayLater:Body"),
		ACTION_PAY_NOW_BANNER("PayNow:Header"),
		ACTION_PAY_LATER_HELP("PayLaterHelp"),
		ACTION_PAY_LATER_BANNER("PayLater:Header"),
		ACTION_MODIFY_METHOD_OF_PAYMENT("ModifyMethodofPayment"),
		ACTION_MODIFY_EXTRAS("ModifyExtras"),
		ACTION_MODIFY_PICKUP_LOCATION("ModifyPickupLocation"),
		ACTION_MODIFY_DROPOFF_LOCATION("ModifyDropOffLocation"),
		ACTION_LOCK("Lock"),
		ACTION_CONTINUE_MODIFY("Continue"),
		ACTION_DATA_COLLECTION_ON("DataCollectionON"),
        ACTION_DATA_COLLECTION_OFF("DataCollectionOFF"),
		ACTION_SAVE_SEARCH_HISTORY_ON("SaveSearchHistoryON"),
        ACTION_SAVE_SEARCH_HISTORY_OFF("SaveSearchHistoryOFF"),
		ACTION_PPREFERRED_CC_ON("PreferredCreditCardON"),
        ACTION_PPREFERRED_CC_OFF("PreferredCreditCardOFF"),
		ACTION_CLEAR_PERSONAL_DATA("ClearPersonalData"),
		ACTION_AUTO_SAVE_ON("AutoSaveON"),
		ACTION_AUTO_SAVE_OFF("AutoSaveOFF"),
		ACTION_COMPLETE_ENROLLMENT("CompleteEnrollment"),
        ACTION_ENROLLMENT("Enrollment"),
        ACTION_PRIVACY_POLICY("PrivacyPolicy"),
        ACTION_ABOUT_EP("AboutEP"),
        ACTION_ABOUT_POINTS("AboutPoints"),
        ACTION_START_RESERVATION("StartReservation"),
        ACTION_EP_PROGRAM_DETAILS("EPProgramDetails"),
        ACTION_VIEW_POINTS_HISTORY("ViewPointsHistory"),
        ACTION_TRANSFER_POINTS("TransferPoints"),
        ACTION_CALL_REQUEST_POINTS("CallRequestPoints"),
        ACTION_EXPAND_COLLAPSE_PLUS_TIER("ExpandCollapsePlusTier"),
        ACTION_EXPAND_COLLAPSE_SILVER_TIER("ExpandCollapseSilverTier"),
        ACTION_EXPAND_COLLAPSE_GOLD_TIER("ExpandCollapseGoldTier"),
        ACTION_EXPAND_COLLAPSE_PLATINUM_TIER("ExpandCollapsePlatinumTier"),
        ACTION_PROGRAM_DETAILS("ProgramDetails"),
        ACTION_SEND_SURVEY("SendSurvey"),
        ACTION_DATE_SELECT("DateSelect"),
        ACTION_TIME_SELECT("TimeSelect"),
        ACTION_DATE_UNSELECT("DateUnselect"),
        ACTION_TIME_UNSELECT("TimeUnselect"),
        ACTION_RESET_FILTER("ResetFilter"),
        ACTION_EDIT_FILTERS("EditFilters"),
        ACTION_CLEAR_ALL_FILTERS("ClearAllFilters"),
        ACTION_SHOW_HOURS("ShowHours"),
        ACTION_HIDE_HOURS("HideHours"),
        ACTION_ABOUT_AFTER_HOURS("AboutAfterHours"),
        ACTION_LOCATION_LIST_HEADER("LocationListHeader"),
        ACTION_SELECT_DIFFERENT_LOCATION("SelectaDifferentLocation"),
        ACTION_CUSTOMER_SERVICE("CustomerService"),
        ACTION_ROADSIDE_ASSISTANCE("RoadsideAssistance"),
        ACTION_ENTERPRISE_PLUS("EnterprisePlus"),
        ACTION_CUSTOMERS_DISABILITIES("CustomersDisabilities"),
        ACTION_SEND_MESSAGE("SendMessage"),
        ACTION_FAQ("FAQs"),
        ACTION_GAS_STATION("GasStation"),
        ACTION_KEEP_ADDRESS("KeepAddress"),
        ACTION_CHANGE_ADDRESS("ChangeAddress"),
        ACTION_NOT_NOW("NotNow"),
        ACTION_ENABLED("Enabled"),
        ACTION_CHANGE_TRACKING_SETTING("ChangeTrackingSetting"),
        ACTION_ADD_TO_CALENDAR("AddToCalendar"),
        ACTION_CLEAR_HISTORICAL_DATA("ClearHistoricalData"),
        ACTION_ERROR("Error");


        public final String value;

        Action(final String newValue) {
            value = newValue;
        }
    }

    public enum LocationType {
        LOCATION_TYPE_BRANCH("Branch"),
        LOCATION_TYPE_AIRPORT("Airport"),
        LOCATION_TYPE_RAIL("Rail"),
        LOCATION_TYPE_PORT("Port");

        public final String value;

        LocationType(final String newValue) {
            value = newValue;
        }
    }

    public enum LocationShortcut {
        LOCATION_SHORTCUT_NEARBY("Nearby"),
        LOCATION_SHORTCUT_RECENT("Recent"),
        LOCATION_SHORTCUT_FAVORITE("Favorite");

        public final String value;

        LocationShortcut(final String newValue) {
            value = newValue;
        }
    }

    public enum Boolean {
        BOOL_YES("Y"),
        BOOL_NO("N");

        public final String value;

        Boolean(final String newValue) {
            value = newValue;
        }
    }

    public enum FilterType {
        FILTER_LOCATION("Location"),
        FILTER_CAR_CLASS("Car Class");

        public final String value;

        FilterType(final String newValue) {
            value = newValue;
        }
    }

    public enum LocationFilterType {
        PICKUP_DATE("Pick Up Date"),
        PICKUP_TIME("Pick Up Time"),
        DROPOFF_DATE("Drop Off Date"),
        DROPOFF_TIME("Drop Off Time"),
        NONE("none");

        public final String value;

        LocationFilterType(final String newValue) {
            value = newValue;
        }
    }

    public enum CustomerType {
        TYPE_CORPORATE("Corp"),
        TYPE_INDIVIDUAL("Ind");

        public final String value;

        CustomerType(final String newValue) {
            value = newValue;
        }
    }

    public enum Lob {
        LOB_MOTORCYCLE("Motorcycle");

        public final String value;

        Lob(final String newValue) {
            value = newValue;
        }
    }

    public enum PhoneType {
        PHONE_TYPE_CALL_CENTER("callCenter"),
        PHONE_TYPE_ROADSIDE("roadside"),
        PHONE_TYPE_PU_LOCATION("pickupLocation"),
        PHONE_TYPE_DO_LOCATION("dropoffLocation");

        public final String value;

        PhoneType(final String newValue) {
            value = newValue;
        }
    }

    public enum ReservationType {
        RESERVATION_ORIGINAL("Original"),
        RESERVATION_MODIFY("Modify");

        public final String value;

        ReservationType(final String newValue) {
            value = newValue;
        }
    }

    public enum PaymentType {
        PAYMENT_TYPE_PAY_NOW("PayNow"),
        PAYMENT_TYPE_PAY_LATER("PayLater");

        public final String value;

        PaymentType(final String newValue) {
            value = newValue;
        }
    }

    public enum LocationConflict {
        NONE("None"),
        CLOSED("Closed");

        public final String value;

        LocationConflict(final String newValue) {
            value = newValue;
        }
    }

}