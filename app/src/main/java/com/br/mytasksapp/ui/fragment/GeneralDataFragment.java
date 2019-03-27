package com.br.mytasksapp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.ui.activity.TermsActivity;
import com.br.mytasksapp.util.Mask;
import com.br.mytasksapp.util.RealPathUtil;
import com.br.mytasksapp.util.Util;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class GeneralDataFragment extends Fragment implements OnUserCompleted {

    @NotEmpty(messageId = R.string.name)
    private EditText name;

    private CheckBox terms;
    private Spinner sex;

    private EditText phone;

    private RadioGroup accountReason;

    private AppCompatButton salve;

    private Context context;

    private String[] itemsSex = {"Masculino", "Feminino"};

    private UserHttp userHttp;

    private TextView viewTerms;

    private View view;

    private RadioButton professional;

    /* Photo */
    private final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/MyTasks/";
    private String file;

    private Uri filePath;
    private boolean isCam = false;
    private Uri outputFileUri;

    private static final int CAMERA_PHOTO = 100;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    private CircleImageView profile_image;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestStoragePermission();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_general, container, false);

        terms = view.findViewById(R.id.terms);
        sex = view.findViewById(R.id.sex);

        phone = view.findViewById(R.id.phone);
        accountReason = view.findViewById(R.id.account_reason);

        salve = view.findViewById(R.id.salve);

        name = view.findViewById(R.id.name);

        professional = view.findViewById(R.id.professional);

        profile_image = view.findViewById(R.id.profile_image);

        context = getContext();

        requestCameraPermission();

        viewTerms = view.findViewById(R.id.viewTerms);

        viewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TermsActivity.class));
            }
        });

        userHttp = new UserHttp(context, this);

        userHttp.getMyData();

        /* Init */
        terms.setTypeface(ResourcesCompat.getFont(Objects.requireNonNull(context), R.font.montserrat));

        Util.createSpinnerItems(context, sex, itemsSex, R.color.gray_strong_app, R.layout.item_spinner, false);

        phone.addTextChangedListener(Mask.insert(phone, Mask.MaskType.PHONE));

        salve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(terms.isChecked()){
                    validate(view);
                }else{
                    Toast.makeText(context, "Aceite os termos de uso para prosseguir", Toast.LENGTH_LONG).show();
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertImage();
            }
        });

        return view;
    }

    private void validate(View view){
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));
        RadioButton ocp = view.findViewById(accountReason.getCheckedRadioButtonId());

        if(isValid){
            RequestParams params = new RequestParams();

            if(file != null || filePath != null){
                File fileRequest = new File(isCam ? file : RealPathUtil.getRealPathFromURI(Objects.requireNonNull(getContext()), filePath));
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(fileRequest.toURI().toString());
                String mimeType = map.getMimeTypeFromExtension(ext);

                try {
                    params.put("avatar", fileRequest, mimeType);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                params.setHttpEntityIsRepeatable(true);
                params.setUseJsonStreamer(false);
            }

            params.put("name", name.getText().toString());
            params.put("sex", sex.getSelectedItem().toString());
            params.put("occupation", ocp.getText().toString());
            params.put("phone", phone.getText().toString());


            userHttp.update(params);
        }

    }

    @Override
    public void userCompleted(JSONObject results) {
        RadioButton ocp = view.findViewById(accountReason.getCheckedRadioButtonId());

        try {
            JSONObject user = results.getJSONObject("user");

            Util.putPref("lastUser", user.toString());

            name.setText(user.getString("name"));
            name.clearFocus();

            if(!user.getString("avatar").equalsIgnoreCase("null")){
                String url = Constants.API.FILES + user.getString("avatar");
                Picasso.get().load(url).error(R.drawable.ic_user_default).into(profile_image);
            }

            if(!sex.getSelectedItem().toString().equalsIgnoreCase(user.getString("sex"))){
                sex.setSelection(1);
            }

            if(!ocp.getText().toString().equalsIgnoreCase(user.getString("occupation"))){
                professional.setChecked(true);
            }

            String phoneFinal = !user.getString("phone").equalsIgnoreCase("null") &&
                    !user.getString("phone").isEmpty() ?
                    user.getString("phone") + 1 /* +1 spacing  */ : "";

            phone.setText(phoneFinal);
            phone.clearFocus();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    public void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(context, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(context, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void alertImage() {
        final android.app.AlertDialog.Builder dialog;
        //Instanciando o dialog
        dialog = new android.app.AlertDialog.Builder(getContext(), R.style.DialogStyle);

        //Criando título do alerta
        dialog.setTitle("Adicionar foto");

        //Mensagem
        dialog.setMessage("Escolha uma das opções");

        //Configurar o botao não
        //Dois parâmetros a mensagem e o que ser feito quando for clicado
        dialog.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                isCam = false;
                String[] mimeTypes = {"image/jpeg", "image/png"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*")
                        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seecione uma imagem"), 2);
            }
        });

        //Configurar botão positivo
        dialog.setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File newdir = new File(dir);
                newdir.mkdirs();

                isCam = true;

                file = dir+ DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                    File newfile = new File(file);
                    try {
                        newfile.createNewFile();
                    } catch (IOException e) {}

                    outputFileUri = FileProvider.getUriForFile(context, "com.br.mytasksapp.ui.fragment.GeneralDataFragment", newfile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, CAMERA_PHOTO);
                }
            }
        });

        dialog.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(context).getContentResolver(), outputFileUri);
                profile_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            if(requestCode == 2 &&  resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(context).getContentResolver(), filePath);
                    profile_image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}