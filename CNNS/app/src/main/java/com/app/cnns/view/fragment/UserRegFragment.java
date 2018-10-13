package com.app.cnns.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.app.cnns.view.activity.MainActivity;
import com.app.cnns.view.custom.ProgressDialogCustom;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Arrays;
import java.util.Calendar;

public class UserRegFragment extends Fragment implements Response, DatePickerDialog.OnDateSetListener {
    private View view;
    private EditText nameField, emailField, passwordField, birthdayField;
    private MaterialBetterSpinner genderField, cityField;
    private ProgressDialogCustom progressDialog;

    private UserPresenter userPresenter;

    private String phone, name, email, password, birthday = "2000/1/1", city = "Medan";
    private int gender = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_reg, container, false);

        initView();
        setEvent();

        return view;
    }

    private void initView() {
        nameField = view.findViewById(R.id.name_field);
        emailField = view.findViewById(R.id.email_field);
        passwordField = view.findViewById(R.id.password_field);
        birthdayField = view.findViewById(R.id.birthday_field);
        genderField = view.findViewById(R.id.gender_field);
        cityField = view.findViewById(R.id.city_field);

        birthdayField.setText("01/01/2000");

        genderField.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                new String[]{"Laki-laki", "Perempuan"}
        ));
        genderField.setText("Laki-laki");

        cityField.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                new String[]{"Medan", "Aceh", "Jakarta", "Bandung", "Surabaya", "Semarang", "Bali"}
        ));
        cityField.setText("Medan");

        userPresenter = new UserPresenter(getContext(), this);
    }

    private void setEvent() {
        birthdayField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            UserRegFragment.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getActivity().getFragmentManager(), "");
                }
                return false;
            }
        });

        genderField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = position;
            }
        });

        cityField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = parent.getItemAtPosition(position).toString();
            }
        });
    }

    public void validate() {
        name = nameField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        if (name.length() == 0) {
            showErrorDialog("Masukkan nama anda");
        } else if (!name.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}")) {
            showErrorDialog("Nama tidak dapat mengandung karakter khusus");
        } else if (email.length() == 0) {
            showErrorDialog("Masukkan email anda");
        } else if (!email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            showErrorDialog("Periksa kembali format email anda");
        } else if (password.length() == 0) {
            showErrorDialog("Masukkan password anda");
        } else if (password.length() < 6) {
            showErrorDialog("Password minimal 6 karakter");
        } else {
            attemptReg();
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void attemptReg() {
        progressDialog = new ProgressDialogCustom(getContext());
        progressDialog.setMessage("Membuat akun...");

        if (Internet.isConnected(getContext())) {
            phone = PhoneRegFragment.phone;
            userPresenter.registerUser(phone, name, email, password, birthday, gender, city);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearDataField() {
        nameField.getText().clear();
        emailField.getText().clear();
        passwordField.getText().clear();
        birthdayField.setText("2000/1/1");
        genderField.setText("Laki-laki");
        cityField.setText("Medan");

        birthday = "2000/1/1";
        gender = 0;
        city = "Medan";
    }

    private void loadError(int error) {
        if (error == 0) {
        } else if (error == 1) {
            showErrorDialog("Email telah terdaftar");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        birthday = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
        birthdayField.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        UserResponse r = (UserResponse) base;
        if (r.isSuccess()) {
            Session.with(getContext()).saveUser(r.getData().getUser());
            Session.with(getContext()).saveToken(r.getData().getToken());
            Session.with(getContext()).startServices();
            Session.with(getContext()).saveLogin();

            Intent intent = new Intent(getContext(), MainActivity.class);
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
        Toast.makeText(getActivity().getApplicationContext(), "Gangguan koneksi, silahkan dicoba lagi", Toast.LENGTH_SHORT).show();
    }
}