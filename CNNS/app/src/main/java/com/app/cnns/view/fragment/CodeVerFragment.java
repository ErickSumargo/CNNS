package com.app.cnns.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Internet;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.UserPresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.RegistrationResponse;
import com.app.cnns.view.activity.RegistrationActivity;
import com.app.cnns.view.custom.ProgressDialogCustom;

public class CodeVerFragment extends Fragment implements Response {
    private View view;

    private EditText codeField1, codeField2, codeField3, codeField4;
    private TextView timerLabel, resendLabel;
    private ProgressDialogCustom progressDialog;

    private UserPresenter userPresenter;

    public CountDownTimer timer;
    public boolean isTimerRunning = false;

    private String phone, code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_code_ver, container, false);
        initView();
        setEvent();

        return view;
    }

    private void initView() {
        codeField1 = view.findViewById(R.id.code_field_1);
        codeField2 = view.findViewById(R.id.code_field_2);
        codeField3 = view.findViewById(R.id.code_field_3);
        codeField4 = view.findViewById(R.id.code_field_4);

        timerLabel = view.findViewById(R.id.timer_label);
        resendLabel = view.findViewById(R.id.resend_label);

        userPresenter = new UserPresenter(getContext(), this);
    }

    private void setEvent() {
        resendLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResendCode();

                Toast.makeText(getActivity().getApplicationContext(), "Kode verifikasi telah dikirimkan", Toast.LENGTH_SHORT).show();
                setTimer();
            }
        });
    }

    private void setTimer() {
        timerLabel.setVisibility(View.VISIBLE);

        resendLabel.setAlpha(0.5f);
        resendLabel.setEnabled(false);
        resendLabel.setTextColor(getResources().getColor(R.color.colorDisabled));

        timer = new CountDownTimer(60500, 1000) {
            int secondsLeft = 60;

            @Override
            public void onTick(long l) {
                secondsLeft--;
                if (secondsLeft < 10) {
                    timerLabel.setText("00:0" + secondsLeft);
                } else {
                    timerLabel.setText("00:" + secondsLeft);
                }
                isTimerRunning = true;
            }

            @Override
            public void onFinish() {
                resetTimer();
            }
        }
                .start();
    }

    public void resetTimer() {
        timerLabel.setVisibility(View.GONE);

        resendLabel.setAlpha(1f);
        resendLabel.setEnabled(true);
        resendLabel.setTextColor(getResources().getColor(R.color.colorPrimary));

        isTimerRunning = false;
    }

    public void validate() {
        code = codeField1.getText().toString().trim() +
                codeField2.getText().toString().trim() +
                codeField3.getText().toString().trim() +
                codeField4.getText().toString().trim();
        if (code.length() == 0 || code.length() < 4) {
            showErrorDialog("Masukkan kode verifikasi yang dikirimkan ke anda melalui SMS");
        } else {
            attemptReg();
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void attemptReg() {
        progressDialog = new ProgressDialogCustom(getContext());
        progressDialog.setMessage("Verifikasi...");

        if (Internet.isConnected(getContext())) {
            phone = PhoneRegFragment.phone;
            userPresenter.verifyCode(phone, code);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptResendCode() {
        phone = PhoneRegFragment.phone;
        userPresenter.resendCode(phone);
    }

    public void clearCodeField() {
        codeField1.getText().clear();
        codeField2.getText().clear();
        codeField3.getText().clear();
        codeField4.getText().clear();
    }

    private void loadError(int error) {
        if (error == 0) {
            showErrorDialog("Kode verifikasi salah");
        }
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (base.getTag().equals(Constant.REQ_RESEND_CODE)) {
            RegistrationResponse r = (RegistrationResponse) base;
            Toast.makeText(getActivity().getApplicationContext(), r.getData().getCode(), Toast.LENGTH_SHORT).show();
        } else {
            if (base.isSuccess()) {
                ((RegistrationActivity) getActivity()).viewPager.setCurrentItem(2);
                ((RegistrationActivity) getActivity()).stepView.go(2, true);

                ((RegistrationActivity) getActivity()).next.setText("DAFTAR");

                if (isTimerRunning) {
                    timer.cancel();
                    resetTimer();
                }
                clearCodeField();
            } else {
                loadError(base.getError());
            }
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