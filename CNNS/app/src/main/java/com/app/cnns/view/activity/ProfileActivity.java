package com.app.cnns.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Internet;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.User;
import com.app.cnns.server.presenter.UserPresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.UserResponse;
import com.app.cnns.view.custom.ProgressDialogCustom;
import com.app.cnns.view.custom.imagepicker.PickerBuilder;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private CircleImageView photo;
    private TextView name, email;
    private MaterialBetterSpinner genderField;
    private EditText phoneField, addressField;
    private TextView thumbUp, thumbDown;
    private TextView bookmark, history, draft;
    private Button save;

    private ProgressDialogCustom progressDialog;

    private UserPresenter userPresenter;
    private User user;

    private Uri imgPhoto;
    private int gender;
    private String phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photo = findViewById(R.id.photo);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        genderField = findViewById(R.id.gender_field);
        phoneField = findViewById(R.id.phone_field);
        addressField = findViewById(R.id.address_field);

        thumbUp = findViewById(R.id.thumb_up);
        thumbDown = findViewById(R.id.thumb_down);

        bookmark = findViewById(R.id.bookmark);
        history = findViewById(R.id.history);
        draft = findViewById(R.id.draft);

        save = findViewById(R.id.save);

        user = Session.with(this).getUser();
        Picasso.with(this).load(Utils.with(this).getURLMediaImage(user.getPhoto(), user.getType()))
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(photo);
        name.setText(user.getName());
        email.setText(user.getEmail());

        genderField.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Laki-laki", "Perempuan"}
        ));
        genderField.setText(genderField.getAdapter().getItem(user.getGender()).toString());
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());

        thumbUp.setText(String.valueOf(user.getUps()));
        thumbDown.setText(String.valueOf(user.getDowns()));

        userPresenter = new UserPresenter(getApplicationContext(), this);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerBuilder pickerBuilder = new PickerBuilder(ProfileActivity.this, PickerBuilder.SELECT_FROM_GALLERY);
                pickerBuilder.setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        imgPhoto = imageUri;

                        photo.setImageURI(imageUri);
                    }
                })
                        .withTimeStamp(false)
                        .setCropScreenColor(getResources().getColor(R.color.colorPrimary))
                        .start();
            }
        });

        genderField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = position;
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DraftActivity.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        phone = phoneField.getText().toString().trim();
        address = addressField.getText().toString().trim();

        if (phone.length() == 0) {
            showErrorDialog("Masukkan No. HP anda");
        } else if (phone.charAt(0) != '0' || phone.length() < 10) {
            showErrorDialog("Periksa kembali format No. HP anda");
        } else {
            attemptSave();
        }
    }

    private void attemptSave() {
        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setMessage("Menyimpan...");

        if (Internet.isConnected(this)) {
            userPresenter.updateProfile(imgPhoto, gender, phone, address);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadError(int error) {
        if (error == 0) {
            showErrorDialog("Maaf, No. HP telah terdaftar sebelumnya");
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        UserResponse r = (UserResponse) base;
        if (r.isSuccess()) {
            Session.with(this).saveUser(r.getData().getUser());
            Session.with(this).saveToken(r.getData().getToken());

            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            loadError(r.getError());
        }
    }

    @Override
    public void onFailure(String message) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Toast.makeText(this, "Gangguan koneksi, silahkan dicoba lagi", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userPresenter.disposables.clear();
    }
}