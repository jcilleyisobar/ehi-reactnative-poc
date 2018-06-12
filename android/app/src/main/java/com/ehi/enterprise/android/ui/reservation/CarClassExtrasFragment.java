package com.ehi.enterprise.android.ui.reservation;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CarClassExtrasDataBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.ui.activity.ModalDialogActivityHelper;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionedRecyclerViewAdapter;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnHeaderActionListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.BookRentalButton;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(CarClassExtrasViewModel.class)
public class CarClassExtrasFragment extends DataBindingViewModelFragment<CarClassExtrasViewModel, CarClassExtrasDataBinding>
        implements SectionedRecyclerViewAdapter.ItemBoundCallback {

    public static final String SCREEN_NAME = "CarClassExtrasFragment";

    public static final String TAG = CarClassExtrasFragment.class.getSimpleName();
    public static final long SLIDE_UP_ANIMATION = 200;
    public static final long SLIDE_DOWN_ANIMATION = 700;

    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";

    @Extra(boolean.class)
    public static final String FROM_CHOOSE_YOUR_RATE = "FROM_CHOOSE_YOUR_RATE";

    private List<EHIExtraItem> mExtraItemsList = new ArrayList<>();

    private CarClassExtrasAdapter mCarClassExtrasAdapter;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private Runnable mRunStartAnimation;
    private Handler mHandler;

    private Map<String, Boolean> mOnRequestDialogsState = new HashMap<>();

    private List<Pair<RecyclerView.ViewHolder, Integer>> mMockedAdapter;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_CONTINUE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_EXTRAS_SELECTED.value)
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
                getViewModel().continueClicked();
            } else if (view == null) {
                showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder().build());
            }
        }
    };

    private OnExtraActionListener mOnExtraActionListener = new OnExtraActionListener() {
        @Override
        public void onChangeExtraCount(EHIExtraItem item, int newCount) {
            if (newCount == 0) {
                mOnRequestDialogsState.remove(item.getCode());
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_UNSELECT.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
            } else {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
            }
            if (EHIExtraItem.ON_REQUEST.equals(item.getAllocation())) {
                final Boolean wasDialogShown = mOnRequestDialogsState.get(item.getCode());
                if (newCount == 1 && wasDialogShown == null) {
                    onRequestDialog(item, newCount);
                    mOnRequestDialogsState.put(item.getCode(), true);
                } else {
                    changeExtrasCount(item, newCount);
                }
            } else {
                changeExtrasCount(item, newCount);
            }
        }

        @Override
        public void onClick(EHIExtraItem item) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SUMMARY.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODAL_LAUNCH.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.modalLaunch(item.getCode()))
                    .tagScreen()
                    .tagEvent();

            Fragment fragment = new ModalTextDialogFragmentHelper.Builder()
                    .title(item.getName())
                    .text(item.getDetailedDescription())
                    .build();

            Intent intent = new ModalDialogActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivity(intent);
        }
    };

    private OnHeaderActionListener mOnHeaderActionListener = new OnHeaderActionListener() {
        @Override
        public void onTotalCostClicked() {
            getViewModel().onTotalCostClicked();
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CarClassExtrasFragmentHelper.Extractor extractor = new CarClassExtrasFragmentHelper.Extractor(this);
        getViewModel().setIsModify(extractor.isModify());
        getViewModel().setPayState(((ReservationFlowListener) getActivity()).getPayState());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_extras, container);
        mMockedAdapter = new ArrayList<>(5);
        initViews();
        mHandler = new Handler();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
        getViewModel().setPayState(flowListener.getPayState());
        getViewModel().setReservationFlowListener(flowListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getViewModel().isModify()) {
            getActivity().setTitle(R.string.reservation_modify_extras_navigation_title);
        } else {
            getActivity().setTitle(R.string.reservation_extras_navigation_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        initAdapter();
        getViewBinding().continueButton.setTitle(getResources().getString(R.string.reservation_extras_footer_continue_title));
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().extrasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        getViewBinding().extrasRecyclerView.setAdapter(mSectionedAdapter);
        introAnimation();
    }

    private void introAnimation() {
        if (getViewModel().isAnimatedOnce()) {
            return;
        }
        getViewBinding().continueButton.setTranslationY(getResources().getDimension(R.dimen.green_button_big_height) + 10);
        getViewBinding().continueButton.animate()
                .translationY(0)
                .setStartDelay(SLIDE_DOWN_ANIMATION)
                .setDuration(SLIDE_UP_ANIMATION);
    }

    private void initAdapter() {
        mCarClassExtrasAdapter = new CarClassExtrasAdapter(
                mExtraItemsList,
                getViewModel().needShowPoints(),
                getViewModel().getPayState(),
                new CarClassExtrasFragmentHelper.Extractor(this).fromChooseYourRate(),
                getViewModel().getSelectedCarClassCharges()
        );

        mCarClassExtrasAdapter.setOnExtraActionListener(mOnExtraActionListener);
        mCarClassExtrasAdapter.setOnHeaderActionListener(mOnHeaderActionListener);
        mCarClassExtrasAdapter.setOnRentalTermsConditionsListener(mOnClickListener);

        mRunStartAnimation = new Runnable() {
            @Override
            public void run() {
                if (mMockedAdapter.size() > 1) {
                    startAnimation();
                }
            }
        };
        mSectionedAdapter = new SectionedRecyclerViewAdapter(getActivity(), mCarClassExtrasAdapter, this);
        if (!getViewModel().isAnimatedOnce()) {
            mSectionedAdapter.hideItems(Collections.singletonList(CarClassExtrasAdapter.VIEW_TYPE_HEADER));
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(BookRentalButton.enabled(getViewModel().continueButton.enabled(), getViewBinding().continueButton));
        bind(BookRentalButton.price(getViewModel().continueButton.price(), getViewBinding().continueButton));
        bind(BookRentalButton.priceVisibility(getViewModel().continueButton.priceVisibility(), getViewBinding().continueButton));
        bind(BookRentalButton.netRateVisibility(getViewModel().continueButton.netRateVisibility(), getViewBinding().continueButton));
        bind(BookRentalButton.progress(getViewModel().continueButton.progress(), getViewBinding().continueButton));
        bind(BookRentalButton.priceSubtitle(getViewModel().continueButton.priceSubtitle(), getViewBinding().continueButton));
        bind(BookRentalButton.subtitle(getViewModel().continueButton.subtitle(), getViewBinding().continueButton));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("CAR_CLASS_DETAILS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getCarClassDetails() != null) {
                    populateCarClassExtrasAdapter();
                }
            }
        });

    }

    private void populateCarClassExtrasAdapter() {
        final EHICarClassDetails ehiCarClassDetails = getViewModel().getCarClassDetails();
        final EHICarClassDetails ehiCarClass = getViewModel().getCarClass();
        if (ehiCarClassDetails == null || ehiCarClass == null) {
            // should not happen if called from CAR_CLASS_DETAILS_REACTION
            DLog.e("getViewModel().getCarClassDetails() or getViewMOdel().getCarClass() returned null");
            return;
        }

        mCarClassExtrasAdapter.setAnimatedOnce(getViewModel().isAnimatedOnce());
        mCarClassExtrasAdapter.setCarClass(ehiCarClass);

        mExtraItemsList.clear();

        final List<SectionHeader> sectionHeaders = new ArrayList<>();

        final EHIExtras ehiExtras = getViewModel().getReservationObject().getExtras();
        if (ehiExtras != null) {
            //included
            final List<EHIExtraItem> included = ehiExtras.getIncludedExtras();
            if (included.size() > 0) {
                sectionHeaders.add(SectionHeader.Builder
                        .atPosition(mExtraItemsList.size() + 2)
                        .setTitle(getString(R.string.reservation_extras_included_header))
                        .showTriangle(mExtraItemsList.size() == 0)
                        .build());
                mExtraItemsList.addAll(included);
            }

            //mandatory
            final List<EHIExtraItem> mandatory = ehiExtras.getMandatoryExtras();
            if (mandatory.size() > 0) {
                sectionHeaders.add(SectionHeader.Builder
                        .atPosition(mExtraItemsList.size() + 2)
                        .setTitle(getString(R.string.reservation_extras_mandatory_header))
                        .showTriangle(mExtraItemsList.size() == 0)
                        .build());
                mExtraItemsList.addAll(mandatory);
            }

            //equipment
            final List<EHIExtraItem> equipment = ehiExtras.getOptionalAndWaivedEquipment();
            sectionHeaders.add(SectionHeader.Builder
                    .atPosition(mExtraItemsList.size() + 2)
                    .setTitle(getActivity().getResources().getString(R.string.reservation_extras_equipment_header))
                    .showTriangle(mExtraItemsList.size() == 0)
                    .build());
            if (equipment.size() > 0) {
                mExtraItemsList.addAll(equipment);
            } else {
                mExtraItemsList.add(EHIExtraItem.createPlaceholder(getString(R.string.reservation_extras_item_equipment_placeholder_text)));
            }

            //insurance
            final List<EHIExtraItem> insurance = ehiExtras.getOptionalAndWaivedInsurance();
            sectionHeaders.add(SectionHeader.Builder
                    .atPosition(mExtraItemsList.size() + 2)
                    .setTitle(getActivity().getResources().getString(R.string.reservation_extras_protection_header))
                    .showTriangle(mExtraItemsList.size() == 0)
                    .build());
            if (insurance.size() > 0) {
                mExtraItemsList.addAll(insurance);
            } else {
                mExtraItemsList.add(EHIExtraItem.createPlaceholder(getString(R.string.reservation_extras_item_protection_placeholder_text)));
            }

            //fuel
            final List<EHIExtraItem> fuel = ehiExtras.getOptionalAndWaivedFuel();
            if (fuel.size() > 0) {
                sectionHeaders.add(SectionHeader.Builder
                        .atPosition(mExtraItemsList.size() + 2)
                        .setTitle(getActivity().getResources().getString(R.string.reservation_extras_fuel_header))
                        .showTriangle(mExtraItemsList.size() == 0)
                        .build());
                mExtraItemsList.addAll(fuel);
            }
        }
        updateExtraItemsPrice();
        mSectionedAdapter.setSections(sectionHeaders);
    }

    private void updateExtraItemsPrice() {
        final List<EHIPaymentLineItem> newItems = getViewModel().getCarClassDetails().getPriceSummary().getAllPaymentLineItems();
        if (newItems == null || mExtraItemsList == null) return;
        Map<String, EHIPaymentLineItem> map = new HashMap<>();
        for (EHIPaymentLineItem item : newItems) {
            if (!EHITextUtils.isEmpty(item.getCode())) {
                map.put(item.getCode().toLowerCase(), item);
            }
        }

        for (final EHIExtraItem extra: mExtraItemsList) {
            if (!EHITextUtils.isEmpty(extra.getCode())) {
                final EHIPaymentLineItem item = map.get(extra.getCode().toLowerCase());
                if (item != null && item.getTotalAmountView() != null) {
                    extra.setTotalAmountView(item.getTotalAmountView());
                }
            }
        }
        mCarClassExtrasAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean recyclerItemBound(RecyclerView.ViewHolder holder, int position) {
        if (mRunStartAnimation == null) {
            return false;
        }
        mHandler.removeCallbacks(mRunStartAnimation);
        mHandler.postDelayed(mRunStartAnimation, 5);
        mMockedAdapter.add(new Pair<>(holder, position));
        return true;
    }

    private void startAnimation() {
        if (mRunStartAnimation == null || getViewModel().isAnimatedOnce()) {
            return;
        }

        mRunStartAnimation = null;
        getViewBinding().extrasRecyclerView.setVisibility(View.GONE);

        View headerView = null;
        float originalStartPosition = 0;
        for (int i = 0, size = mMockedAdapter.size(); i < size; i++) {
            final Pair<RecyclerView.ViewHolder, Integer> viewHolderIntegerPair = mMockedAdapter.get(i);
            final View mockedView = mSectionedAdapter.getViewHolder(
                    viewHolderIntegerPair.second, getViewBinding().animationLayer, true
            ).itemView;
            final View recyclerItem = viewHolderIntegerPair.first.itemView;

            mockedView.setY(recyclerItem.getY());
            mockedView.setLayoutParams(new ViewGroup.LayoutParams(recyclerItem.getWidth(), recyclerItem.getHeight()));

            //shift down by 1 due to behaviour of sectioned adapter view types
            if (mSectionedAdapter.getItemViewType(viewHolderIntegerPair.second) - 1 == CarClassExtrasAdapter.VIEW_TYPE_HEADER) {
                headerView = mockedView;
                originalStartPosition = recyclerItem.getY() + recyclerItem.getHeight();
                getViewBinding().animationLayer.addView(mockedView);
            } else {
                getViewBinding().animationExtras.addView(mockedView);
            }
        }

        if (headerView != null) {
            headerView.bringToFront();

        }
        final View finalHeaderView = headerView;

        getViewBinding().animationExtras.setY(-1 * (DisplayUtils.getScreenHeight(getActivity()) - originalStartPosition + 100));
        getViewBinding().animationExtras.animate()
                .setDuration(SLIDE_DOWN_ANIMATION)
                .y(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        getViewModel().setAnimatedOnce(true);
                        getViewBinding().extrasRecyclerView.setVisibility(View.VISIBLE);
                        getViewBinding().animationLayer.setVisibility(View.GONE);
                        getViewBinding().animationExtras.removeAllViews();
                        mCarClassExtrasAdapter.setAnimatedOnce(false);
                        mCarClassExtrasAdapter.notifyDataSetChanged();

                        mSectionedAdapter.revealItems();
                        for (int i = 0, size = getViewBinding().animationLayer.getChildCount(); i < size; i++) {
                            if (!getViewBinding().animationLayer.getChildAt(i).equals(finalHeaderView)) {
                                getViewBinding().animationLayer.removeView(getViewBinding().animationLayer.getChildAt(i));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    private void changeExtrasCount(EHIExtraItem item, int newCount) {
        getViewModel().changeExtrasCount(item, newCount);
    }

    private void onRequestDialog(final EHIExtraItem item, final int newCount) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.on_request_status_message_dialog_title))
                .setPositiveButton(getString(R.string.standard_ok_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeExtrasCount(item, newCount);
                    }
                })
                .setNegativeButton(getString(R.string.alert_cancel_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCarClassExtrasAdapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(true)
                .create()
                .show();
    }

}