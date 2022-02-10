package com.qboxus.godelivery.HelpingClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.qboxus.godelivery.Interfaces.OnBackPressListenerCallback;


public class BackPressImplimentationCallback implements OnBackPressListenerCallback {

    private Fragment parentFragment;

    public BackPressImplimentationCallback(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            return false;
        } else {
          try {

              // get the child Fragment
              FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
              OnBackPressListenerCallback childFragment = (OnBackPressListenerCallback) childFragmentManager.getFragments().get(0);

              if (!childFragment.onBackPressed()) {
                  childFragmentManager.popBackStackImmediate();
              }

              return true;

          }catch (Exception e)
          {
              return false;
          }

        }
    }

}