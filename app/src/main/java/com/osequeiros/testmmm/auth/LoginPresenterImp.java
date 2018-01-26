package com.osequeiros.testmmm.auth;

/**
 * Created by osequeiros on 1/25/18.
 */

public class LoginPresenterImp implements LoginPresenter {

    private LoginView view;

    LoginPresenterImp(LoginView view) {
        this.view = view;
    }

    @Override
    public void doLogin(String user, String password) {
        if (user == null || user.trim().isEmpty()) {
            view.showErrorUser();
        } else if (password == null || password.trim().isEmpty()) {
            view.showErrorPassword();
        } else {
            view.launchNextActivity();
        }
    }
}
