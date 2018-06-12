package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.HtmlParserViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class HtmlParseFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, HtmlParserViewBinding> {

    @Extra(value = String.class, required = false)
    public static final String TITLE = "TITLE";
    @Extra(value = String.class, required = false)
    public static final String MESSAGE = "MESSAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_html_parser, container);

        HtmlParseFragmentHelper.Extractor extractor = new HtmlParseFragmentHelper.Extractor(this);
        if (extractor.title() != null) {
            getActivity().setTitle(extractor.title());
        } else {
            getActivity().setTitle("");
        }
        if (extractor.message() != null) {
            getViewBinding().parsedText.setText(Html.fromHtml(extractor.message()));
        } else {
            getViewBinding().parsedText.setText("");
        }
        return getViewBinding().getRoot();
    }

}
