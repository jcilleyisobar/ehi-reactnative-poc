package com.ehi.enterprise.android.ui.reservation.history;

import android.Manifest;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.InvoiceFragmentBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.widget.CollapsibleItemView;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(InvoiceViewModel.class)
public class InvoiceFragment extends DataBindingViewModelFragment<InvoiceViewModel, InvoiceFragmentBinding> {

    private static final String TAG = "InvoiceFragment";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    @Extra(value = String.class)
    public static final String INVOICE_NUMBER = "EXTRA_INVOICE_NUMBER";

    PermissionRequester mPermissionRequester = new PermissionRequester() {
        @Override
        public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            final boolean permissionsGranted = PermissionUtils.areAllPermissionsGranted(grantResults);
            if (permissionsGranted) {
                getViewModel().saveReceipt(getActivity());
            }
        }
    };

    private View.OnClickListener mSaveToPhotosClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, TAG)
                    .state(EHIAnalytics.State.STATE_RECEIPT.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SAVE_TO_PHOTOS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();
            if (!PermissionUtils.checkPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE).isEmpty()) {
                PermissionRequestHandler permissionRequestHandler = (PermissionRequestHandler) getActivity();
                permissionRequestHandler.requestPermissions(
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                        mPermissionRequester,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                );
            } else {
                getViewModel().saveReceipt(getActivity());
            }
        }
    };
    private View.OnClickListener mPhoneCallClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, TAG)
                    .state(EHIAnalytics.State.STATE_RECEIPT.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PHONE_LINK.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();
            IntentUtils.callNumber(getActivity(), ((TextView)view).getText().toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        InvoiceFragmentHelper.Extractor extractor = new InvoiceFragmentHelper.Extractor(this);
        getViewModel().setInvoiceNumber(extractor.invoiceNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_invoice, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, TAG)
                .state(EHIAnalytics.State.STATE_RECEIPT.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().getRoot().setVisibility(View.INVISIBLE);
        getViewBinding().saveToPhotos.setOnClickListener(mSaveToPhotosClickListener);
        getViewBinding().pickupPhoneView.setOnClickListener(mPhoneCallClickListener);
        getViewBinding().returnPhoneView.setOnClickListener(mPhoneCallClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getInvoice() != null) {
                    getViewBinding().getRoot().setVisibility(View.VISIBLE);
                    setUpView(getViewModel().getInvoice());
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (!TextUtils.isEmpty(getViewModel().getFileSaved())) {
                    // notify the os that we inserted a new file
                    MediaScannerConnection.scanFile(getActivity(),
                            new String[]{getViewModel().getFileSaved()}, null, null);
                    getViewModel().setFileSaved(null);
                    ToastUtils.showToast(getActivity(), R.string.invoice_saved_photo);
                }
            }
        });
    }

    private void setUpView(EHITripSummary invoice) {

        // header
        getViewBinding().rentalAgreement.setText(new TokenizedString.Formatter<EHIStringToken>(
                getActivity().getResources())
                .addTokenAndValue(EHIStringToken.RENTAL, invoice.getRentalAgreementNumber())
                .formatString(R.string.invoice_rental_number)
                .format());
        if (!TextUtils.isEmpty(invoice.getContractName())) {
            getViewBinding().contract.setVisibility(View.VISIBLE);
            getViewBinding().contract.setText(new TokenizedString.Formatter<EHIStringToken>(
                    getActivity().getResources())
                    .addTokenAndValue(EHIStringToken.CONTRACT_NAME, invoice.getContractName())
                    .formatString(R.string.invoice_rental_contract_name)
                    .format());
        }

        // trip summary
        final EHILocation pickupLocation = invoice.getPickupLocation();
        final EHILocation returnLocation = invoice.getReturnLocation();

        final String formattedDate = DateUtils.formatDateTime(getActivity(), invoice.getPickupTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR |
                        DateUtils.FORMAT_ABBREV_MONTH);
        getViewBinding().rentalTime.setText(new TokenizedString.Formatter<EHIStringToken>(getActivity().getResources())
                .addTokenAndValue(EHIStringToken.DATE, formattedDate)
                .formatString(R.string.invoice_rental_date)
                .format());

        getViewBinding().pickupTime.setText(getViewModel().getFormattedDate(getActivity(), invoice.getPickupTime().getTime()));
        getViewBinding().returnTime.setText(getViewModel().getFormattedDate(getActivity(), invoice.getReturnTime().getTime()));

        getViewBinding().pickupNameView.setText(pickupLocation.getName());
        getViewBinding().pickupAddressView.setText(pickupLocation.getAddress().getReadableAddress());
        if (pickupLocation.getPhoneNumbers() != null && pickupLocation.getPhoneNumbers().size() > 0) {
            getViewBinding().pickupPhoneView.setText(pickupLocation.getFormattedPhoneNumber(true));
        }

        getViewBinding().returnNameView.setText(returnLocation.getName());
        getViewBinding().returnAddressView.setText(returnLocation.getAddress().getReadableAddress());
        if (returnLocation.getPhoneNumbers() != null && returnLocation.getPhoneNumbers().size() > 0) {
            getViewBinding().returnPhoneView.setText(returnLocation.getFormattedPhoneNumber(true));
        }

        // currency warning
        if (getViewModel().shouldShowCurrencyWarning(pickupLocation)) {
            final String currency = invoice.getPriceSummary().getEstimatedTotalView().getFormattedCurrency();
            final SpannableString spannableString = new SpannableString(currency);
            final Typeface tf = ResourcesCompat.getFont(getContext(), R.font.source_sans_bold);
            final CustomTypefaceSpan bss = new CustomTypefaceSpan("", tf);
            spannableString.setSpan(bss, 0, currency.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            getViewBinding().currencyBanner.setMessage(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .addTokenAndValue(EHIStringToken.CURRENCY_CODE, spannableString)
                    .formatString(R.string.car_class_currency_code_differs_title)
                    .format());
            getViewBinding().currencyBanner.setVisibility(View.VISIBLE);
            getViewBinding().currencyBanner.setIcon(R.drawable.ico_info);
        }

        getViewBinding().estimatedTotal.setText(invoice.getPriceSummary().getFormattedPriceView());
        getViewBinding().rentalEstimatedTotal.setText(invoice.getPriceSummary().getFormattedPriceView());

        // price summary
        for (EHIPaymentLineItem line : invoice.getPriceSummary().getAllPaymentLineItems()) {
            CollapsibleItemView view = new CollapsibleItemView(getActivity());
            view.setInfo(line.getDescription(getResources()), line.getRentalRateText(getResources()));
            view.setValue(line.getTotalAmountView().getFormattedPrice(true).toString());
            getViewBinding().priceSummaryContainer.addView(view);
        }

        // additional info
        getViewBinding().additionalInfo.populate(invoice);

        //footer
        if (!TextUtils.isEmpty(invoice.getVatNumber())) {
            getViewBinding().vat.setText(new TokenizedString.Formatter<EHIStringToken>(getActivity().getResources())
                    .addTokenAndValue(EHIStringToken.VAT, invoice.getVatNumber())
                    .formatString(R.string.invoice_vat_number)
                    .format());
            getViewBinding().vat.setVisibility(View.VISIBLE);
        }

        getViewBinding().invoice.setText(new TokenizedString.Formatter<EHIStringToken>(getActivity().getResources())
                .addTokenAndValue(EHIStringToken.INVOICE, getViewModel().getInvoiceNumber())
                .formatString(R.string.invoice_number)
                .format());
    }
}
