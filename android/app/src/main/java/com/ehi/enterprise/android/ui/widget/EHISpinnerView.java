package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EhiSpinnerViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorSpinnerViewState;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(EHISpinnerViewModel.class)
public class EHISpinnerView extends DataBindingViewModelView<EHISpinnerViewModel, EhiSpinnerViewBinding> {

    private CharSequence mText;
    private DialogInterface.OnClickListener mCallback = null;
    private List<CharSequence> mItems;
    private boolean mNoDefaultValue = false;

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            setText(mItems.get(i));
            dialogInterface.dismiss();
            mNoDefaultValue = false;
            if (mCallback != null) {
                mCallback.onClick(dialogInterface, i);
            }
        }
    };

    public EHISpinnerView(Context context) {
        this(context, null, 0);
    }

    public EHISpinnerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EHISpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        createViewBinding(R.layout.v_ehi_spinner);
        populateText(context, attrs);
        initViews();
    }

    public DialogInterface.OnClickListener getCallback() {
        return mCallback;
    }

    public void setCallback(DialogInterface.OnClickListener callback) {
        mCallback = callback;
    }

    private void initViews() {
        setText(mText);
    }

    private void populateText(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EHISpinnerView,
                0, 0);

        try {
            mText = array.getString(R.styleable.EHISpinnerView_spinnerText);
        } finally {
            array.recycle();
        }
    }

    /**
     * @param items
     * @param selectedValue -1 for no default value to be selected, aka empty default choice
     * @param title
     */
    public void populateView(final List<CharSequence> items, final int selectedValue, final CharSequence title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        mItems = items;

        if (selectedValue == -1) {
            getViewBinding().spinnerSelectedText.setHint(title);
            mNoDefaultValue = true;
        }
        else {
            setText(items.get(selectedValue));
        }

        final CharSequence[] array = new CharSequence[items.size()];
        items.toArray(array);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setItems(array, mOnClickListener)
                       .setTitle(title)
                       .show();
            }
        });
    }

    public void setValid(boolean valid) {
        if (valid) {
            getViewBinding().getRoot().setBackgroundResource(R.drawable.white_background_green_border);
        }
        else {
            getViewBinding().getRoot().setBackgroundResource(R.drawable.edit_text_red_border);
        }
    }

    public CharSequence getSelection() {
        return mText;
    }

    public int getSelectedIndex() {
        return mItems == null || mNoDefaultValue ? -1 : mItems.indexOf(mText);
    }

    public void setSelectedIndex(int index) {
        if (!ListUtils.isEmpty(mItems)) {
            setText(mItems.get(index));
        }
    }

    private void setText(CharSequence text) {
        mText = text;
        getViewBinding().spinnerSelectedText.setText(mText);
    }

    public static ReactorComputationFunction spinnerSelection(final ReactorVar<Pair<Integer, CharSequence>> var, final EHISpinnerView spinner) {
        spinner.setCallback(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                var.setValue(new Pair<>(spinner.getSelectedIndex(), spinner.getSelection()));
            }
        });

        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (var.getValue() != null && spinner.getVisibility() != View.GONE) {
                    spinner.setSelectedIndex(var.getValue().first);
                }
            }
        };
    }

    public static ReactorComputationFunction bindPopulateMethodBox(final ReactorSpinnerViewState source, final EHISpinnerView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getPopulateMethodParamsBox().getValue(), target))
                    target.populateView(source.getPopulateMethodParamsBox().getItemsToShow(),
                                        source.getPopulateMethodParamsBox().getSelectedValueIndex(),
                                        source.getPopulateMethodParamsBox().getTitle());
            }
        };
    }
}