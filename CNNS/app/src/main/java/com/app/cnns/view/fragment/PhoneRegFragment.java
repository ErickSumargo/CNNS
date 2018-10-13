package com.app.cnns.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Internet;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.UserPresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.RegistrationResponse;
import com.app.cnns.view.activity.RegistrationActivity;
import com.app.cnns.view.custom.ProgressDialogCustom;

public class PhoneRegFragment extends Fragment implements Response {
    private View view;
    private EditText phoneField;
    private ProgressDialogCustom progressDialog;

    private UserPresenter userPresenter;

    public static String phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phone_reg, container, false);
        initView();

        return view;
    }

    private void initView() {
        phoneField = view.findViewById(R.id.phone_field);

        userPresenter = new UserPresenter(getContext(), this);
    }

    public void validate() {
        phone = phoneField.getText().toString().trim();
        if (phone.length() == 0) {
            showErrorDialog("Masukkan No. HP anda");
        } else if (phone.charAt(0) != '0' || phone.length() < 10) {
            showErrorDialog("Periksa kembali format No. HP anda");
        } else {
            showConfirmationDialog();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(phone)
                .setMessage("Kode verifikasi akan dikirimkan pada nomor ini melalui SMS")
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attemptReg();
                    }
                })
                .show();
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
        progressDialog.setMessage("Memeriksa...");

        if (Internet.isConnected(getContext())) {
            userPresenter.registerPhone(phone);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadError(int error) {
        if (error == 0) {
            showErrorDialog("Maaf, No. HP telah terdaftar sebelumnya");
        }
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        RegistrationResponse r = (RegistrationResponse) base;
        if (r.isSuccess()) {
            if (!r.getData().isSkipped()) {
                ((RegistrationActivity) getActivity()).viewPager.setCurrentItem(1);
                ((RegistrationActivity) getActivity()).stepView.go(1, true);

                ((RegistrationActivity) getActivity()).previous.setText("KEMBALI");
                ((RegistrationActivity) getActivity()).previous.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity().getApplicationContext(), r.getData().getCode(), Toast.LENGTH_SHORT).show();
            } else {
                ((RegistrationActivity) getActivity()).viewPager.setCurrentItem(2);
                ((RegistrationActivity) getActivity()).stepView.go(2, true);

                ((RegistrationActivity) getActivity()).previous.setText("KEMBALI");
                ((RegistrationActivity) getActivity()).previous.setVisibility(View.VISIBLE);

                ((RegistrationActivity) getActivity()).next.setText("DAFTAR");
            }
            phoneField.getText().clear();
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