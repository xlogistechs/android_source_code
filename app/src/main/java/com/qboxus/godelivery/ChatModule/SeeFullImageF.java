package com.qboxus.godelivery.ChatModule;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.CircleProgressBarDrawable;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.R;

import java.io.File;


public class SeeFullImageF extends RootFragment implements View.OnClickListener {


    private View view;
    private Context context;
    private SimpleDraweeView singleImage;
    private String imageUrl, chatId;
    private ProgressDialog progressDialog;
    Uri uri;
    Preferences preferences;
    private File direct;
    private File fullPath;
    private Boolean isFromChatScreen;
    private DownloadRequest prDownloader;

    public SeeFullImageF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.see_full_img_f, container, false);
        context = getContext();
        preferences=new Preferences(view.getContext());
        isFromChatScreen = getArguments().getBoolean("isfromchat");

        Log.d("isfromchatscreen", "" + isFromChatScreen);
        if (isFromChatScreen) {
            imageUrl = getArguments().getString("image_url");

        } else {
            imageUrl = getArguments().getString("image_url");
            chatId = getArguments().getString("chat_id");
        }

        ImageView closeGallery = view.findViewById(R.id.close_gallery);
        closeGallery.setOnClickListener(this);


        progressDialog = new ProgressDialog(context, R.style.MyDialogStyle);
        progressDialog.setMessage("Please Wait");

        PRDownloader.initialize(getActivity().getApplicationContext());


        //get the full path of image in database
        fullPath = new File(Variables.folderGoDelivery + chatId + ".jpg");

        //if the image file is exits then we will hide the save btn
        ImageView savebtn = view.findViewById(R.id.savebtn);
        if (fullPath.exists()) {
            savebtn.setVisibility(View.GONE);
        }

        //get the directory inwhich we want to save the image
        direct = new File(Variables.folderGoDelivery);

        //this code will download the image
        prDownloader = PRDownloader.download(imageUrl, direct.getPath(), chatId + ".jpg")
                .build();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savepicture();
            }
        });

        singleImage = view.findViewById(R.id.single_image);

        // if the image is already save then we will show the image from directory otherwise
        // we will show the image by using picasso
        if (fullPath.exists()) {
            Uri uri = Uri.parse(fullPath.getAbsolutePath());
            singleImage.setImageURI(uri);
        } else {

            GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setProgressBarImage(new CircleProgressBarDrawable())
                    .build();
            singleImage.setHierarchy(hierarchy);

            if (isFromChatScreen) {
                String image_url = preferences.getKeyUserImage();
                uri = Uri.parse(ApiUrl.baseUrl + image_url);
            } else {

                uri = Uri.parse(imageUrl);
            }

            singleImage.setImageURI(uri);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_gallery:
                if (isFromChatScreen)
                    getActivity().onBackPressed();
                else
                    getFragmentManager().popBackStackImmediate();
                break;
        }
    }

    // this funtion will save the picture but we have to give tht permision to right the storage
    private void Savepicture() {
        if (Checkstoragepermision()) {

            final File direct = new File(Variables.folderGoDeliveryDCIM);
            progressDialog.show();
            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse(direct.getPath() + chatId + ".jpg"));
                    context.sendBroadcast(intent);
                    progressDialog.dismiss();

                    Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.image_saved)
                    ,""+ fullPath.getAbsolutePath());
                }

                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.something_went_wrong));
                }

            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.click_again));
        }
    }

    private boolean Checkstoragepermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {

            return true;
        }
    }
}


