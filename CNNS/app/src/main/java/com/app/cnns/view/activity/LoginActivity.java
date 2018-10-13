package com.app.cnns.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Internet;
import com.app.cnns.helper.Session;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.UserPresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.UserResponse;
import com.app.cnns.view.custom.ProgressDialogCustom;

public class LoginActivity extends AppCompatActivity implements Response {
    private EditText emailField, passwordField;
    private Button login, register;

    private ProgressDialogCustom progressDialog;

    private UserPresenter userPresenter;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setEvent();
    }

    private void initView() {
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        userPresenter = new UserPresenter(getApplicationContext(), this);
    }

    private void setEvent() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validate() {
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        if (email.length() == 0) {
            showErrorDialog("Masukkan email anda");
        } else if (!email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            showErrorDialog("Periksa kembali format email anda");
        } else if (password.length() == 0) {
            showErrorDialog("Masukkan password anda");
        } else if (password.length() < 6) {
            showErrorDialog("Password minimal 6 karakter");
        } else {
            attemptLogin();
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

    private void attemptLogin() {
        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setMessage("Masuk...");

        if (Internet.isConnected(this)) {
            userPresenter.login(email, password);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadError(int error) {
        if (error == 0) {
            showErrorDialog("Email tidak terdaftar");
        } else if (error == 1) {
            showErrorDialog("Password tidak cocok");
        }
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
            Session.with(this).startServices();
            Session.with(this).saveLogin();

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