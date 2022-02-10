package com.qboxus.godelivery.ActivitiesFragments.EditProfile;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileF extends RootFragment implements View.OnClickListener {

    private TextView tvEditUsername;
    private EditText etFname, etLname, etUserName, etEmail, etPhoneno, etDateOfBirth;
    Preferences preferences;
    String countryName ="";
    private CircleImageView ivProfile, ivEditProfile;
    private View view;
    Dialog dialog;
    View.OnClickListener navClickListener;
    FragmentClickCallback callBack;
    String extension =".jpg";
    String imageFilePath="";

    public ProfileF(View.OnClickListener navClickListener, FragmentClickCallback callBack) {
        this.callBack = callBack;
        this.navClickListener = navClickListener;
    }

    public ProfileF() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, null);

        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views() {
        ImageView iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(navClickListener);
        preferences=new Preferences(view.getContext());
        ivProfile = view.findViewById(R.id.iv_profile);
        ivEditProfile =view.findViewById(R.id.iv_edit_profile);
        ivEditProfile.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        etFname = view.findViewById(R.id.et_fname);
        etFname.setText(preferences.getKeyUserFirstName());
        etLname = view.findViewById(R.id.et_lname);
        etLname.setText(preferences.getKeyUserLastName());
        etEmail = view.findViewById(R.id.et_email);
        etEmail.setText(preferences.getKeyUserEmail());
        etUserName = view.findViewById(R.id.et_username);
        etUserName.setText(preferences.getKeyUserName());
        etPhoneno = view.findViewById(R.id.et_phoneno);
        countryName =preferences.getKeyUserCountry();
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etDateOfBirth.setText(""+Functions.ChangeDateFormat("yyyy-MM-dd","dd MMM yyyy",preferences.getKeyUserDateOfBirth()));
        tvEditUsername = view.findViewById(R.id.edit_username);
        tvEditUsername.setOnClickListener(this);
        etDateOfBirth.setOnClickListener(this);
        etPhoneno.setOnClickListener(this);
        view.findViewById(R.id.tv_done).setOnClickListener(this);
        Uri uri = Uri.parse(ApiUrl.baseUrl +preferences.getKeyUserImage());
        Glide.with(view.getContext())
                .load(uri)
                .placeholder(R.drawable.ic_profile_gray)
                .error(R.drawable.ic_profile_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(ivProfile);

        etFname.setSelection(etFname.getText().toString().length());
        etPhoneno.setText(preferences.getKeyUserPhone());

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_done:
                if (METHOD_inputValidation())
                    CallApi_editProfile();
                break;

            case R.id.edit_username:
                METHOD_setFocusonEdittext(etUserName);
                etUserName.requestFocus();
                tvEditUsername.setVisibility(View.GONE);
                break;
            case R.id.et_date_of_birth:
            {

                ClearFocusAllTextField();
                EditDateOfBirthF frg_ment = new EditDateOfBirthF(new FragmentClickCallback() {
                    @Override
                    public void OnItemClick(int postion, Bundle bundle) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (bundle!=null){
                                    etDateOfBirth.setText(""+Functions.ChangeDateFormat("yyyy-MM-dd","dd MMM yyyy",bundle.getString("dob")));
                                }
                            }
                        },200);
                    }
                });
                FragmentTransaction transaction =getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putSerializable("DOB", etDateOfBirth.getText().toString());
                frg_ment.setArguments(args);
                transaction.addToBackStack("EditDateOfBirth_F");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_profile, frg_ment,"EditDateOfBirth_F").commit();

            }
            break;
            case R.id.et_phoneno:
            {
                ClearFocusAllTextField();
                EditPhoneNoF frg_ment = new EditPhoneNoF(new FragmentClickCallback() {
                    @Override
                    public void OnItemClick(int postion, Bundle bundle) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (bundle!=null){
                                    etPhoneno.setText(preferences.getKeyUserPhone());
                                }
                            }
                        },200);
                    }
                });
                FragmentTransaction transaction =getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                frg_ment.setArguments(args);
                transaction.addToBackStack("EditPhoneNo_F");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_profile, frg_ment,"EditPhoneNo_F").commit();
            }
                break;
            case R.id.iv_profile:
            {
                selectfile();
            }
                break;
            case R.id.iv_edit_profile:
            {
                selectfile();
            }
                break;
            default:
                break;
        }
    }


    private void selectfile() {

        dialog = new Dialog(view.getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.select_profile_picture_list_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_take_photo = dialog.findViewById(R.id.txt_take_photo);
        TextView txt_gallery = dialog.findViewById(R.id.txt_gallery);
        TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
        txt_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_camrapermission())
                {
                    openCameraIntent();
                }
                dialog.dismiss();

            }});
        txt_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_ReadStoragepermission())
                {
                    openGalleryIntent();
                }
                dialog.dismiss();
            }
        });
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    private void openCameraIntent() {

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
            } catch (Exception ex) {
                Log.d(Variables.tag,"Error : "+ex);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private boolean check_camrapermission() {

        if (ContextCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, Variables.permissionCameraCode);
        }
        return false;
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Variables.permissionGalleryCode);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Variables.permissionCameraCode:
            {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraIntent();
                } else {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.camera_denied));
                }
            }
            break;
            case Variables.permissionGalleryCode:
            {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryIntent();
                } else {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.storage_denied));
                }
            }
            break;
        }
    }



    //    edittext focus request
    private void METHOD_setFocusonEdittext(EditText et) {
        et.setFocusableInTouchMode(true);
        et.setFocusable(true);
        et.setClickable(true);

        if (et.length() > 0)
            et.setSelection(et.length());
    }

//    clear edittext focues
    private void ClearFocusAllTextField()
    {
        Functions.HideSoftKeyboard(getActivity());
        etFname.clearFocus();
        etLname.clearFocus();
        etUserName.clearFocus();
        etEmail.clearFocus();

        etFname.setError(null);
        etLname.setError(null);
        etUserName.setError(null);
        etDateOfBirth.setError(null);
        etPhoneno.setError(null);
        etEmail.setError(null);
    }



//    form validation
    private Boolean METHOD_inputValidation(){
        if (TextUtils.isEmpty(etFname.getText().toString()))
        {
            etFname.setError(view.getContext().getString(R.string.required_first_name));
            etFname.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etLname.getText().toString()))
        {
            etLname.setError(view.getContext().getString(R.string.required_last_name));
            etLname.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etUserName.getText().toString()))
        {
            etUserName.setError(view.getContext().getString(R.string.required_username));
            etUserName.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etDateOfBirth.getText().toString()))
        {
            etDateOfBirth.setError(view.getContext().getString(R.string.required_dob));
            return false;
        }
        if (TextUtils.isEmpty(etPhoneno.getText().toString()))
        {
            etPhoneno.setError(view.getContext().getString(R.string.required_phone));
            etPhoneno.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString()))
        {
            etEmail.setError(view.getContext().getString(R.string.required_email));
            etEmail.setFocusable(true);
            return false;
        }
        if (!(Functions.isValidEmail(etEmail.getText().toString())))
        {
            etEmail.setError(view.getContext().getString(R.string.invalid_email));
            etEmail.setFocusable(true);
            return false;
        }

        return true;
    }



    private void CallApi_editProfile(){
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id",preferences.getKeyUserId());
            sendobj.put("first_name", ""+ etFname.getText().toString());
            sendobj.put("last_name", ""+ etLname.getText().toString());
            sendobj.put("username", ""+ etUserName.getText().toString());
            sendobj.put("dob", ""+Functions.ChangeDateFormat("dd MMM yyyy","yyyy-MM-dd",""+ etDateOfBirth.getText().toString()));
            sendobj.put("email", ""+ etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.editProfile, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                            METHOD_saveUserDetails(userobj,countryobj);
                        }else {
                            Functions.ShowToast(view.getContext(),respobj.optString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void METHOD_saveUserDetails(JSONObject userobj,JSONObject countryobj) {
        String fname = ""+userobj.optString("first_name");
        String lname = ""+userobj.optString("last_name");
        String email = ""+userobj.optString("email");
        String username = ""+userobj.optString("username");
        String dob = ""+userobj.optString("dob");
        String country = ""+countryobj.optString("name");
        String country_id = ""+countryobj.optString("id");
        String country_Ios = ""+countryobj.optString("iso");
        String country_Code = ""+countryobj.optString("country_code");

        preferences.setKeyUserFirstName(fname);
        preferences.setKeyUserLastName(lname);
        preferences.setKeyUserEmail(email);
        preferences.setKeyUserName(username);
        preferences.setKeyUserDateOfBirth(dob);
        preferences.setKeyUserCountry(country);
        preferences.setKeyUserCountryId(country_id);
        preferences.setKeyUserCountryIOS(country_Ios);
        preferences.setKeyCountryCode(country_Code);

        callBack.OnItemClick(0, new Bundle());
        getActivity().onBackPressed();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {

            if (requestCode == 1) {
                Matrix matrix = new Matrix();
                try {
                    android.media.ExifInterface exif = new android.media.ExifInterface(imageFilePath);
                    int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
                beginCrop(selectedImage);

            }
            else
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                beginCrop(selectedImage);

            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                handleCrop(result.getUri());
            }

        }
    }


    private void handleCrop( Uri userimageuri) {

        InputStream imageStream = null;
        try {
            imageStream =getActivity().getContentResolver().openInputStream(userimageuri);
        } catch (FileNotFoundException e) {
           Log.d(Variables.tag,"Error : "+e);
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path=userimageuri.getPath();
        Log.d(Variables.tag,"Check : "+path);
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                Log.d(Variables.tag,"Error : "+e);
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Bitmap converetdImage = getResizedBitmap(rotatedBitmap, 500);
        CallApi_updateProfilepic(encodetobase64(converetdImage));

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void beginCrop(Uri source) {
        CropImage.activity(source)
                .start(view.getContext(), this);
    }

//    image incrypt into base64 String
    String  encodetobase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public  void CallApi_updateProfilepic(String image) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            JSONObject file_data = new JSONObject();
            file_data.put("file_data", image);
            sendobj.put("image", file_data);

        } catch (JSONException e) {

        }

        Functions.ShowProgressLoader(getContext(), false, false);
        ApiRequest.CallApi(getContext(), ApiUrl.addUserImage, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            String image = ""+userobj.optString("image");

                            preferences.setKeyUserImage(image);
                            Uri uri = Uri.parse(ApiUrl.baseUrl +preferences.getKeyUserImage());
                            Glide.with(view.getContext())
                                    .load(uri)
                                    .placeholder(R.drawable.ic_profile_gray)
                                    .error(R.drawable.ic_profile_gray)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(ivProfile);

                        }else {
                            Log.d(Variables.tag, ""+respobj.getString("msg"));
                            Functions.ShowToast(view.getContext(),respobj.optString("msg"));
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }


    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}
