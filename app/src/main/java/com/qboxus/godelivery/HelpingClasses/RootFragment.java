package com.qboxus.godelivery.HelpingClasses;

import androidx.fragment.app.Fragment;
import com.qboxus.godelivery.Interfaces.OnBackPressListenerCallback;


public class RootFragment extends Fragment implements OnBackPressListenerCallback {

    @Override
    public boolean onBackPressed() {
        return new BackPressImplimentationCallback(this).onBackPressed();
    }

}