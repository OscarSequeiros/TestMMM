package com.osequeiros.testmmm.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.nav.NavigationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by osequeiros on 1/25/18.
 */

public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.edit_login_user) EditText mEditUser;
    @BindView(R.id.edit_login_password) EditText mEditPassword;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter = new LoginPresenterImp(this);
    }

    @OnClick(R.id.btn_login_singin)
    public void doLogin() {
        presenter.doLogin(mEditUser.getText().toString(), mEditPassword.getText().toString());
    }

    @Override
    public void showErrorUser() {
        Snackbar.make(mEditUser, "Asigne su usuario.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showErrorPassword() {
        Snackbar.make(mEditUser, "Asigne su contraseña.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void launchNextActivity() {
        Intent intent = new Intent().setClass(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}