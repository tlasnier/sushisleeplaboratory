package fr.wyniwyg.sushisleeplaboratory.utils;

import fr.wyniwyg.sushisleeplaboratory.exception.BadAuthenticationException;
import fr.wyniwyg.sushisleeplaboratory.exception.RegisterException;

/**
 * Created by t.lasnier on 27/08/13.
 */
public interface UserManagement {
    public void login(String login, String password) throws BadAuthenticationException;

    public void register(String lastname, String firstname, String login, String password, String gender, long birth) throws RegisterException;

}
